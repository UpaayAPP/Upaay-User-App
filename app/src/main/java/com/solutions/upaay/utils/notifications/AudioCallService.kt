package com.solutions.upaay.utils.notifications

import android.content.Context
import android.media.Ringtone
import android.media.RingtoneManager
import android.net.Uri
import android.os.Vibrator
import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

object UserCallNotificationManager {
    private val outgoingSuppressors = mutableStateListOf<String>()
    private val ongoingSuppressors = mutableStateListOf<String>()

    var isShowing = mutableStateOf(false)
    var currentCallData = mutableStateOf<Map<String, Any>?>(null)

    val isOutgoingSuppressed: Boolean get() = outgoingSuppressors.isNotEmpty()
    val isOngoingSuppressed: Boolean get() = ongoingSuppressors.isNotEmpty()

    fun show(callData: Map<String, Any>) {
        currentCallData.value = callData
        isShowing.value = true
    }

    fun dismiss() {
        currentCallData.value = null
        isShowing.value = false
        outgoingSuppressors.clear()
        ongoingSuppressors.clear()
    }

    fun suppressOutgoing(screen: String) {
        if (screen !in outgoingSuppressors) outgoingSuppressors.add(screen)
    }

    fun unsuppressOutgoing(screen: String) {
        outgoingSuppressors.remove(screen)
    }

    fun suppressOngoing(screen: String) {
        if (screen !in ongoingSuppressors) ongoingSuppressors.add(screen)
    }

    fun unsuppressOngoing(screen: String) {
        ongoingSuppressors.remove(screen)
    }
}

object UserAudioCallService {
    private var listenerAttached = false

    // Global state to reflect current calls
    val ongoingCallList = mutableStateListOf<Map<String, Any>>()

    fun start(context: Context) {
        val firestore = FirebaseFirestore.getInstance()
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return

        if (listenerAttached) return

        firestore.collection("users")
            .document(userId)
            .collection("audiocalls")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    return@addSnapshotListener
                }

                val calls = snapshot?.documents
                    ?.filter { it.getString("status") in listOf("connecting", "ongoing") }
                    ?.mapNotNull { it.data }
                    ?: emptyList()

                // Update observable list
                ongoingCallList.clear()
                ongoingCallList.addAll(calls)

                // Show first relevant banner
                val firstCall = calls.firstOrNull()
                if (firstCall != null) {
                    if (!UserCallNotificationManager.isShowing.value) {
                        UserCallNotificationManager.show(firstCall)
                    }
                } else {
                    if (UserCallNotificationManager.isShowing.value) {
                        UserCallNotificationManager.dismiss()
                    }
                }
            }

        listenerAttached = true
    }
}

