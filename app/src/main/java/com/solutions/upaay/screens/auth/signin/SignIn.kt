package com.solutions.upaay.screens.auth.signin

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.firebase.Firebase
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.firestore.firestore
import com.solutions.upaay.MainActivity
import com.solutions.upaay.R
import com.solutions.upaay.globalComponents.components.CrackinTextField
import com.solutions.upaay.utils.loading.LoadingStateManager
import com.solutions.upaay.utils.translate.TranslatedText
import java.util.concurrent.TimeUnit

fun handleSignIn(
    context: Context,
    navController: NavController,
    auth: FirebaseAuth,
    isEmail: Boolean,
    email: String? = null,
    password: String? = null,
    mobileNumber: String? = null,
    verificationId: MutableState<String?>,
    otp: String? = null
) {
    if (isEmail) {
        if (!email.isNullOrEmpty() && !password.isNullOrEmpty()) {
            signInWithEmail(context, auth, email, password, navController)
        } else {
            Toast.makeText(context, "Please provide email and password", Toast.LENGTH_SHORT).show()
        }
    } else {
        if (!otp.isNullOrEmpty() && verificationId.value != null) {
            verifyOtp(context, auth, verificationId.value!!, otp, navController)
        } else {
            Toast.makeText(context, "Please enter OTP", Toast.LENGTH_SHORT).show()
        }
    }
}

// Email Authentication
private fun signInWithEmail(
    context: Context,
    auth: FirebaseAuth,
    email: String,
    password: String,
    navController: NavController
) {
    LoadingStateManager.showLoading()
    auth.signInWithEmailAndPassword(email, password).addOnCompleteListener { task ->
        if (task.isSuccessful) {

            val deviceId =
                Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID)
            val userId = task.result.user?.uid ?: return@addOnCompleteListener

            Firebase.firestore.collection("users").document(userId)
                .update("activeDeviceId", deviceId)
                .addOnSuccessListener {

                    val sharedPreferences =
                        context.getSharedPreferences("upaay_user", Context.MODE_PRIVATE)
                    sharedPreferences.edit().putString("deviceId", deviceId).apply()

                    Toast.makeText(context, "Login Successful!", Toast.LENGTH_SHORT).show()
                    LoadingStateManager.hideLoading()
                    navController.navigate(MainActivity.HomeScreenRoute(false)) {
                        popUpTo(0) { inclusive = true } // clears entire backstack
                    }
                }.addOnFailureListener {
//                    Toast.makeText(context, "Login failed: ${it.message}", Toast.LENGTH_SHORT).show()
                    LoadingStateManager.hideLoading()
                    navController.navigate(MainActivity.HomeScreenRoute(false)) {
                        popUpTo(0) { inclusive = true } // clears entire backstack
                    }

                }
                } else {
            val exception = task.exception
            when (exception) {
                is FirebaseAuthInvalidUserException -> {
                    // User not found
                    Toast.makeText(
                        context,
                        "User does not exist. Please sign up first.",
                        Toast.LENGTH_LONG
                    ).show()
                    LoadingStateManager.hideLoading()
                }

                is FirebaseAuthInvalidCredentialsException -> {
                    // Incorrect password

                    Toast.makeText(
                        context,
                        "Invalid credentials. Please check your password.",
                        Toast.LENGTH_LONG
                    ).show()
                    LoadingStateManager.hideLoading()
                }

                else -> {
                    // General error message
                    Toast.makeText(
                        context,
                        "Login failed, please try again.",
                        Toast.LENGTH_SHORT
                    ).show()
                    LoadingStateManager.hideLoading()
                }
            }

        }
    }
}

// OTP Logic (Send OTP)
private fun sendOtp(
    context: Context,
    activity: Activity,
    auth: FirebaseAuth,
    mobileNumber: String,
    verificationId: MutableState<String?>,
    onOtpSent: () -> Unit
) {
    LoadingStateManager.showLoading()
    val options = PhoneAuthOptions.newBuilder(auth)
        .setPhoneNumber("+91$mobileNumber")
        .setTimeout(60L, TimeUnit.SECONDS)
        .setActivity(activity)
        .setCallbacks(object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                Toast.makeText(context, "Verification successful", Toast.LENGTH_SHORT)
                    .show()
                LoadingStateManager.hideLoading()
            }

            override fun onVerificationFailed(e: FirebaseException) {
                Toast.makeText(context, "Something went wrong", Toast.LENGTH_SHORT)
                    .show()

//                Log.d("SIGNINISSUES", "onVerificationFailed: $e")

                LoadingStateManager.hideLoading()
            }

            override fun onCodeSent(vId: String, token: PhoneAuthProvider.ForceResendingToken) {
                verificationId.value = vId
                onOtpSent() // Notify OTP is sent
                LoadingStateManager.hideLoading()
            }
        })
        .build()

    PhoneAuthProvider.verifyPhoneNumber(options)
}

// Verify OTP and sign in
private fun verifyOtp(
    context: Context,
    auth: FirebaseAuth,
    verificationId: String,
    otp: String,
    navController: NavController
) {
    val credential = PhoneAuthProvider.getCredential(verificationId, otp)
    LoadingStateManager.showLoading()
    auth.signInWithCredential(credential).addOnCompleteListener { task ->
        if (task.isSuccessful) {

            val deviceId =
                Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID)
            val userId = task.result.user?.uid ?: return@addOnCompleteListener

            Firebase.firestore.collection("users").document(userId)
                .update("activeDeviceId", deviceId)
                .addOnSuccessListener {

                    val sharedPreferences =
                        context.getSharedPreferences("upaay_user", Context.MODE_PRIVATE)
                    sharedPreferences.edit().putString("deviceId", deviceId).apply()

                    Toast.makeText(context, "Login Successful!", Toast.LENGTH_SHORT).show()
                    LoadingStateManager.hideLoading()
                    navController.navigate(MainActivity.HomeScreenRoute(false)) {
                        popUpTo(0) { inclusive = true } // clears entire backstack
                    }

                }.addOnFailureListener {
//                    Toast.makeText(context, "Login failed: ${it.message}", Toast.LENGTH_SHORT).show()
                    LoadingStateManager.hideLoading()
                    navController.navigate(MainActivity.HomeScreenRoute(false)) {
                        popUpTo(0) { inclusive = true } // clears entire backstack
                    }

                }
        } else {
            // Handle failure
            Toast.makeText(navController.context, "Invalid OTP", Toast.LENGTH_SHORT).show()
            LoadingStateManager.hideLoading()
        }
    }
}

//@Composable
//fun AuthSignInScreen(
//    navController: NavController, googleSignInClient: GoogleSignInClient,
//    googleSignInLauncher: ActivityResultLauncher<Intent>
//) {
//    val context = LocalContext.current
//    val activity = LocalContext.current as? Activity
//    val auth = remember { FirebaseAuth.getInstance() }
//
//    val imageUrls = listOf(
//        R.drawable.welcome_banner_1,
//        R.drawable.welcome_banner_2,
//        R.drawable.welcome_banner_3
//    )
//
//    // For OTP handling
//    val verificationId = remember { mutableStateOf<String?>(null) }
//    val otpCode = remember { mutableStateOf(TextFieldValue("")) }
//    var isOtpSent by remember { mutableStateOf(false) }
//
////    val email = remember { mutableStateOf(TextFieldValue("")) }
////    val password = remember { mutableStateOf(TextFieldValue("")) }
//    val mobileNumber = remember { mutableStateOf(TextFieldValue("")) }
//
////    val loginWithEmail = remember { mutableStateOf(true) }
////    val loginWithMobileNumber = remember { mutableStateOf(false) }
//
//    Column(modifier = Modifier.padding(16.dp, 35.dp, 16.dp, 20.dp)) {
//        TranslatedText(
//            text = "Welcome to Upaay",
//            style = MaterialTheme.typography.titleLarge,
//            modifier = Modifier.align(Alignment.CenterHorizontally),
//            textAlign = TextAlign.Center
//        )
//
//        Box(
//            modifier = Modifier
//                .fillMaxWidth()
//                .padding(top = 20.dp)
//                .weight(1f)
//        ) {
//            AutoScrollingBanner(
//                modifier = Modifier.align(Alignment.Center),
//                images = imageUrls,
//                scrollInterval = 5000,
//                boxHeight = LocalConfiguration.current.screenHeightDp / 2.5f,
//                imageContentScale = ContentScale.Fit,
//                imageStartPadding = 10
//            )
//        }

//        Row(
//            verticalAlignment = Alignment.CenterVertically,
//            modifier = Modifier
//                .fillMaxWidth()
//                .padding(top = 25.dp)
//        ) {
//
//            Row(verticalAlignment = Alignment.CenterVertically) {
//                RadioButton(
//                    selected = loginWithEmail.value,
//                    onClick = {
//                        loginWithEmail.value = true
//                        loginWithMobileNumber.value = false
//                    },
//                    modifier = Modifier.padding(0.dp)
//                )
//
//                TranslatedText(
//                    text = "Email",
//                    style = MaterialTheme.typography.bodyMedium,
//                    fontWeight = FontWeight.Normal,
//                    modifier = Modifier.clickable {
//                        loginWithEmail.value = true
//                        loginWithMobileNumber.value = false
//                    }
//                )
//            }
//
//            Spacer(modifier = Modifier.width(40.dp))
//
//            Row(verticalAlignment = Alignment.CenterVertically) {
//                RadioButton(
//                    selected = loginWithMobileNumber.value,
//                    onClick = {
//                        loginWithMobileNumber.value = true
//                        loginWithEmail.value = false
//                    }
//                )
//
//                TranslatedText(
//                    text = "Phone (OTP)",
//                    style = MaterialTheme.typography.bodyMedium,
//                    fontWeight = FontWeight.Normal,
//                    modifier = Modifier.clickable {
//                        loginWithMobileNumber.value = true
//                        loginWithEmail.value = false
//                    }
//                )
//            }
//        }

//        if (loginWithEmail.value) {
//            CrackinTextField(
//                value = email,
//                label = "Enter your email address",
//                isEmail = true,
//                leadingIcon = {
//                    Icon(
//                        imageVector = Icons.Filled.Email,
//                        contentDescription = "Email ID",
//                        modifier = Modifier.size(20.dp)
//                    )
//                },
//                modifier = Modifier.padding(top = 15.dp)
//            )
//
//            CrackinTextField(
//                value = password,
//                label = "Enter password",
//                isPassword = true,
//                leadingIcon = {
//                    Icon(
//                        imageVector = Icons.Filled.Password,
//                        contentDescription = "Password",
//                        modifier = Modifier.size(20.dp)
//                    )
//                },
//                modifier = Modifier.padding(top = 20.dp)
//            )
//            TranslatedText(
//                text = "Forgot Password?",
//                color = MaterialTheme.colorScheme.primary,
//                style = MaterialTheme.typography.bodyMedium,
//                modifier = Modifier
//                    .align(Alignment.End)
//                    .clickable {
//                        navController.navigate(MainActivity.AuthForgotPasswordScreenRoute)
//                    }
//                    .padding(top = 16.dp)
//            )
//
//            Button(
//                onClick = {
//                    handleSignIn(
//                        context = context,
//                        navController = navController,
//                        auth = auth,
//                        isEmail = true,
//                        email = email.value.text,
//                        password = password.value.text,
//                        verificationId = verificationId
//                    )
//                },
//                colors = ButtonDefaults.buttonColors(
//                    containerColor = Color(0xFF2A7BF5),
//                    contentColor = Color.White
//                ),
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .padding(5.dp, 30.dp, 5.dp, 7.dp)
//            ) {
//                TranslatedText(text = "Login", style = MaterialTheme.typography.titleSmall)
//            }
//        } else {

@Composable
fun AuthSignInScreen(
    navController: NavController,
    googleSignInClient: GoogleSignInClient,
    googleSignInLauncher: ActivityResultLauncher<Intent>,
    selectedLanguage: String,
    updateLanguage: (String) -> Unit
) {

    val activity = LocalContext.current as? Activity
    var isChecked by remember { mutableStateOf(true) }

    val verificationId = remember { mutableStateOf<String?>(null) }
    val otpCode = remember { mutableStateOf(TextFieldValue("")) }
    var isOtpSent by remember { mutableStateOf(false) }
    val mobileNumber = remember { mutableStateOf(TextFieldValue("")) }

    val context = LocalContext.current
    val auth = remember { FirebaseAuth.getInstance() }
    val isLoading = remember { mutableStateOf(false) }
    var isLanguageDropdownOpen by remember { mutableStateOf(false) }
    var tempSelectedLanguage by remember { mutableStateOf(selectedLanguage) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(horizontal = 24.dp, vertical = 40.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        item {

            // 🔹 Logo
            Image(
                painter = painterResource(id = R.drawable.upaay_astrologer_logo_nobackground),
                contentDescription = "Upaay Astrologer Logo",
                modifier = Modifier
                    .fillMaxWidth(0.28f)
                    .aspectRatio(1f)
            )

            Spacer(modifier = Modifier.height(20.dp))

            // 🔹 Heading
            TranslatedText(
                text = "Welcome to Upaay",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(10.dp))

            TranslatedText(
                text = "Let's get you logged in",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Gray
            )

            Spacer(modifier = Modifier.height(25.dp))

            // 🔹 Email / Username Input
            // 📱 Mobile Number Field

            if (isOtpSent) {
                // 🔢 OTP Field
                UpaayLoginTextField(
                    value = otpCode,
                    label = "Enter OTP",
                    isNumeric = true,
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Key,
                            contentDescription = "OTP",
                            modifier = Modifier.size(20.dp)
                        )
                    },
                    modifier = Modifier.padding(top = 10.dp)
                )

                // ✅ Verify OTP Button
                Button(
                    onClick = {
                        handleSignIn(
                            context = context,
                            navController = navController,
                            auth = auth,
                            isEmail = false,
                            mobileNumber = mobileNumber.value.text,
                            verificationId = verificationId,
                            otp = otpCode.value.text
                        )
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFA24C13),
                        contentColor = Color.White
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 20.dp)
                ) {
                    TranslatedText(
                        text = "Verify OTP",
                        style = MaterialTheme.typography.titleSmall
                    )
                }
            } else {
                // 🔄 Get OTP Button
                UpaayLoginTextField(
                    value = mobileNumber,
                    label = "Enter your mobile number",
                    isNumeric = true,
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Filled.Phone,
                            contentDescription = "Mobile Number",
                            modifier = Modifier.size(20.dp)
                        )
                    },
                    modifier = Modifier.padding(top = 10.dp)
                )

                Button(
                    onClick = {
                        if (activity != null) {
                            if (mobileNumber.value.text.isNotEmpty()) {
                                if (isChecked) {
                                    sendOtp(
                                        context = context,
                                        activity = activity,
                                        auth = auth,
                                        mobileNumber = mobileNumber.value.text,
                                        verificationId = verificationId
                                    ) {
                                        isOtpSent = true
                                    }
                                } else {
                                    Toast.makeText(
                                        context,
                                        "You must accept our terms and policies before proceeding",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            } else {
                                Toast.makeText(
                                    context,
                                    "Please enter your phone number",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFA24C13),
                        contentColor = Color.White
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 20.dp)
                ) {
                    TranslatedText(
                        text = "Get OTP",
                        style = MaterialTheme.typography.titleSmall
                    )
                }
            }

            // ➖ Divider with "or"
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(10.dp, 20.dp)
            ) {
                Spacer(
                    modifier = Modifier
                        .weight(1f)
                        .height(1.dp)
                        .background(Color.Gray.copy(alpha = 0.3f))
                )
                TranslatedText(
                    text = "or",
                    fontSize = 11.sp,
                    modifier = Modifier.padding(10.dp, 0.dp)
                )
                Spacer(
                    modifier = Modifier
                        .weight(1f)
                        .height(1.dp)
                        .background(Color.Gray.copy(alpha = 0.3f))
                )
            }

            // 🔵 Google Sign-In Button
            Button(
                onClick = {
                    if (isChecked) {
                        LoadingStateManager.showLoading()
                        val mainActivity = context as? Activity
                        mainActivity?.let {
                            googleSignInClient.signOut()
                                .addOnCompleteListener {
                                    googleSignInLauncher.launch(googleSignInClient.signInIntent)
                                    LoadingStateManager.hideLoading()
                                }
                        }
                    } else {
                        Toast.makeText(
                            context,
                            "You must accept our terms and policies before proceeding",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                border = BorderStroke(1.dp, Color.Gray.copy(alpha = 0.2f)),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 5.dp)
            ) {
                AsyncImage(
                    model = "https://upload.wikimedia.org/wikipedia/commons/thumb/c/c1/Google_%22G%22_logo.svg/768px-Google_%22G%22_logo.svg.png",
                    contentDescription = "Google Sign-In",
                    modifier = Modifier.size(20.dp)
                )
                TranslatedText(
                    text = "Sign In with Google",
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color.Black,
                    modifier = Modifier.padding(8.dp, 6.dp, 0.dp, 6.dp),
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            TermsAgreementSection(
                isChecked = isChecked,
                onCheckedChange = { isChecked = it },
                onTermsClick = {
                    navController.navigate(MainActivity.PoliciesTermsAndConditionsRoute)
                },
                onPrivacyClick = {
                    // Navigate to Privacy screen
                    navController.navigate(MainActivity.PoliciesPrivacyPolicyRoute)
                }
            )


            Spacer(modifier = Modifier.height(15.dp))

            // 🔹 Create New Account Button (WhatsApp Chat)
//            GradientButton(
//                text = "Create New Account",
//                gradientColors = listOf(Color(0xFFF7A32B), Color(0xFFE95432)),
//                onClick = {
//                    val whatsappIntent = Intent(Intent.ACTION_VIEW).apply {
//                        data =
//                            Uri.parse("https://wa.me/919318345767?text=Hello, I want to create an astrologer account.")
//                    }
//                    context.startActivity(whatsappIntent)
//                }
//            )

            Spacer(modifier = Modifier.height(50.dp))

            LanguageSelectorCard(
                selectedLanguage = selectedLanguage,
                tempSelectedLanguage = tempSelectedLanguage,
                isLanguageDropdownOpen = isLanguageDropdownOpen,
                onToggleDropdown = { isLanguageDropdownOpen = !isLanguageDropdownOpen },
                onLanguageSelected = { tempSelectedLanguage = it },
                onApply = {
                    updateLanguage(tempSelectedLanguage)
                    isLanguageDropdownOpen = false
                }
            )
        }
    }
}

// Old SignIn Screen
//@Composable
//fun AuthSignInScreen(
//    navController: NavController,
//    googleSignInClient: GoogleSignInClient,
//    googleSignInLauncher: ActivityResultLauncher<Intent>
//) {
//    val context = LocalContext.current
//    val activity = LocalContext.current as? Activity
//    val auth = remember { FirebaseAuth.getInstance() }
//    var isChecked by remember { mutableStateOf(false) }
//
//    val imageUrls = listOf(
//        R.drawable.welcome_banner_1,
//        R.drawable.welcome_banner_2,
//        R.drawable.welcome_banner_3
//    )
//
//    val verificationId = remember { mutableStateOf<String?>(null) }
//    val otpCode = remember { mutableStateOf(TextFieldValue("")) }
//    var isOtpSent by remember { mutableStateOf(false) }
//    val mobileNumber = remember { mutableStateOf(TextFieldValue("")) }
//
//    Box(
//        modifier = Modifier
//            .fillMaxSize()
//            .padding(16.dp)
//    ) {
//        Column(
//            modifier = Modifier.fillMaxSize()
//        ) {
//            // 🎨 Enhanced Top Bar
//            Column(
//                modifier = Modifier.fillMaxWidth(),
//                horizontalAlignment = Alignment.CenterHorizontally
//            ) {
//                TranslatedText(
//                    text = "Welcome to Upaay",
//                    style = MaterialTheme.typography.headlineLarge.copy(fontWeight = FontWeight.Bold),
//                    color = MaterialTheme.colorScheme.primary,
//                    textAlign = TextAlign.Center
//                )
//                Spacer(modifier = Modifier.height(4.dp))
//                TranslatedText(
//                    text = "India's Most Trusted Astrologers",
//                    style = MaterialTheme.typography.bodyMedium,
//                    color = Color.Gray,
//                    textAlign = TextAlign.Center
//                )
//            }
//
//            // 🌟 AutoScrollingBanner
//            Box(
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .padding(top = 10.dp)
//                    .weight(1f)
//            ) {
//                AutoScrollingBanner(
//                    modifier = Modifier.align(Alignment.Center),
//                    images = imageUrls,
//                    scrollInterval = 5000,
//                    boxHeight = LocalConfiguration.current.screenHeightDp / 2.5f,
//                    imageContentScale = ContentScale.FillBounds,
//                    imageStartPadding = 10
//                )
//            }
//
//            // 🔥 Login Card with Shadow
//            ElevatedCard(
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .padding(top = 16.dp),
//                elevation = CardDefaults.elevatedCardElevation(12.dp), // Increased for better depth
//                shape = RoundedCornerShape(16.dp),
//                colors = CardDefaults.elevatedCardColors(containerColor = Color.White) // Whitish Background
//            ) {
//                Column(
//                    modifier = Modifier.padding(10.dp, 20.dp)
//                ) {
//                    TranslatedText(
//                        text = "Enter Your Phone Number Below",
//                        style = MaterialTheme.typography.titleMedium,
//                        modifier = Modifier.align(Alignment.CenterHorizontally),
//                        textAlign = TextAlign.Center
//                    )
//
//                    // 📱 Mobile Number Field
//                    CrackinTextField(
//                        value = mobileNumber,
//                        label = "Enter your mobile number",
//                        isNumeric = true,
//                        leadingIcon = {
//                            Icon(
//                                imageVector = Icons.Filled.Phone,
//                                contentDescription = "Mobile Number",
//                                modifier = Modifier.size(20.dp)
//                            )
//                        },
//                        modifier = Modifier.padding(top = 10.dp)
//                    )
//
//                    if (isOtpSent) {
//                        // 🔢 OTP Field
//                        CrackinTextField(
//                            value = otpCode,
//                            label = "Enter OTP",
//                            isNumeric = true,
//                            modifier = Modifier.padding(top = 10.dp)
//                        )
//
//                        // ✅ Verify OTP Button
//                        Button(
//                            onClick = {
//                                handleSignIn(
//                                    context = context,
//                                    navController = navController,
//                                    auth = auth,
//                                    isEmail = false,
//                                    mobileNumber = mobileNumber.value.text,
//                                    verificationId = verificationId,
//                                    otp = otpCode.value.text
//                                )
//                            },
//                            colors = ButtonDefaults.buttonColors(
//                                containerColor = MaterialTheme.colorScheme.primary,
//                                contentColor = Color.White
//                            ),
//                            modifier = Modifier
//                                .fillMaxWidth()
//                                .padding(top = 20.dp)
//                        ) {
//                            TranslatedText(
//                                text = "Verify OTP",
//                                style = MaterialTheme.typography.titleSmall
//                            )
//                        }
//                    } else {
//                        // 🔄 Get OTP Button
//                        Button(
//                            onClick = {
//                                if (activity != null) {
//                                    if (mobileNumber.value.text.isNotEmpty()) {
//                                        if (isChecked) {
//                                            sendOtp(
//                                                context = context,
//                                                activity = activity,
//                                                auth = auth,
//                                                mobileNumber = mobileNumber.value.text,
//                                                verificationId = verificationId
//                                            ) {
//                                                isOtpSent = true
//                                            }
//                                        } else {
//                                            Toast.makeText(
//                                                context,
//                                                "You must accept our terms and policies before proceeding",
//                                                Toast.LENGTH_SHORT
//                                            ).show()
//                                        }
//                                    } else {
//                                        Toast.makeText(
//                                            context,
//                                            "Please enter your phone number",
//                                            Toast.LENGTH_SHORT
//                                        ).show()
//                                    }
//                                }
//                            },
//                            colors = ButtonDefaults.buttonColors(
//                                containerColor = MaterialTheme.colorScheme.primary,
//                                contentColor = Color.White
//                            ),
//                            modifier = Modifier
//                                .fillMaxWidth()
//                                .padding(top = 20.dp)
//                        ) {
//                            TranslatedText(
//                                text = "Get OTP",
//                                style = MaterialTheme.typography.titleSmall
//                            )
//                        }
//                    }
//
//                    // ➖ Divider with "or"
//                    Row(
//                        verticalAlignment = Alignment.CenterVertically,
//                        modifier = Modifier.padding(10.dp, 10.dp)
//                    ) {
//                        Spacer(
//                            modifier = Modifier
//                                .weight(1f)
//                                .height(1.dp)
//                                .background(Color.Gray.copy(alpha = 0.3f))
//                        )
//                        TranslatedText(
//                            text = "or",
//                            fontSize = 11.sp,
//                            modifier = Modifier.padding(10.dp, 0.dp)
//                        )
//                        Spacer(
//                            modifier = Modifier
//                                .weight(1f)
//                                .height(1.dp)
//                                .background(Color.Gray.copy(alpha = 0.3f))
//                        )
//                    }
//
//                    // 🔵 Google Sign-In Button
//                    Button(
//                        onClick = {
//                            if (isChecked) {
//                                LoadingStateManager.showLoading()
//                                val mainActivity = context as? Activity
//                                mainActivity?.let {
//                                    googleSignInClient.signOut()
//                                        .addOnCompleteListener {
//                                            googleSignInLauncher.launch(googleSignInClient.signInIntent)
//                                            LoadingStateManager.hideLoading()
//                                        }
//                                }
//                            } else {
//                                Toast.makeText(
//                                    context,
//                                    "You must accept our terms and policies before proceeding",
//                                    Toast.LENGTH_SHORT
//                                ).show()
//                            }
//                        },
//                        colors = ButtonDefaults.buttonColors(containerColor = Color.White),
//                        border = BorderStroke(1.dp, Color.Gray.copy(alpha = 0.2f)),
//                        modifier = Modifier
//                            .fillMaxWidth()
//                            .padding(top = 5.dp)
//                    ) {
//                        AsyncImage(
//                            model = "https://upload.wikimedia.org/wikipedia/commons/thumb/c/c1/Google_%22G%22_logo.svg/768px-Google_%22G%22_logo.svg.png",
//                            contentDescription = "Google Sign-In",
//                            modifier = Modifier.size(20.dp)
//                        )
//                        TranslatedText(
//                            text = "Sign In with Google",
//                            style = MaterialTheme.typography.bodyLarge,
//                            color = Color.Black,
//                            modifier = Modifier.padding(8.dp, 6.dp, 0.dp, 6.dp),
//                        )
//                    }
//
//                    TermsAgreementSection(
//                        isChecked = isChecked,
//                        onCheckedChange = { isChecked = it },
//                        onTermsClick = {
//                            navController.navigate(MainActivity.PoliciesTermsAndConditionsRoute)
//                        },
//                        onPrivacyClick = {
//                            // Navigate to Privacy screen
//                            navController.navigate(MainActivity.PoliciesPrivacyPolicyRoute)
//                        }
//                    )
//                }
//            }
//        }
//    }
//}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UpaayLoginTextField(
    value: MutableState<TextFieldValue>,
    label: String,
    isNumeric: Boolean = false,
    isPassword: Boolean = false,
    leadingIcon: @Composable (() -> Unit)? = null,
    modifier: Modifier = Modifier,
    errorText: String? = null
) {
    val visualTransformation =
        if (isPassword) PasswordVisualTransformation() else VisualTransformation.None

    val keyboardOptions = KeyboardOptions(
        keyboardType = when {
            isPassword -> KeyboardType.Password
            isNumeric -> KeyboardType.Number
            else -> KeyboardType.Text
        },
        imeAction = ImeAction.Done
    )

    Column(modifier = modifier.fillMaxWidth()) {
        OutlinedTextField(
            value = value.value,
            onValueChange = { value.value = it },
            label = { TranslatedText(label) },
            visualTransformation = visualTransformation,
            keyboardOptions = keyboardOptions,
            singleLine = true,
            leadingIcon = leadingIcon,
            isError = errorText != null,
            shape = RectangleShape,
            textStyle = LocalTextStyle.current.copy(fontSize = 15.sp),
            colors = TextFieldDefaults.outlinedTextFieldColors(
                focusedBorderColor = Color.Transparent,
                unfocusedBorderColor = Color.Transparent,
                disabledBorderColor = Color.Transparent,
                errorBorderColor = Color.Transparent,
                cursorColor = Color(0xFFF7A32B),
                focusedLabelColor = Color(0xFFF7A32B)
            ),
            modifier = Modifier
                .fillMaxWidth()
//                .height(56.dp)
                .drawBehind {
                    val strokeWidth = 2.dp.toPx()
                    val y = size.height - strokeWidth / 2
                    drawLine(
                        color = if (value.value.text.isNotEmpty()) Color(0xFFF7A32B) else Color.Gray,
                        start = Offset(0f, y),
                        end = Offset(size.width, y),
                        strokeWidth = strokeWidth
                    )
                }
        )

        errorText?.let {
            TranslatedText(
                text = it,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.labelSmall,
                modifier = Modifier.padding(start = 16.dp, top = 4.dp)
            )
        }
    }
}

@Composable
fun LanguageSelectorCard(
    selectedLanguage: String,
    tempSelectedLanguage: String,
    isLanguageDropdownOpen: Boolean,
    onToggleDropdown: () -> Unit,
    onLanguageSelected: (String) -> Unit,
    onApply: () -> Unit
) {
    val displayLanguage = when (selectedLanguage) {
        "hi" -> "Hindi"
        else -> "English"
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 6.dp, vertical = 8.dp)
            .clickable { onToggleDropdown() },
        elevation = CardDefaults.elevatedCardElevation(4.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            TranslatedText(
                text = "Language: $displayLanguage",
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp
                ),
                color = Color.Black,
                textAlign = TextAlign.Center
            )

            AnimatedVisibility(visible = isLanguageDropdownOpen) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    listOf("en" to "English", "hi" to "Hindi").forEach { (code, name) ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .clickable { onLanguageSelected(code) }
                                .padding(3.dp)
                        ) {
                            RadioButton(
                                selected = tempSelectedLanguage == code,
                                onClick = { onLanguageSelected(code) },
                                colors = RadioButtonDefaults.colors(
                                    selectedColor = Color(0xFFA24C13)
                                )
                            )
                            TranslatedText(
                                text = name,
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontWeight = if (tempSelectedLanguage == code) FontWeight.Bold else FontWeight.Normal
                                ),
                                modifier = Modifier.padding(start = 6.dp)
                            )
                        }
                    }

                    if (tempSelectedLanguage != selectedLanguage) {
                        Spacer(modifier = Modifier.height(12.dp))
                        Button(
                            onClick = onApply,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(45.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFFA24C13),
                                contentColor = Color.White
                            ),
                            shape = RoundedCornerShape(30.dp)
                        ) {
                            TranslatedText(
                                text = "Apply",
                                style = MaterialTheme.typography.labelLarge
                            )
                        }
                    }
                }
            }
        }
    }
}


@Composable
fun TermsAgreementSection(
    isChecked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    onTermsClick: () -> Unit,
    onPrivacyClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 16.dp),
        verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
    ) {
        Checkbox(
            checked = isChecked,
            onCheckedChange = onCheckedChange,
            colors = CheckboxDefaults.colors(
                checkedColor = Color(0xFFC95E16),
                uncheckedColor = Color(0xFFC95E16),
                checkmarkColor = Color.White
            )
        )

        Spacer(modifier = Modifier.width(8.dp))

        val annotatedText = buildAnnotatedString {
            append("By continuing, I have read and agree to the ")

            pushStringAnnotation(tag = "TERMS", annotation = "terms")
            withStyle(
                style = SpanStyle(
                    color = Color(0xFFF7A32B),
                    textDecoration = TextDecoration.Underline
                )
            ) { append("Terms and Conditions") }
            pop()

            append(" and ")

            pushStringAnnotation(tag = "PRIVACY", annotation = "privacy")
            withStyle(
                style = SpanStyle(
                    color = Color(0xFFF7A32B),
                    textDecoration = TextDecoration.Underline
                )
            ) { append("Privacy Policy") }
            pop()

            append(" of Upaay.")
        }

        ClickableText(
            text = annotatedText,
            style = MaterialTheme.typography.labelMedium,
            onClick = { offset ->
                annotatedText.getStringAnnotations(offset, offset).firstOrNull()?.let {
                    when (it.tag) {
                        "TERMS" -> onTermsClick()
                        "PRIVACY" -> onPrivacyClick()
                    }
                }
            }
        )
    }
}

// Newer UI to implement

//@Composable
//fun AuthSignInScreen(navigateToHomeScreen: () -> Unit) {
//
//    val mobileNumber = remember {
//        mutableStateOf(TextFieldValue(""))
//    }
//
//    Column(
//        modifier = Modifier
//            .fillMaxSize()
//            .background(Color(0xFFF2F2F2)),
//        verticalArrangement = Arrangement.Bottom,
//        horizontalAlignment = Alignment.CenterHorizontally
//    ) {
//
//        Card(
//            modifier = Modifier
//                .fillMaxWidth()
//                .padding(horizontal = 2.dp),
//            shape = RoundedCornerShape(topStart = 30.dp, topEnd = 30.dp),
//            colors = CardDefaults.cardColors(containerColor = Color.White)
//        ) {
//            Column(
//                modifier = Modifier
//                    .padding(start = 20.dp, end = 20.dp, bottom = 10.dp),
//                horizontalAlignment = Alignment.CenterHorizontally
//            ) {
//
//                AsyncImage(
//                    model = "https://play-lh.googleusercontent.com/cCVvEEwDQSPmDO8F-kryWhvzy53JllG1FGtHGDG-SDgfVTvvYSvWEXXDbgAY0hf7Bg",
//                    contentDescription = "Upaay Logo",
//                    modifier = Modifier
//                        .padding(top = 20.dp)
//                        .size(100.dp)
//                )
//
//                CrackinTextField(
//                    value = mobileNumber,
//                    label = "Enter your mobile number",
//                    isNumeric = true,
//                    leadingIcon = {
//                        Icon(
//                            imageVector = Icons.Filled.Phone,
//                            contentDescription = "Mobile Number",
//                            modifier = Modifier.size(20.dp)
//                        )
//                    },
//                    modifier = Modifier.padding(top = 10.dp)
//                )
//
//                Button(
//                    onClick = {
////                            if (activity != null) {
////                                if (mobileNumber.value.text.isNotEmpty()) {
////
////                                    sendOtp(
////                                        context = context,
////                                        activity = activity,
////                                        auth = auth,
////                                        mobileNumber = mobileNumber.value.text,
////                                        verificationId = verificationId
////                                    ) {
////                                        isOtpSent = true
////                                    }
////                                } else {
////                                    Toast.makeText(
////                                        context,
////                                        "Please enter your phone number",
////                                        Toast.LENGTH_SHORT
////                                    ).show()
////                                }
////                            }
//
//                        navigateToHomeScreen()
//                    },
//                    colors = ButtonDefaults.buttonColors(
//                        containerColor = Color(0xFF2A7BF5),
//                        contentColor = Color.White
//                    ),
//                    modifier = Modifier
//                        .fillMaxWidth()
//                        .padding(0.dp, 20.dp, 0.dp, 7.dp)
//                ) {
//                    TranslatedText(
//                        text = "Get OTP",
//                        style = MaterialTheme.typography.titleSmall,
//                        modifier = Modifier.padding(vertical = 5.dp)
//                    )
//                }
//
//                Row(
//                    verticalAlignment = Alignment.CenterVertically,
//                    modifier = Modifier
//                        .padding(0.dp, 10.dp)
//                ) {
//                    Spacer(
//                        modifier = Modifier
//                            .weight(1f)
//                            .height(1.dp)
//                            .background(Color.Gray.copy(alpha = 0.3f))
//                    )
//                    TranslatedText(text = "or", fontSize = 12.sp, modifier = Modifier.padding(10.dp, 0.dp))
//                    Spacer(
//                        modifier = Modifier
//                            .weight(1f)
//                            .height(1.dp)
//                            .background(Color.Gray.copy(alpha = 0.3f))
//                    )
//                }
//
//                Button(
//                    onClick = {
////                            val mainActivity = context as? Activity
////                            mainActivity?.let {
////                                googleSignInClient.signOut()
////                                    .addOnCompleteListener { // Ensure sign-out is completed
////                                        googleSignInLauncher.launch(googleSignInClient.signInIntent)
////                                    }
////                            }
//                    },
//                    colors = ButtonDefaults.buttonColors(
//                        containerColor = Color.White,
////                            contentColor = if (isSystemInDarkTheme()) Color.White else Color.Black
//                    ),
//                    border = BorderStroke(
//                        color = Color.Gray.copy(alpha = 0.2f),
//                        width = 1.dp,
//                    ),
//                    modifier = Modifier
//                        .fillMaxWidth()
//                        .padding(0.dp, 8.dp)
//                ) {
//                    AsyncImage(
//                        model = "https://upload.wikimedia.org/wikipedia/commons/thumb/c/c1/Google_%22G%22_logo.svg/768px-Google_%22G%22_logo.svg.png",
//                        contentDescription = "Google Sign-In",
//                        modifier = Modifier.size(20.dp)
//                    )
//                    TranslatedText(
//                        text = "Continue with Google",
//                        style = MaterialTheme.typography.bodyLarge,
//                        color = Color.Black,
//                        modifier = Modifier.padding(8.dp, 7.dp, 0.dp, 7.dp),
//                    )
//                }
//
//                Row(
//                    verticalAlignment = Alignment.CenterVertically,
//                    modifier = Modifier
//                        .padding(0.dp, 7.dp, 0.dp, 10.dp)
//                ) {
//                    TranslatedText(text = "By continuing, you agree to our ", fontSize = 11.sp)
//                    TranslatedText(text = "Terms and Conditions", fontSize = 12.sp, fontWeight = FontWeight.Bold)
//                }
//            }
//        }
//    }
//}
