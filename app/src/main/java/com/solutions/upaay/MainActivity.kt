package com.solutions.upaay

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import com.google.firebase.messaging.FirebaseMessaging
import com.solutions.upaay.globalComponents.components.CircularProgressLoadingScreen
import com.solutions.upaay.globalComponents.components.CircularProgressLoadingScreenWithText
import com.solutions.upaay.screens.auth.forgotPassword.AuthForgotPassword
import com.solutions.upaay.screens.auth.newuser.basicinfo.CreateUser
import com.solutions.upaay.screens.auth.newuser.profileinfo.BuildProfile
import com.solutions.upaay.screens.auth.signin.AuthSignInScreen
import com.solutions.upaay.screens.auth.welcome.AuthWelcomeScreen
import com.solutions.upaay.screens.call.audio_call.AudioCallScreen
import com.solutions.upaay.screens.call.audio_call.OngoingCallBanner
import com.solutions.upaay.screens.call.audio_call.OutgoingCallBanner
import com.solutions.upaay.screens.call.audio_call.RecentAudioCalls
import com.solutions.upaay.screens.call.audio_call.WebRTCManager
import com.solutions.upaay.screens.chat.ChatScreen
import com.solutions.upaay.screens.chat.MyChatsScreen
import com.solutions.upaay.screens.home.HomeScreen
import com.solutions.upaay.screens.home.components.astrologer.components.cards.Astrologer
import com.solutions.upaay.screens.home.components.astrologer.components.cards.CallAstrologerBottomSheet
import com.solutions.upaay.screens.home.components.astrologer.components.screens.AstrologerDetailsScreen
import com.solutions.upaay.screens.home.components.astrologer.components.screens.AstrologerUploadsScreen
import com.solutions.upaay.screens.home.components.profile.MyProfile
import com.solutions.upaay.screens.home.components.shop.components.screens.CartScreen
import com.solutions.upaay.screens.home.components.shop.components.screens.CheckoutScreen
import com.solutions.upaay.screens.home.components.shop.components.screens.MyOrdersScreen
import com.solutions.upaay.screens.home.components.shop.components.screens.ProductScreen
import com.solutions.upaay.screens.policies.AboutUsScreen
import com.solutions.upaay.screens.policies.PrivacyPolicyScreen
import com.solutions.upaay.screens.policies.RefundPolicyScreen
import com.solutions.upaay.screens.policies.TermsAndConditionsScreen
import com.solutions.upaay.screens.search.search.SearchResultScreen
import com.solutions.upaay.screens.search.search.SearchScreen
import com.solutions.upaay.screens.speciality.AstrologerBySpeciality
import com.solutions.upaay.screens.wallet.RechargeHistoryScreen
import com.solutions.upaay.screens.wallet.WalletScreen
import com.solutions.upaay.ui.theme.UpaayTheme
import com.solutions.upaay.utils.auth.AuthUtils
import com.solutions.upaay.utils.database.retrieve.startRealtimeIsInCallUpdates
import com.solutions.upaay.utils.loading.LoadingStateManager
import com.solutions.upaay.utils.notifications.UserAudioCallService
import com.solutions.upaay.utils.notifications.UserCallNotificationManager
//import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.serialization.Serializable
import org.webrtc.PeerConnection

//@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var googleSignInLauncher: ActivityResultLauncher<Intent>
    private lateinit var navController: NavController

    private lateinit var sharedPreferences: SharedPreferences
    private val _selectedLanguage = MutableStateFlow("en") // Default to English
    val selectedLanguage: StateFlow<String> = _selectedLanguage

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        sharedPreferences = getSharedPreferences("upaay_prefs", Context.MODE_PRIVATE)
        _selectedLanguage.value = getCurrentSelectedLanguage()

        // Initialize GoogleSignInClient
        val googleSignInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(this, googleSignInOptions)

        // Register ActivityResultLauncher for Google Sign-In
        googleSignInLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == RESULT_OK) {
                    val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
                    handleGoogleSignInResult(this, task)
                }
            }

        // for the realtime updates of Call Status of astrologers
        startRealtimeIsInCallUpdates()

//        val apiKey = "02mL0iKaRE5aWtnl9zUK259d98GAkvhA9upfR9nB"
//
//        val interceptor = Interceptor { chain ->
//            val request = chain.request().newBuilder()
//                .addHeader("x-api-key", apiKey)
//                .build()
//            chain.proceed(request)
//        }
//
//        val okHttpClient = OkHttpClient.Builder()
//            .addInterceptor(interceptor)
//            .build()
//
//        // ✅ Create Retrofit instance manually
//        val retrofit = Retrofit.Builder()
//            .baseUrl("https://json.freeastrologyapi.com/") // ✅ Ensure correct API URL
//            .client(okHttpClient)
//            .addConverterFactory(GsonConverterFactory.create())
//            .build()
//
//        // ✅ Create API Service
//        val apiService = retrofit.create(PanchangApiService::class.java)
//
//        // ✅ Create Repository manually
//        val repository = PanchangRepository(apiService)
//
//        // ✅ Create ViewModel manually
//        val panchangViewModel = ViewModelProvider(this, object : ViewModelProvider.Factory {
//            override fun <T : ViewModel> create(modelClass: Class<T>): T {
//                return PanchangViewModel(repository) as T
//            }
//        })[PanchangViewModel::class.java]

        setContent {
            UserAudioCallService.start(this)
            navController = UpaayApp(googleSignInClient, selectedLanguage, ::updateLanguage)
        }
    }

    private fun getCurrentSelectedLanguage(): String {
        return sharedPreferences.getString("selected_language", "en") ?: "en"
    }

    fun updateLanguage(languageCode: String) {
        _selectedLanguage.value = languageCode // Update state
        sharedPreferences.edit().putString("selected_language", languageCode).apply()
        Toast.makeText(this, "Language updated", Toast.LENGTH_SHORT).show()
    }

    override fun onResume() {
        super.onResume()
        intent.removeExtra("navigateTo")
    }

    @Composable
    fun UpaayApp(
        googleSignInClient: GoogleSignInClient, selectedLanguageFlow: StateFlow<String>,
        updateLanguage: (String) -> Unit
    ): NavController {

        val context = LocalContext.current
        val navController = rememberNavController()
        val sharedPreferences = getSharedPreferences("CartPreferences", MODE_PRIVATE)

        val selectedLanguage by selectedLanguageFlow.collectAsState()

        val isLoading by LoadingStateManager::isLoading
        val isLoadingWithText by LoadingStateManager::isLoadingWithText

        val isUserAuthenticated = AuthUtils.isUserAuthenticated()
        val showSessionConflictDialog = remember { mutableStateOf(false) }

        var selectedAstrologerForCall by remember { mutableStateOf<Astrologer?>(null) }

        val startDestination = if (isUserAuthenticated) {
            HomeScreenRoute(false)
        } else {
            AuthSignInScreenRoute
        }

        if (!isUserAuthenticated) {
            LoadingStateManager.hideLoading()
        }

        LaunchedEffect(Unit) {
            val auth = FirebaseAuth.getInstance()
            val currentUser = auth.currentUser
            val firestore = FirebaseFirestore.getInstance()

            if (currentUser != null) {
                val sharedPreferences =
                    context.getSharedPreferences("upaay_user", Context.MODE_PRIVATE)
                val localDeviceId = sharedPreferences.getString("deviceId", null)

                saveAstrologerFcmToken()

                firestore.collection("users").document(currentUser.uid)
                    .addSnapshotListener { snapshot, error ->
                        if (error != null || snapshot == null || !snapshot.exists()) return@addSnapshotListener

                        val remoteDeviceId = snapshot.getString("activeDeviceId")
                        if (remoteDeviceId == null || remoteDeviceId != localDeviceId) {
                            showSessionConflictDialog.value = true
                        }
                    }
            }
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    android.Manifest.permission.POST_NOTIFICATIONS
                )
                != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(android.Manifest.permission.POST_NOTIFICATIONS),
                    101
                )
            }
        }

        val activity = context as? Activity
        val startIntent = activity?.intent
        val navigateTo = remember { mutableStateOf(startIntent?.getStringExtra("navigateTo")) }
        LaunchedEffect(navigateTo.value) {
            when (navigateTo.value) {
                "recentAudioCall" -> {
                    navController.navigate(MyRecentAudioCallsScreenRoute)
                    startIntent?.removeExtra("navigateTo")
                }

                "chatScreen" -> {
                    val senderId = startIntent?.getStringExtra("senderId") ?: return@LaunchedEffect
                    navController.navigate(ChatScreenRoute(senderId))
                    startIntent.removeExtra("navigateTo")
                    startIntent.removeExtra("senderId")
                }
            }
        }

        UpaayTheme(darkTheme = false) {
            Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                ) {

                    if (showSessionConflictDialog.value) {
                        Dialog(onDismissRequest = {}) {
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(24.dp),
                                elevation = CardDefaults.cardElevation(8.dp),
                                shape = RoundedCornerShape(16.dp)
                            ) {
                                Column(
                                    modifier = Modifier.padding(24.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text(
                                        "Your account is logged in on another device.",
                                        style = MaterialTheme.typography.titleMedium,
                                        textAlign = TextAlign.Center
                                    )
                                    Spacer(modifier = Modifier.height(16.dp))
                                    Button(
                                        onClick = {
                                            FirebaseAuth.getInstance().signOut()
                                            val appContext = context as Activity
                                            val intent = context.intent
                                            appContext.finish()
                                            appContext.overridePendingTransition(
                                                0,
                                                0
                                            ) // Optional: smooth restart
                                            appContext.startActivity(intent)
//                                            navController.navigate(AuthSignInScreenRoute) {
//                                                popUpTo(0) { inclusive = true } // Clear backstack
//                                            }
                                        },
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = Color.Red,
                                            contentColor = Color.White
                                        )
                                    ) {
                                        Text("Log Out")
                                    }
                                }
                            }
                        }

                    } else {

                        val call = UserCallNotificationManager.currentCallData.value
                        val status = call?.get("status") as? String

                        if (call != null && UserCallNotificationManager.isShowing.value) {
                            when (status) {
                                "connecting" -> {
                                    if (!UserCallNotificationManager.isOutgoingSuppressed) {
                                        OutgoingCallBanner(
                                            navController = navController,
                                            callData = call,
                                            onDismiss = { UserCallNotificationManager.dismiss() }
                                        )
                                    }
                                }

                                "ongoing" -> {
                                    if (!UserCallNotificationManager.isOngoingSuppressed) {
                                        OngoingCallBanner(
                                            navController = navController,
                                            callData = call,
                                            onDismiss = { UserCallNotificationManager.dismiss() }
                                        )
                                    }
                                }
                            }
                        }

                        NavHost(
                            navController = navController,
                            startDestination = startDestination
                        ) {

                            composable<AuthWelcomeScreenRoute> {
                                AuthWelcomeScreen {
                                    navController.navigate(AuthSignInScreenRoute)
                                }
                            }

                            composable<AuthSignInScreenRoute> {
                                AuthSignInScreen(
                                    navController = navController,
                                    googleSignInClient = googleSignInClient,
                                    googleSignInLauncher = googleSignInLauncher,
                                    selectedLanguage = selectedLanguage,
                                    updateLanguage = updateLanguage
                                )
                            }

                            composable<AuthForgotPasswordScreenRoute> {
                                AuthForgotPassword(navController)
                            }

                            composable<AuthCreateUserRoute> {
                                CreateUser(navController)
                            }

                            composable<AuthStartBuildingProfileRoute> {
                                BuildProfile(navController)
                            }

                            composable<HomeScreenRoute> {
                                val args = it.toRoute<HomeScreenRoute>()
                                HomeScreen(
                                    navController,
                                    sharedPreferences,
                                    args.forceCheckProfileState,
                                    selectedLanguage = selectedLanguage,
                                    updateLanguage = updateLanguage,
                                    selectAstrologerForCall = { astrologer ->
                                        selectedAstrologerForCall =
                                            astrologer
                                    })
                            }

                            composable<SearchScreenRoute> {
                                SearchScreen(navController)
                            }

                            composable<AstrologersBySpecialityScreenRoute> {
                                val args = it.toRoute<AstrologersBySpecialityScreenRoute>()
                                AstrologerBySpeciality(
                                    args.selectedTopic,
                                    navController,
                                    selectAstrologerForCall = { astrologer ->
                                        selectedAstrologerForCall =
                                            astrologer
                                    })
                            }

                            composable<SearchResultScreenRoute> {
                                val args = it.toRoute<SearchResultScreenRoute>()
                                SearchResultScreen(
                                    navController,
                                    args.searchQuery,
                                    selectAstrologerForCall = { astrologer ->
                                        selectedAstrologerForCall =
                                            astrologer
                                    })
                            }

                            composable<AstrologerDetailsScreenRoute> {
                                val args = it.toRoute<AstrologerDetailsScreenRoute>()
                                AstrologerDetailsScreen(
                                    args.astrologerId,
                                    navController,
                                    selectAstrologerForCall = { astrologer ->
                                        selectedAstrologerForCall =
                                            astrologer
                                    }
                                )
                            }

                            composable<AstrologerUploadsScreenRoute> {
                                AstrologerUploadsScreen(navController)
                            }

                            composable<ChatScreenRoute> {
                                val args = it.toRoute<ChatScreenRoute>()
                                FirebaseAuth.getInstance().currentUser?.uid?.let { it1 ->
                                    ChatScreen(
                                        navController, args.astrologerId,
                                        it1
                                    )
                                }
                            }

                            composable<MyChatsScreenRoute> {
                                FirebaseAuth.getInstance().currentUser?.uid?.let { it1 ->
                                    MyChatsScreen(navController, it1)
                                }
                            }

                            composable<MyRecentAudioCallsScreenRoute> {
                                RecentAudioCalls(navController)
                            }

                            composable<AudioCallScreenRoute> {
                                val args = it.toRoute<AudioCallScreenRoute>()

                                val webRTCManager = WebRTCManager(this@MainActivity)
                                webRTCManager.initialize()

                                val iceServers = listOf(
                                    PeerConnection.IceServer.builder("stun:stun.l.google.com:19302")
                                        .createIceServer()
                                )

                                webRTCManager.createPeerConnection(iceServers) { candidate ->
                                    webRTCManager.sendIceCandidate(args.callId, candidate)
                                }
                                    ?.let { peerConnection -> // ✅ Only proceed if peerConnection is not null

                                        FirebaseAuth.getInstance().currentUser?.uid?.let { userId ->
                                            AudioCallScreen(
                                                context = this@MainActivity,
                                                userId = userId,
                                                astrologerId = args.astrologerId,
                                                callId = args.callId,
                                                onEndCall = {
                                                    navController.navigate(HomeScreenRoute(false)) {
                                                        popUpTo(0) { inclusive = true }
                                                    }
                                                }
                                            )
                                        }
                                    } ?: Log.e(
                                    "WebRTC",
                                    "Failed to create PeerConnection"
                                ) // Log error if PeerConnection is null
                            }

                            composable<WalletScreenRoute> {
                                WalletScreen(navController)
                            }

                            composable<RechargeHistoryScreenRoute> {
                                FirebaseAuth.getInstance().currentUser?.uid?.let { it1 ->
                                    RechargeHistoryScreen(navController, it1)
                                }
                            }

                            composable<ProductScreenRoute> {
                                val args = it.toRoute<ProductScreenRoute>()
                                ProductScreen(
                                    navController = navController,
                                    sharedPreferences = sharedPreferences,
                                    productId = args.productId
                                )
                            }

                            composable<CartScreenRoute> {
                                CartScreen(navController, sharedPreferences)
                            }

                            composable<CheckoutScreenRoute> {
                                FirebaseAuth.getInstance().currentUser?.uid?.let { it1 ->
                                    CheckoutScreen(navController, sharedPreferences, it1)
                                }
                            }

                            composable<MyOrdersScreenRoute> {
                                FirebaseAuth.getInstance().currentUser?.uid?.let { it1 ->
                                    MyOrdersScreen(it1, navController)
                                }
                            }

                            composable<MyProfileScreenRoute> {
                                MyProfile(navController)
                            }

                            composable<PoliciesAboutUsRoute> {
                                AboutUsScreen()
                            }

                            composable<PoliciesPrivacyPolicyRoute> {
                                PrivacyPolicyScreen()
                            }

                            composable<PoliciesTermsAndConditionsRoute> {
                                TermsAndConditionsScreen()
                            }

                            composable<PoliciesRefundPolicyRoute> {
                                RefundPolicyScreen()
                            }
                        }
                    }
                }
                if (isLoading) {
                    CircularProgressLoadingScreen()
                }
                if (isLoadingWithText.isNotEmpty()) {
                    CircularProgressLoadingScreenWithText(isLoadingWithText)
                }

                if (selectedAstrologerForCall != null) {
                    CallAstrologerBottomSheet(
                        navController = navController,
                        astrologerId = selectedAstrologerForCall!!.uid,  // ✅ Pass correct ID
                        astrologerName = selectedAstrologerForCall!!.name,  // ✅ Pass correct Name
                        onDismiss = { selectedAstrologerForCall = null }
                    )
                }
            }
        }
        return navController
    }

    private fun handleGoogleSignInResult(
        context: Context,
        task: com.google.android.gms.tasks.Task<GoogleSignInAccount>
    ) {
        LoadingStateManager.showLoading()
        try {
            val account = task.getResult(ApiException::class.java)
            val credential = GoogleAuthProvider.getCredential(account?.idToken, null)
            FirebaseAuth.getInstance().signInWithCredential(credential)
                .addOnCompleteListener { signInTask ->
                    if (signInTask.isSuccessful) {
                        val deviceId =
                            Settings.Secure.getString(
                                context.contentResolver,
                                Settings.Secure.ANDROID_ID
                            )
                        val userId = signInTask.result.user?.uid ?: return@addOnCompleteListener

                        Firebase.firestore.collection("users").document(userId)
                            .update("activeDeviceId", deviceId)
                            .addOnSuccessListener {

                                val sharedPreferences =
                                    context.getSharedPreferences("upaay_user", Context.MODE_PRIVATE)
                                sharedPreferences.edit().putString("deviceId", deviceId).apply()

                                Toast.makeText(context, "Login Successful!", Toast.LENGTH_SHORT)
                                    .show()
                                LoadingStateManager.hideLoading()
                                navController.navigate(MainActivity.HomeScreenRoute(false)) {
                                    popUpTo(0) { inclusive = true } // clears entire backstack
                                }
                            }.addOnFailureListener {

                                LoadingStateManager.hideLoading()
                                navController.navigate(MainActivity.HomeScreenRoute(false)) {
                                    popUpTo(0) { inclusive = true } // clears entire backstack
                                }

                            }
                    } else {
                        Toast.makeText(
                            this,
                            "Google Sign-In failed",
                            Toast.LENGTH_LONG
                        ).show()
                        LoadingStateManager.hideLoading()
                    }
                }
        } catch (e: ApiException) {
            Toast.makeText(this, "Google Sign-In failed", Toast.LENGTH_LONG).show()
            LoadingStateManager.hideLoading()
        }
    }

    fun saveAstrologerFcmToken() {
        val firestore = FirebaseFirestore.getInstance()
        val astrologerId = FirebaseAuth.getInstance().currentUser?.uid ?: return

        FirebaseMessaging.getInstance().token.addOnSuccessListener { token ->
            val docRef = firestore.collection("astrologers").document(astrologerId)

            docRef.get().addOnSuccessListener { document ->
                val existingToken = document.getString("fcmToken")
                if (existingToken != token) {
                    docRef.update("fcmToken", token)
                }
            }
        }
    }

    @Serializable
    object AuthWelcomeScreenRoute

    @Serializable
    object AuthSignInScreenRoute

    @Serializable
    object AuthForgotPasswordScreenRoute

    @Serializable
    object AuthCreateUserRoute

    @Serializable
    object AuthStartBuildingProfileRoute

    @Serializable
    data class HomeScreenRoute(val forceCheckProfileState: Boolean)

    @Serializable
    object SearchScreenRoute

    @Serializable
    data class AstrologersBySpecialityScreenRoute(val selectedTopic: String)

    @Serializable
    data class SearchResultScreenRoute(val searchQuery: String)

    @Serializable
    data class AstrologerDetailsScreenRoute(val astrologerId: String)

    @Serializable
    object AstrologerUploadsScreenRoute

    @Serializable
    data class ChatScreenRoute(val astrologerId: String)

    @Serializable
    object MyChatsScreenRoute

    @Serializable
    object MyRecentAudioCallsScreenRoute

    @Serializable
    data class AudioCallScreenRoute(
        val userId: String,
        val astrologerId: String,
        val callId: String
    )

    @Serializable
    object WalletScreenRoute

    @Serializable
    object RechargeHistoryScreenRoute

    @Serializable
    data class ProductScreenRoute(val productId: String)

    @Serializable
    object CartScreenRoute

    @Serializable
    object CheckoutScreenRoute

    @Serializable
    object MyOrdersScreenRoute

    @Serializable
    object MyProfileScreenRoute

    @Serializable
    object PoliciesAboutUsRoute

    @Serializable
    object PoliciesPrivacyPolicyRoute

    @Serializable
    object PoliciesTermsAndConditionsRoute

    @Serializable
    object PoliciesRefundPolicyRoute
}
