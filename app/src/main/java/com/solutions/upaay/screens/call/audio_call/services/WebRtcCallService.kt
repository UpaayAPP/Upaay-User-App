package com.solutions.upaay.screens.call.audio_call.services

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.media.AudioManager
import android.media.MediaPlayer
import android.os.Binder
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.Timestamp
import com.google.firebase.firestore.ListenerRegistration
import kotlinx.coroutines.*
import kotlinx.coroutines.tasks.await
import org.webrtc.AudioTrack
import org.webrtc.IceCandidate
import org.webrtc.PeerConnection
import org.webrtc.PeerConnectionFactory
import org.webrtc.RtpTransceiver
import org.webrtc.SdpObserver
import org.webrtc.SessionDescription
import com.solutions.upaay.R // replace with your actual R import for icons
import com.solutions.upaay.screens.call.audio_call.WebRTCManager
import com.solutions.upaay.utils.notifications.UserCallNotificationManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class WebRtcCallService : Service() {

    private lateinit var webRTCManager: WebRTCManager
    private var peerConnection: PeerConnection? = null

    private lateinit var callId: String
    private lateinit var userId: String
    private lateinit var astrologerId: String

    private val binder = LocalBinder()
    private var balanceListener: ListenerRegistration? = null
    private var callStatusListener: ListenerRegistration? = null

    var callDurationSeconds = 0
    private var durationTimerJob: Job? = null
    private val _durationFlow = MutableStateFlow("00:00")
    val durationFlow: StateFlow<String> get() = _durationFlow

    private var mediaPlayer: MediaPlayer? = null

    inner class LocalBinder : Binder() {
        fun getService(): WebRtcCallService = this@WebRtcCallService
    }

    override fun onBind(intent: Intent?): IBinder = binder

    override fun onCreate() {
        super.onCreate()
        webRTCManager = WebRTCManager(applicationContext)
        webRTCManager.initialize()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        callId = intent?.getStringExtra("callId") ?: return START_NOT_STICKY
        userId = intent.getStringExtra("userId") ?: return START_NOT_STICKY
        astrologerId = intent.getStringExtra("astrologerId") ?: return START_NOT_STICKY

        startForegroundNotification()
        startCall()
        listenToUserBalance()
        listenToCallStatus()
        startRinging()

        return START_STICKY
    }

    private fun startCall() {
        val iceServers = listOf(
            PeerConnection.IceServer.builder("stun:stun.l.google.com:19302").createIceServer()
        )

        peerConnection = webRTCManager.createPeerConnection(iceServers) { candidate ->
            webRTCManager.sendIceCandidate(callId, candidate)
        }

        peerConnection?.let { pc ->
            val firestore = FirebaseFirestore.getInstance()
            val callRef = firestore.collection("calls").document(callId)

            CoroutineScope(Dispatchers.IO).launch {
                val snapshot = callRef.get().await()
                val existingOffer = snapshot.getString("offer")
                val currentStatus = snapshot.getString("status")

                withContext(Dispatchers.Main) {
                    if (existingOffer == null || currentStatus == "connecting") {
                        webRTCManager.createOffer(pc, callId, userId, astrologerId)
                    } else {
                        val offerSDP = SessionDescription(SessionDescription.Type.OFFER, existingOffer)
                        pc.setLocalDescription(object : SdpObserver {
                            override fun onSetSuccess() {}
                            override fun onSetFailure(error: String?) {}
                            override fun onCreateSuccess(p0: SessionDescription?) {}
                            override fun onCreateFailure(p0: String?) {}
                        }, offerSDP)
                    }

                    webRTCManager.listenForAnswer(pc, callId)
                    webRTCManager.listenForRemoteIceCandidates(pc, callId)
                }
            }
        }
    }

    private fun startRinging() {
        mediaPlayer = MediaPlayer.create(this, R.raw.outgoing_call_tone)
        mediaPlayer?.isLooping = true
        mediaPlayer?.start()
    }

    private fun stopRinging() {
        mediaPlayer?.stop()
        mediaPlayer?.release()
        mediaPlayer = null
    }

    private fun listenToUserBalance() {
        val firestore = FirebaseFirestore.getInstance()
        val userRef = firestore.collection("users").document(userId)
        val astrologerRef = firestore.collection("astrologers").document(astrologerId)

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val astroSnapshot = astrologerRef.get().await()
                val ratePerMinute = astroSnapshot.getDouble("audioRatePerMinute") ?: 10.0

                balanceListener = userRef.addSnapshotListener { snapshot, _ ->
                    val balance = snapshot?.getDouble("balance") ?: 0.0
                    if (balance < ratePerMinute) {
                        firestore.collection("calls").document(callId)
                            .update("status", "ended")

                        UserCallNotificationManager.dismiss()

                        stopSelf()
                    }
                }
            } catch (e: Exception) {
                Log.e("WebRtcCallService", "Error fetching astrologer rate: ${e.message}")
            }
        }
    }

    private fun listenToCallStatus() {
        val firestore = FirebaseFirestore.getInstance()
        val callRef = firestore.collection("calls").document(callId)

        callStatusListener = callRef.addSnapshotListener { snapshot, _ ->
            val status = snapshot?.getString("status") ?: "ended"
            when (status) {
                "ongoing" -> {
                    stopRinging()
                    startCallTimer() // ✅ Start the timer when picked
                }
                "ended" -> {
                    stopRinging()
                    stopCallTimer()
                    UserCallNotificationManager.dismiss()

                    stopSelf()
                }
            }
        }
    }

    private fun startForegroundNotification() {
        val channelId = "webrtc_call_channel"
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "WebRTC Audio Call",
                NotificationManager.IMPORTANCE_LOW
            )
            getSystemService(NotificationManager::class.java).createNotificationChannel(channel)
        }

        val notification = NotificationCompat.Builder(this, channelId)
            .setContentTitle("In Call")
            .setContentText("Call is ongoing")
            .setSmallIcon(R.drawable.ic_call)
            .build()

        startForeground(1, notification)
    }

    private fun startCallTimer() {
        durationTimerJob?.cancel()
        callDurationSeconds = 0

        durationTimerJob = CoroutineScope(Dispatchers.Default).launch {
            while (isActive) {
                delay(1000)
                callDurationSeconds++
                val minutes = callDurationSeconds / 60
                val seconds = callDurationSeconds % 60
                _durationFlow.value = String.format("%02d:%02d", minutes, seconds)
            }
        }
    }

    private fun stopCallTimer() {
        durationTimerJob?.cancel()
        durationTimerJob = null
        callDurationSeconds = 0
        _durationFlow.value = "00:00"
    }

    fun setMute(muted: Boolean) {
        webRTCManager.localAudioTrack?.setEnabled(!muted)
    }

    fun setSpeaker(on: Boolean) {
        val audioManager = getSystemService(Context.AUDIO_SERVICE) as AudioManager
        audioManager.isSpeakerphoneOn = on
    }

    override fun onDestroy() {
        stopRinging()
        stopCallTimer()
        UserCallNotificationManager.dismiss()
        peerConnection?.close()
        peerConnection = null
        balanceListener?.remove()
        callStatusListener?.remove()
        super.onDestroy()
    }

    companion object {
        fun start(context: Context, callId: String, userId: String, astrologerId: String) {
            val intent = Intent(context, WebRtcCallService::class.java).apply {
                putExtra("callId", callId)
                putExtra("userId", userId)
                putExtra("astrologerId", astrologerId)
            }
            ContextCompat.startForegroundService(context, intent)
        }

        fun stop(context: Context) {
            context.stopService(Intent(context, WebRtcCallService::class.java))
        }
    }
}


//class WebRtcCallService : Service() {
//
//    private lateinit var webRTCManager: WebRTCManager
//    private var peerConnection: PeerConnection? = null
//
//    private lateinit var callId: String
//    private lateinit var userId: String
//    private lateinit var astrologerId: String
//
//    private val binder = LocalBinder()
//
//    inner class LocalBinder : Binder() {
//        fun getService(): WebRtcCallService = this@WebRtcCallService
//    }
//
//    override fun onBind(intent: Intent?): IBinder = binder
//
//    override fun onCreate() {
//        super.onCreate()
//        webRTCManager = WebRTCManager(applicationContext)
//        webRTCManager.initialize()
//    }
//
//    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
//        callId = intent?.getStringExtra("callId") ?: return START_NOT_STICKY
//        userId = intent.getStringExtra("userId") ?: return START_NOT_STICKY
//        astrologerId = intent.getStringExtra("astrologerId") ?: return START_NOT_STICKY
//
//        Log.d("WebRTC-Service", "onStartCommand: Starting call with callId=$callId")
//
//        startForegroundNotification()
//        startCall()
//
//        return START_STICKY
//    }
//
//    private fun startCall() {
//        val iceServers = listOf(
//            PeerConnection.IceServer.builder("stun:stun.l.google.com:19302").createIceServer()
//        )
//
//        peerConnection = webRTCManager.createPeerConnection(iceServers) { candidate ->
//            webRTCManager.sendIceCandidate(callId, candidate)
//        }
//
//        peerConnection?.let { pc ->
//            val firestore = FirebaseFirestore.getInstance()
//            val callRef = firestore.collection("calls").document(callId)
//
//            CoroutineScope(Dispatchers.IO).launch {
//                val snapshot = callRef.get().await()
//                val existingOffer = snapshot.getString("offer")
//                val currentStatus = snapshot.getString("status")
//
//                withContext(Dispatchers.Main) {
//                    if (existingOffer == null || currentStatus == "connecting") {
//                        Log.d("WebRTC-Service", "Creating new offer...")
//                        webRTCManager.createOffer(pc, callId, userId, astrologerId)
//                    } else {
//                        val offerSDP = SessionDescription(SessionDescription.Type.OFFER, existingOffer)
//                        pc.setLocalDescription(object : SdpObserver {
//                            override fun onSetSuccess() {
//                                Log.d("WebRTC-Service", "Re-set local description from existing offer")
//                            }
//                            override fun onSetFailure(error: String?) {
//                                Log.e("WebRTC-Service", "Failed to set local description: $error")
//                            }
//                            override fun onCreateSuccess(p0: SessionDescription?) {}
//                            override fun onCreateFailure(p0: String?) {}
//                        }, offerSDP)
//                    }
//
//                    webRTCManager.listenForAnswer(pc, callId)
//                    webRTCManager.listenForRemoteIceCandidates(pc, callId)
//                }
//            }
//        } ?: run {
//            Log.e("WebRTC-Service", "PeerConnection is null")
//        }
//    }
//
//    fun setMute(muted: Boolean) {
//        webRTCManager.localAudioTrack?.setEnabled(!muted)
//    }
//
//    fun setSpeaker(on: Boolean) {
//        val audioManager = getSystemService(Context.AUDIO_SERVICE) as AudioManager
//        audioManager.isSpeakerphoneOn = on
//    }
//
//    private fun startForegroundNotification() {
//        val channelId = "webrtc_call_channel"
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            val channel = NotificationChannel(
//                channelId,
//                "WebRTC Audio Call",
//                NotificationManager.IMPORTANCE_LOW
//            )
//            val manager = getSystemService(NotificationManager::class.java)
//            manager.createNotificationChannel(channel)
//        }
//
//        val notification = NotificationCompat.Builder(this, channelId)
//            .setContentTitle("In Call")
//            .setContentText("Call is ongoing")
//            .setSmallIcon(R.drawable.ic_call)
//            .build()
//
//        startForeground(1, notification)
//    }
//
//    fun endCall() {
//        peerConnection?.close()
//        peerConnection = null
//        stopSelf()
//    }
//
//    override fun onDestroy() {
//        peerConnection?.close()
//        peerConnection = null
//        super.onDestroy()
//    }
//
//    companion object {
//        fun start(context: Context, callId: String, userId: String, astrologerId: String) {
//            val intent = Intent(context, WebRtcCallService::class.java).apply {
//                putExtra("callId", callId)
//                putExtra("userId", userId)
//                putExtra("astrologerId", astrologerId)
//            }
//            ContextCompat.startForegroundService(context, intent)
//        }
//
//        fun stop(context: Context) {
//            context.stopService(Intent(context, WebRtcCallService::class.java))
//        }
//    }
//}