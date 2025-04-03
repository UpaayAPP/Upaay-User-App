package com.solutions.upaay.utils.database.retrieve

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.solutions.upaay.screens.home.components.astrologer.AstrologerRepository
import com.solutions.upaay.screens.home.components.astrologer.components.cards.Astrologer
import com.solutions.upaay.utils.loading.LoadingStateManager

fun fetchAstrologers(
    existingUids: List<String>,
    onResult: (List<Astrologer>) -> Unit,
    onError: (Exception) -> Unit
) {
    LoadingStateManager.showLoading()
    val db = FirebaseFirestore.getInstance()
    val query = if (existingUids.isNotEmpty()) {
        db.collection("astrologers")
            .whereNotIn("uid", existingUids)
            .limit(10)
    } else {
        db.collection("astrologers")
            .limit(10)
    }

    query.get()
        .addOnSuccessListener { documents ->
            LoadingStateManager.hideLoading()
            val astrologers = documents.mapNotNull { doc ->
                val isInCall = doc.getBoolean("isInCall") ?: false
                doc.toObject(Astrologer::class.java).copy(isInCall = isInCall)
            }
            onResult(astrologers)
        }
        .addOnFailureListener { exception ->
            LoadingStateManager.hideLoading()
            onError(exception)
        }
}


fun startRealtimeIsInCallUpdates() {
    val firestore = FirebaseFirestore.getInstance()
    firestore.collection("astrologers")
        .addSnapshotListener { snapshot, error ->
            if (error != null || snapshot == null) return@addSnapshotListener

            snapshot.documentChanges.forEach { change ->
                val doc = change.document
                val uid = doc.id

                val updatedIsInCall = doc.getBoolean("isInCall") ?: false
                val audioRate = doc.getDouble("audioRatePerMinute") ?: 0.0
                val messageRate = doc.getDouble("ratePerMessage") ?: 0.0
//                val videoRate = doc.getDouble("videoRatePerMinute") ?: 0.0

                val index = AstrologerRepository.astrologers.indexOfFirst { it.uid == uid }
                if (index != -1) {
                    val updated = AstrologerRepository.astrologers.toMutableList()
                    val existing = updated[index]

                    updated[index] = existing.copy(
                        isInCall = updatedIsInCall,
                        audioRatePerMinute = audioRate,
                        ratePerMessage = messageRate,
//                        videoRatePerMinute = videoRate
                    )

                    AstrologerRepository.astrologers = updated
                }
            }
        }
}


fun fetchAstrologerById(astrologerId: String, onResult: (Astrologer?) -> Unit) {
    val db = FirebaseFirestore.getInstance()
    LoadingStateManager.showLoading()
    db.collection("astrologers").document(astrologerId)
        .get()
        .addOnSuccessListener { document ->
            LoadingStateManager.hideLoading()
            if (document.exists()) {
                val astrologer = document.toObject(Astrologer::class.java)
                onResult(astrologer)
            } else {
                onResult(null)
            }
        }
        .addOnFailureListener {
            LoadingStateManager.hideLoading()
            Log.e("Firestore", "Failed to fetch astrologer", it)
            onResult(null)
        }
}
