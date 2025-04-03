package com.solutions.upaay.utils.database.update

import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import com.google.firebase.firestore.*
import com.google.firebase.Timestamp

suspend fun processChatTransaction(
    firestore: FirebaseFirestore,
    userId: String,
    astrologerId: String,
    chatId: String,
    chatMessageRef: String
): Boolean {
    return suspendCancellableCoroutine { continuation ->
        firestore.runTransaction { transaction ->
            val userRef = firestore.collection("users").document(userId)
            val astrologerRef = firestore.collection("astrologers").document(astrologerId)
            val chatSummaryRef = firestore.collection("transactions")
                .document("chats").collection(chatId).document("summary")

            // 🔹 Each message gets its own history entry (auto-generated document ID)
            val historyRef = firestore.collection("transactions")
                .document("chats").collection(chatId).document()

            val commissionRef = firestore.collection("utils").document("commissions")

            // 🔹 Fetch user balance
            val userSnapshot = transaction.get(userRef)
            val userBalance = userSnapshot.getDouble("balance") ?: 0.0

            // 🔹 Fetch astrologer's rate per message
            val astrologerSnapshot = transaction.get(astrologerRef)
            val ratePerMessage = astrologerSnapshot.getDouble("ratePerMessage") ?: 0.0

            if (ratePerMessage == 0.0) {
                throw Exception("Rate per message is not set for astrologer")
            }

            if (userBalance < ratePerMessage) {
                throw Exception("Insufficient balance")
            }

            // 🔹 Fetch commission for chat from Firestore
            val commissionSnapshot = transaction.get(commissionRef)
            val chatCommissionPercentage = commissionSnapshot.getDouble("chat_commission") ?: 20.0

            // 🔹 Platform fee calculation
            val platformFee = ratePerMessage * (chatCommissionPercentage / 100)
            val astrologerCut = ratePerMessage - platformFee

            // 🔹 Efficiently update user's balance
            transaction.update(userRef, "balance", FieldValue.increment(-ratePerMessage))

            // 🔹 Credit astrologer's earnings
            transaction.update(astrologerRef, "earnings", FieldValue.increment(astrologerCut))

            // 🔹 Update chat summary (total deduction & earnings)
            transaction.set(
                chatSummaryRef,
                mapOf(
                    "chatId" to chatId,
                    "userId" to userId,
                    "astrologerId" to astrologerId,
                    "timestamp" to Timestamp.now()
                ),
                SetOptions.merge()
            )
            transaction.update(chatSummaryRef, mapOf(
                "totalUserDeduction" to FieldValue.increment(ratePerMessage),
                "totalAstrologerEarnings" to FieldValue.increment(astrologerCut)
            ))

            // 🔹 Store each message's transaction history
            transaction.set(
                historyRef,
                mapOf(
                    "timestamp" to Timestamp.now(),
                    "userDeduction" to ratePerMessage,
                    "astrologerEarnings" to astrologerCut,
                    "chatMessageRef" to chatMessageRef,
                    "createdBy" to "Astrologer"
                )
            )
        }.addOnSuccessListener {
            continuation.resume(true)
        }.addOnFailureListener { e ->
            continuation.resumeWithException(e)
        }
    }
}