package com.solutions.upaay.screens.call.audio_call
//
//import android.Manifest
//import android.app.Activity
//import android.content.ComponentName
//import android.content.Context
//import android.content.Intent
//import android.content.ServiceConnection
//import android.content.pm.PackageManager
//import android.media.AudioAttributes
//import android.media.AudioFocusRequest
//import android.media.AudioManager
//import android.os.Build
//import android.os.IBinder
//import android.util.Log
//import androidx.compose.foundation.background
//import androidx.compose.foundation.border
//import androidx.compose.foundation.layout.*
//import androidx.compose.foundation.shape.CircleShape
//import androidx.compose.material.icons.Icons
//import androidx.compose.material.icons.filled.CallEnd
//import androidx.compose.material.icons.filled.Mic
//import androidx.compose.material.icons.filled.MicOff
//import androidx.compose.material.icons.filled.VolumeMute
//import androidx.compose.material.icons.filled.VolumeUp
//import androidx.compose.material3.*
//import androidx.compose.runtime.*
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.draw.alpha
//import androidx.compose.ui.draw.clip
//import androidx.compose.ui.graphics.Color
//import androidx.compose.ui.graphics.vector.ImageVector
//import androidx.compose.ui.layout.ContentScale
//import androidx.compose.ui.platform.LocalContext
//import androidx.compose.ui.text.font.FontWeight
//import androidx.compose.ui.unit.dp
//import androidx.compose.ui.unit.sp
//import androidx.core.app.ActivityCompat
//import androidx.core.content.ContextCompat
//import coil.compose.AsyncImage
//import com.google.firebase.Timestamp
//import com.google.firebase.firestore.FirebaseFirestore
//import com.solutions.upaay.screens.call.audio_call.services.WebRtcCallService
//import com.solutions.upaay.utils.notifications.CallForegroundService
//import com.solutions.upaay.utils.notifications.UserCallNotificationManager
//import kotlinx.coroutines.*
//import kotlinx.coroutines.tasks.await
//import org.webrtc.*
//import org.webrtc.audio.JavaAudioDeviceModule
//
//class WebRTCManager(private val context: Context) {
//    //    private lateinit var peerConnection: PeerConnection
//    private lateinit var peerConnectionFactory: PeerConnectionFactory
//    private lateinit var audioSource: AudioSource
//    var localAudioTrack: AudioTrack? = null
//        private set
//
//    private val pendingIceCandidates = mutableListOf<IceCandidate>()
//    private var isRemoteDescriptionSet = false
//    private var isLocalDescriptionSet = false
//
//    fun initialize() {
//        PeerConnectionFactory.initialize(
//            PeerConnectionFactory.InitializationOptions.builder(context)
//                .setEnableInternalTracer(true)
//                .createInitializationOptions()
//        )
//
//        // ✅ Echo Canceler & Noise Suppressor
//        val audioDeviceModule = JavaAudioDeviceModule.builder(context).apply {
//            setUseHardwareAcousticEchoCanceler(true)
//            setUseHardwareNoiseSuppressor(true)
//        }.createAudioDeviceModule()
//
//        peerConnectionFactory = PeerConnectionFactory.builder()
//            .setAudioDeviceModule(audioDeviceModule)
//            .createPeerConnectionFactory()
//
//        val audioConstraints = MediaConstraints()
//        audioSource = peerConnectionFactory.createAudioSource(audioConstraints)
//        localAudioTrack = peerConnectionFactory.createAudioTrack("audio", audioSource)
//        localAudioTrack?.setEnabled(true)
//    }
//
//
//    fun createPeerConnection(
//        iceServers: List<PeerConnection.IceServer>,
//        onIceCandidate: (IceCandidate) -> Unit
//    ): PeerConnection? {
//        val rtcConfig = PeerConnection.RTCConfiguration(iceServers).apply {
//            sdpSemantics = PeerConnection.SdpSemantics.UNIFIED_PLAN // 🔥 Ensure Unified Plan
//        }
//
//        val peerConnection =
//            peerConnectionFactory.createPeerConnection(rtcConfig, object : PeerConnection.Observer {
//                override fun onIceCandidate(candidate: IceCandidate) {
//                    onIceCandidate(candidate)
//                }
//
//                override fun onTrack(transceiver: RtpTransceiver?) {
//
//                    transceiver?.receiver?.track()?.let { track ->
//                        if (track is AudioTrack) {
//                            track.setEnabled(true)  // 🔊 Enable Remote Audio
//                            track.setVolume(1.0)   // 🚀 Increase Volume for testing
//                        }
//                    }
//                }
//
//                override fun onIceConnectionChange(newState: PeerConnection.IceConnectionState?) {
//                    Log.d("WebRTC", "ICE connection state changed: $newState")
//                }
//
//                override fun onSignalingChange(newState: PeerConnection.SignalingState?) {}
//                override fun onIceConnectionReceivingChange(receiving: Boolean) {}
//                override fun onIceCandidatesRemoved(candidates: Array<out IceCandidate>?) {}
//                override fun onDataChannel(dataChannel: DataChannel?) {}
//                override fun onRenegotiationNeeded() {}
//                override fun onIceGatheringChange(newState: PeerConnection.IceGatheringState?) {}
//                override fun onRemoveStream(stream: MediaStream?) {}
//                override fun onAddStream(stream: MediaStream?) {}
//                override fun onConnectionChange(newState: PeerConnection.PeerConnectionState?) {}
//            }) ?: return null
//
//        // 🔹 Attach Local Audio Track (Use `addTrack()` instead of `addStream()`)
////        val audioConstraints = MediaConstraints()
////        val audioSource = peerConnectionFactory.createAudioSource(audioConstraints)
////        val audioTrack = peerConnectionFactory.createAudioTrack("audioTrack", audioSource)
////
////        // Attach audio track with media stream
////        peerConnection.addTrack(audioTrack)
//
//        localAudioTrack?.let {
//            peerConnection.addTrack(it)
//        }
//
//        return peerConnection
//    }
//
//    fun createOffer(
//        peerConnection: PeerConnection,
//        callId: String,
//        userId: String,
//        astrologerId: String
//    ) {
//        val firestore = FirebaseFirestore.getInstance()
//        val callRef = firestore.collection("calls").document(callId)
//        val astrologerCallRef = firestore.collection("astrologers").document(astrologerId)
//            .collection("audiocalls").document(callId)
//        val userCallRef = firestore.collection("users").document(userId)
//            .collection("audiocalls").document(callId)
//
//        val mediaConstraints = MediaConstraints().apply {
//            mandatory.add(MediaConstraints.KeyValuePair("OfferToReceiveAudio", "true"))
//        }
//
//        peerConnection.createOffer(object : SdpObserver {
//            override fun onCreateSuccess(sessionDescription: SessionDescription?) {
//                sessionDescription?.let {
//                    peerConnection.setLocalDescription(object : SdpObserver {
//                        override fun onSetSuccess() {
//                            Log.d("WebRTC", "Local description set successfully")
//                        }
//
//                        override fun onSetFailure(error: String?) {
//                            Log.e("WebRTC", "Failed to set local description: $error")
//                        }
//
//                        override fun onCreateSuccess(sessionDescription: SessionDescription?) {}
//                        override fun onCreateFailure(error: String?) {}
//                    }, it)
//
//                    val callData = mapOf(
//                        "offer" to it.description,
//                        "userId" to userId,
//                        "callId" to callId,
//                        "astrologerId" to astrologerId,
//                        "callStartTimestamp" to Timestamp.now(),
//                        "status" to "connecting"
//                    )
//
//                    // 🔹 Prevent Infinite Updates by Checking Status First
//                    callRef.get().addOnSuccessListener { document ->
//                        val currentStatus = document.getString("status") ?: "new"
//                        if (currentStatus != "connecting") {
//                            callRef.set(callData)
//                            astrologerCallRef.set(callData)
//                            userCallRef.set(callData)
//                        } else {
//                            Log.d("Firestore", "Call status already connecting, skipping update.")
//                        }
//                    }
//                }
//            }
//
//            override fun onCreateFailure(error: String?) {
//            }
//
//            override fun onSetSuccess() {}
//            override fun onSetFailure(error: String?) {}
//        }, mediaConstraints)
//    }
//
//    fun sendIceCandidate(callId: String, candidate: IceCandidate) {
//        val candidateMap = mapOf(
//            "sdpMid" to candidate.sdpMid,
//            "sdpMLineIndex" to candidate.sdpMLineIndex,
//            "candidate" to candidate.sdp
//        )
//        FirebaseFirestore.getInstance().collection("calls").document(callId)
//            .collection("iceCandidates").add(candidateMap)
//    }
//
//    fun listenForAnswer(peerConnection: PeerConnection, callId: String) {
//        val callRef = FirebaseFirestore.getInstance().collection("calls").document(callId)
//
//        callRef.addSnapshotListener { snapshot, _ ->
//            val answer = snapshot?.getString("answer")
//            if (answer != null && !isRemoteDescriptionSet) {
//                val sessionDescription = SessionDescription(SessionDescription.Type.ANSWER, answer)
//
//                peerConnection.setRemoteDescription(object : SdpObserver {
//                    override fun onSetSuccess() {
//                        Log.d("WebRTC", "✅ Remote description (answer) set")
//                        isRemoteDescriptionSet = true
//
//                        // Flush any ICE we stored before remote SDP was ready
//                        pendingIceCandidates.forEach {
//                            peerConnection.addIceCandidate(it)
//                        }
//                        pendingIceCandidates.clear()
//                    }
//
//                    override fun onSetFailure(error: String?) {
//                        Log.e("WebRTC", "❌ Failed to set remote description: $error")
//                    }
//
//                    override fun onCreateSuccess(p0: SessionDescription?) {}
//                    override fun onCreateFailure(p0: String?) {}
//                }, sessionDescription)
//            }
//        }
//    }
//
////    fun listenForAnswer(peerConnection: PeerConnection, callId: String) {
////        val callRef = FirebaseFirestore.getInstance().collection("calls").document(callId)
////
////        callRef.addSnapshotListener { snapshot, _ ->
////            val answer = snapshot?.getString("answer")
////            if (answer != null) {
////                val sessionDescription = SessionDescription(SessionDescription.Type.ANSWER, answer)
////                peerConnection.setRemoteDescription(object : SdpObserver {
////                    override fun onSetSuccess() {
////                        Log.d("WebRTC", "Remote description (answer) set successfully")
////                    }
////
////                    override fun onSetFailure(error: String?) {
////                        Log.e("WebRTC", "Failed to set remote description: $error")
////                    }
////
////                    override fun onCreateSuccess(sessionDescription: SessionDescription?) {}
////                    override fun onCreateFailure(error: String?) {}
////                }, sessionDescription)
////            }
////        }
////    }
//
//    fun listenForRemoteIceCandidates(peerConnection: PeerConnection, callId: String) {
//        val iceCandidatesRef = FirebaseFirestore.getInstance()
//            .collection("calls").document(callId)
//            .collection("iceCandidates")
//
//        iceCandidatesRef.addSnapshotListener { snapshot, _ ->
//            snapshot?.documents?.forEach { doc ->
//                val candidate = IceCandidate(
//                    doc.getString("sdpMid"),
//                    doc.getLong("sdpMLineIndex")?.toInt() ?: 0,
//                    doc.getString("candidate") ?: ""
//                )
//
//                if (isRemoteDescriptionSet) {
//                    peerConnection.addIceCandidate(candidate)
//                } else {
//                    pendingIceCandidates.add(candidate)
//                    Log.d("WebRTC", "⏳ Queued ICE candidate until remote SDP is ready")
//                }
//            }
//        }
//    }
//
////    fun listenForRemoteIceCandidates(peerConnection: PeerConnection, callId: String) {
////        val iceCandidatesRef = FirebaseFirestore.getInstance()
////            .collection("calls").document(callId)
////            .collection("iceCandidates")
////
////        iceCandidatesRef.addSnapshotListener { snapshot, _ ->
////            snapshot?.documents?.forEach { doc ->
////                val candidate = IceCandidate(
////                    doc.getString("sdpMid"),
////                    doc.getLong("sdpMLineIndex")?.toInt() ?: 0,
////                    doc.getString("candidate") ?: ""
////                )
////
////                Log.d("WebRTC", "Adding ICE Candidate: $candidate") // 🔥 Debug log
////                peerConnection.addIceCandidate(candidate)
////            }
////        }
////    }
//}
//@Composable
//fun AudioCallScreen(
//    context: Context,
//    userId: String,
//    astrologerId: String,
//    callId: String,
//    onEndCall: () -> Unit
//) {
//    val firestore = FirebaseFirestore.getInstance()
//
//    var callStatus by remember { mutableStateOf("Connecting") }
//    var astrologerName by remember { mutableStateOf("Astrologer") }
//    var astrologerProfileUrl by remember { mutableStateOf<String?>(null) }
//    var speakerOn by remember { mutableStateOf(false) }
//    var muteOn by remember { mutableStateOf(false) }
//    var durationText by remember { mutableStateOf("") }
//
//    val coroutineScope = rememberCoroutineScope()
//    var timerJob by remember { mutableStateOf<Job?>(null) }
//
//    var boundService: WebRtcCallService? by remember { mutableStateOf(null) }
//
//    val serviceConnection = remember {
//        object : ServiceConnection {
//            override fun onServiceConnected(name: ComponentName?, binder: IBinder?) {
//                boundService = (binder as? WebRtcCallService.LocalBinder)?.getService()
//            }
//
//            override fun onServiceDisconnected(name: ComponentName?) {
//                boundService = null
//            }
//        }
//    }
//
//    // 🔹 Fetch Astrologer Info
//    LaunchedEffect(userId) {
//        firestore.collection("astrologers").document(astrologerId).get()
//            .addOnSuccessListener { doc ->
//                astrologerName = doc.getString("name") ?: "Astrologer"
//                astrologerProfileUrl = doc.getString("profileImageUrl")
//                    ?: "https://i0.wp.com/static.vecteezy.com/system/resources/previews/036/280/650/original/default-avatar-profile-icon-social-media-user-image-gray-avatar-icon-blank-profile-silhouette-illustration-vector.jpg?ssl=1"
//            }
//    }
//
//    // 🔥 Start and Bind to WebRTC Service
//    LaunchedEffect(astrologerName) {
//        if (astrologerName != "Astrologer") {
//            WebRtcCallService.start(context, callId, userId, astrologerId)
//            val intent = Intent(context, WebRtcCallService::class.java)
//            context.bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE)
//        }
//    }
//
//    // ❌ Auto end if call is stuck in connecting
////    LaunchedEffect(callId) {
////        delay(20_000)
////        val snapshot = firestore.collection("calls").document(callId).get().await()
////        if (snapshot.getString("status") == "connecting") {
////            firestore.collection("calls").document(callId).update("status", "ended")
////        }
////    }
//
//    // ⏱️ Call Duration
//    LaunchedEffect(callStatus) {
//        if (callStatus == "ongoing" && timerJob == null) {
//            val timestamp = firestore.collection("calls").document(callId)
//                .get().await().getTimestamp("callStartTimestamp")
//
//            timestamp?.let {
//                val startMillis = it.toDate().time
//                timerJob = coroutineScope.launch {
//                    while (isActive) {
//                        val now = System.currentTimeMillis()
//                        val elapsed = now - startMillis
//                        val minutes = (elapsed / 1000) / 60
//                        val seconds = (elapsed / 1000) % 60
//                        durationText = String.format("%02d:%02d", minutes, seconds)
//                        delay(1000)
//                    }
//                }
//            }
//        } else {
//            timerJob?.cancel()
//            timerJob = null
//        }
//    }
//
//    fun endCallCleanup() {
//        CoroutineScope(Dispatchers.IO).launch {
//            endCallAndDeductFinalAmount(firestore, callId, userId, astrologerId)
//            WebRtcCallService.stop(context)
//            withContext(Dispatchers.Main) {
//                onEndCall()
//            }
//        }
//    }
//
//    // 🔄 Firestore Listeners & Cleanup
//    DisposableEffect(Unit) {
//        val callRef = firestore.collection("calls").document(callId)
//        val userRef = firestore.collection("users").document(userId)
//
//        val callListener = callRef.addSnapshotListener { snapshot, _ ->
//            val status = snapshot?.getString("status") ?: "ended"
//            if (status != callStatus) callStatus = status
//            if (status == "ended") endCallCleanup()
//        }
//
//        val balanceListener = userRef.addSnapshotListener { snapshot, _ ->
//            val balance = snapshot?.getDouble("balance") ?: 0.0
//            if (balance <= 0) endCallCleanup()
//        }
//
//        onDispose {
//            context.unbindService(serviceConnection)
//            callListener.remove()
//            balanceListener.remove()
//        }
//    }
//
//    // 🔹 UI Layout (Minimal)
//    Column(
//        modifier = Modifier
//            .fillMaxSize()
//            .background(Color.Black)
//            .padding(horizontal = 32.dp, vertical = 24.dp),
//        horizontalAlignment = Alignment.CenterHorizontally
//    ) {
//        Spacer(modifier = Modifier.height(48.dp))
//
//        AsyncImage(
//            model = astrologerProfileUrl,
//            contentDescription = null,
//            modifier = Modifier
//                .size(100.dp)
//                .clip(CircleShape)
//                .border(2.dp, Color.White, CircleShape)
//        )
//
//        Spacer(modifier = Modifier.height(16.dp))
//
//        Text(astrologerName, color = Color.White, fontSize = 22.sp)
//        Spacer(modifier = Modifier.height(8.dp))
//        Text(if (callStatus == "ongoing") durationText else callStatus, color = Color.Gray)
//
//        Spacer(modifier = Modifier.weight(1f))
//
//        Row(
//            modifier = Modifier.fillMaxWidth(),
//            horizontalArrangement = Arrangement.SpaceEvenly
//        ) {
//            CallActionButton(Icons.Default.VolumeUp, "Speaker", speakerOn) {
//                speakerOn = !speakerOn
//                boundService?.setSpeaker(speakerOn)
//            }
//
//            CallActionButton(Icons.Default.MicOff, "Mute", muteOn) {
//                muteOn = !muteOn
//                boundService?.setMute(muteOn)
//            }
//
//            Column(horizontalAlignment = Alignment.CenterHorizontally) {
//                IconButton(
//                    onClick = { endCallCleanup() },
//                    modifier = Modifier
//                        .size(65.dp)
//                        .clip(CircleShape)
//                        .background(Color.Red)
//                ) {
//                    Icon(Icons.Default.CallEnd, contentDescription = "End", tint = Color.White)
//                }
//                Spacer(modifier = Modifier.height(4.dp))
//                Text("End", color = Color.White, fontSize = 12.sp)
//            }
//        }
//    }
//}
//
//
//@Composable
//fun CallActionButton(
//    icon: ImageVector,
//    label: String,
//    toggled: Boolean = false,
//    onClick: () -> Unit
//) {
//    val backgroundColor = if (toggled) Color.White else Color.DarkGray
//    val iconColor = if (toggled) Color.Black else Color.White
//
//    Column(horizontalAlignment = Alignment.CenterHorizontally) {
//        IconButton(
//            onClick = onClick,
//            modifier = Modifier
//                .size(50.dp)
//                .clip(CircleShape)
//                .background(backgroundColor)
//        ) {
//            Icon(icon, contentDescription = label, tint = iconColor)
//        }
//        Spacer(modifier = Modifier.height(4.dp))
//        Text(text = label, color = Color.White, fontSize = 12.sp)
//    }
//}
//
//fun toggleSpeaker(context: Context, enable: Boolean) {
//    val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
//    audioManager.mode = AudioManager.MODE_IN_COMMUNICATION
//    audioManager.isSpeakerphoneOn = enable
//}
//
////@Composable
////fun AudioCallScreen(
////    context: Context,
////    userId: String,
////    astrologerId: String,
////    callId: String,
////    onEndCall: () -> Unit
////) {
////    val webRTCManager = remember { WebRTCManager(context) }
////    val firestore = FirebaseFirestore.getInstance()
////    val localContext = LocalContext.current
////
////    var callStatus by remember { mutableStateOf("Connecting...") }
////    var peerConnection: PeerConnection? by remember { mutableStateOf(null) }
////
////    // 🔹 Request Audio Permissions
////    LaunchedEffect(Unit) {
////        if (ActivityCompat.checkSelfPermission(localContext, Manifest.permission.RECORD_AUDIO)
////            != PackageManager.PERMISSION_GRANTED
////        ) {
////            ActivityCompat.requestPermissions(
////                (localContext as? android.app.Activity) ?: return@LaunchedEffect,
////                arrayOf(Manifest.permission.RECORD_AUDIO),
////                100
////            )
////        }
////    }
////
////    // 🔹 Function to End Call & Clean Up
////    fun endCallCleanup() {
////        CoroutineScope(Dispatchers.IO).launch {
////            endCallAndDeductFinalAmount(firestore, callId, userId, astrologerId)
////            withContext(Dispatchers.Main) {
////                peerConnection?.close() // 🔹 Ensure WebRTC cleanup
////                peerConnection = null
////                onEndCall()  // 🔹 Navigate back safely
////            }
////        }
////    }
////
////    DisposableEffect(Unit) {
////        webRTCManager.initialize()
////        val iceServers = listOf(PeerConnection.IceServer.builder("stun:stun.l.google.com:19302").createIceServer())
////
////        peerConnection = webRTCManager.createPeerConnection(iceServers) { candidate ->
////            webRTCManager.sendIceCandidate(callId, candidate)
////        }
////
////        peerConnection?.let { pc ->
////            webRTCManager.createOffer(pc, callId, userId, astrologerId)
////            webRTCManager.listenForAnswer(pc, callId)
////            webRTCManager.listenForRemoteIceCandidates(pc, callId)
////        }
////
////        val callRef = firestore.collection("calls").document(callId)
////        val userRef = firestore.collection("users").document(userId)
////
////        val callListener = callRef.addSnapshotListener { snapshot, _ ->
////            snapshot?.let {
////                val newStatus = it.getString("status") ?: "ended"
////                if (newStatus != callStatus) {
////                    callStatus = newStatus // 🔹 Update UI
////                }
////                if (newStatus == "ended") {
////                    endCallCleanup()
////                }
////            }
////        }
////
////        val balanceListener = userRef.addSnapshotListener { snapshot, _ ->
////            snapshot?.let {
////                val userBalance = it.getDouble("balance") ?: 0.0
////                if (userBalance <= 0) {
////                    endCallCleanup()
////                }
////            }
////        }
////
////        onDispose {
////            callListener.remove()
////            balanceListener.remove()
////            peerConnection?.close()
////            peerConnection = null
////        }
////    }
////
////    // 🔹 UI Layout
////    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
////        Column(horizontalAlignment = Alignment.CenterHorizontally) {
////           TranslatedText("In Call with Astrologer")
////           TranslatedText("Status: $callStatus") // 🔥 NEW: Show call status
////            Spacer(modifier = Modifier.height(16.dp))
////            Button(onClick = { endCallCleanup() }) { // 🔥 No more unresolved reference error!
////               TranslatedText("End Call")
////            }
////        }
////    }
////}
//
//fun endCallAndDeductFinalAmount(
//    firestore: FirebaseFirestore,
//    callId: String,
//    userId: String,
//    astrologerId: String
//) {
//    val callRef = firestore.collection("calls").document(callId)
//    val userRef = firestore.collection("users").document(userId)
//    val astrologerRef = firestore.collection("astrologers").document(astrologerId)
//    callRef.update("status", "ended")
//    userRef.collection("audiocalls").document(callId).update("status", "ended")
//    astrologerRef.collection("audiocalls").document(callId).update("status", "ended")
//}
