package com.solutions.upaay.screens.auth.forgotPassword

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.solutions.upaay.globalComponents.components.CrackinTextField
import com.solutions.upaay.utils.loading.LoadingStateManager
import com.google.firebase.auth.FirebaseAuth
import com.solutions.upaay.utils.translate.TranslatedText

@Composable
fun AuthForgotPassword(navController: NavHostController) {
    val auth = FirebaseAuth.getInstance()
    val email = remember { mutableStateOf(TextFieldValue("")) }
    val context = LocalContext.current

    Column(modifier = Modifier.padding(16.dp, 35.dp, 16.dp, 20.dp)) {
        TranslatedText(
            text = "Forgot Password?",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        TranslatedText(
            text = "Enter your registered email address. We'll send you a link to reset your password.",
            style = MaterialTheme.typography.labelMedium,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        CrackinTextField(
            value = email,
            label = "Email Address",
            isEmail = true,
            leadingIcon = {
                Icon(
                    imageVector = Icons.Filled.Email,
                    contentDescription = "Email",
                    modifier = Modifier.size(20.dp)
                )
            },
            modifier = Modifier.fillMaxWidth()
        )

        Button(
            onClick = {
                if (email.value.text.isNotEmpty()) {
                    sendPasswordResetEmail(
                        context,
                        auth,
                        email.value.text.trim(),
                        navController
                    )
                } else {
                    Toast.makeText(context, "Please enter your email address.", Toast.LENGTH_SHORT)
                        .show()
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp)
        ) {
            TranslatedText(text = "Send Reset Link")
        }
    }
}

fun sendPasswordResetEmail(
    context: Context,
    auth: FirebaseAuth,
    email: String,
    navController: NavHostController
) {
    LoadingStateManager.showLoading()
    auth.sendPasswordResetEmail(email)
        .addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Toast.makeText(
                    context,
                    "Password reset link sent to $email. Check your inbox.",
                    Toast.LENGTH_LONG
                ).show()
                LoadingStateManager.hideLoading()
                navController.popBackStack() // Navigate back to the previous screen
            } else {
                val exception = task.exception
                val errorMessage = exception?.localizedMessage ?: "Failed to send reset link."
                Toast.makeText(context, errorMessage, Toast.LENGTH_LONG).show()
                LoadingStateManager.hideLoading()
            }
        }
}