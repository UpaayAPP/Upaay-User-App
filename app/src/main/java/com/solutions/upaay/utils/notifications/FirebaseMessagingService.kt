package com.solutions.upaay.utils.notifications

import android.Manifest
import android.app.PendingIntent
import android.content.Intent
import android.content.pm.PackageManager
import android.media.RingtoneManager
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.solutions.upaay.MainActivity
import com.solutions.upaay.R

class FirebaseMessagingService : FirebaseMessagingService() {

    override fun onNewToken(token: String) {
        super.onNewToken(token)
//        Log.d("FCM", "New FCM token: $token")
        saveTokenToFirestore(token)
    }

    private fun saveTokenToFirestore(token: String) {
        val astrologerId = FirebaseAuth.getInstance().currentUser?.uid ?: return

        FirebaseFirestore.getInstance()
            .collection("users")
            .document(astrologerId)
            .update("fcmToken", token)
            .addOnSuccessListener {
                Log.d("FCM", "Token saved successfully")
            }
            .addOnFailureListener {
                Log.e("FCM", "Failed to save token: ${it.message}")
            }
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        val type = remoteMessage.data["type"]

        when (type) {
            "incoming_call" -> handleIncomingCall(remoteMessage.data)
            "new_message" -> handleNewMessage(remoteMessage.data)
        }
    }

    private fun handleIncomingCall(data: Map<String, String>) {
        val context = applicationContext
        val callerName = data["callerName"] ?: "Incoming Call"
        val callId = data["callId"] ?: return
        val userId = data["userId"] ?: return
        val astrologerId = data["astrologerId"] ?: return

        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            putExtra("navigateTo", "recentAudioCall")
            putExtra("callId", callId)
            putExtra("userId", userId)
            putExtra("astrologerId", astrologerId)
        }

        val pendingIntent = PendingIntent.getActivity(
            context, 0, intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val notification = NotificationCompat.Builder(this, "calls_channel")
            .setContentTitle(callerName)
            .setContentText("Incoming call from $callerName")
            .setSmallIcon(R.drawable.ic_call) // your icon
            .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
            == PackageManager.PERMISSION_GRANTED
        ) {
            NotificationManagerCompat.from(this).notify(1001, notification)
        }
    }

    private fun handleNewMessage(data: Map<String, String>) {
        val context = applicationContext
        val senderId = data["senderId"] ?: return
        val senderName = data["senderName"] ?: "New Message"
        val text = data["text"] ?: "📷 Photo"

        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            putExtra("navigateTo", "chatScreen")
            putExtra("senderId", senderId)
        }

        val pendingIntent = PendingIntent.getActivity(
            context, 0, intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val notification = NotificationCompat.Builder(this, "chats_channel")
            .setContentTitle(senderName)
            .setContentText(text)
            .setSmallIcon(R.drawable.ic_chat) // your icon
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
            == PackageManager.PERMISSION_GRANTED
        ) {
            NotificationManagerCompat.from(this).notify(1001, notification)
        }
    }
}