package com.solutions.upaay.screens.auth.newuser.basicinfo

import android.content.Context
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Password
import androidx.compose.material.icons.filled.Person2
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.google.firebase.Timestamp
import com.solutions.upaay.MainActivity
import com.solutions.upaay.globalComponents.components.CrackinTextField
import com.solutions.upaay.utils.loading.LoadingStateManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.solutions.upaay.utils.translate.TranslatedText

val auth = FirebaseAuth.getInstance()

fun saveUserToDatabase(context : Context, uid:String, fullName:String, email:String, phoneNumber:String, navController: NavHostController){
    LoadingStateManager.showLoading()

    if(fullName.isEmpty() || email.isEmpty() || phoneNumber.isEmpty()){
        LoadingStateManager.hideLoading()
        Toast.makeText(context, "Please fill all the fields", Toast.LENGTH_SHORT).show()
        return
    }

    val userData = mapOf(
        "uid" to uid,
        "name" to fullName,
        "email" to email,
        "mobileNumber" to phoneNumber,
        "balance" to 0,
        "profileImageUrl" to null,
        // Other fields are left as null or empty and can be updated later
        "dateOfBirth" to null,
        "timeOfBirth" to null,
        "placeOfBirth" to null,
        "currentAddress" to null,
        "horoscope" to null,
        "languages" to emptyList<String>(),
        "createdAt" to Timestamp.now(),
        "activeDeviceId" to Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID) // ✅ here
    )

    val firestore = FirebaseFirestore.getInstance()
    firestore.collection("users").document(uid)
        .set(userData, SetOptions.merge())
        .addOnSuccessListener {
            context.getSharedPreferences("upaay_user", Context.MODE_PRIVATE)
                .edit().putString("deviceId", userData["activeDeviceId"] as String).apply()

            navController.navigate(MainActivity.HomeScreenRoute(false)) {
                popUpTo(0) { inclusive = true } // clears entire backstack
            }
            LoadingStateManager.hideLoading()
        }
        .addOnFailureListener {
            Toast.makeText(
                context,
                "Something went wrong",
                Toast.LENGTH_SHORT
            ).show()
            LoadingStateManager.hideLoading()
        }
}

@Composable
fun CreateUser(navController: NavHostController) {

    val context = LocalContext.current

    val fullName = remember {
        mutableStateOf(TextFieldValue(auth.currentUser?.displayName ?:""))
    }
    val email = remember {
        mutableStateOf(TextFieldValue(auth.currentUser?.email ?:""))
    }
    val mobileNumber = remember {
        mutableStateOf(TextFieldValue(auth.currentUser?.phoneNumber ?:""))
    }

    Column(modifier = Modifier.padding(15.dp, 35.dp, 15.dp, 20.dp)) {
        TranslatedText(
            text = "Tell us about yourself",
            style = MaterialTheme.typography.headlineMedium
        )

        TranslatedText(
            text = "Your name, Email are cruicial for us to get you started",
//            text = "Give assessments, train for interviews and apply to jobs with 90% success rate",
            style = MaterialTheme.typography.labelMedium,
            fontSize = 13.sp,
            modifier = Modifier.padding(top = 7.dp)
        )

        CrackinTextField(
            isEnabled = auth.currentUser?.displayName.isNullOrEmpty(),
            value = fullName,
            leadingIcon = {
                Icon(
                    imageVector = Icons.Filled.Person2,
                    contentDescription = "Full Name",
                    modifier = Modifier.size(20.dp)
                )
            },
            label = "Full Name *",
            isEmail = true,
            modifier = Modifier
                .padding(top = 25.dp)
        )

        CrackinTextField(
            isEnabled = auth.currentUser?.email.isNullOrEmpty(),
            value = email,
            leadingIcon = {
                Icon(
                    imageVector = Icons.Filled.Email,
                    contentDescription = "Email ID",
                    modifier = Modifier.size(20.dp)
                )
            },
            label = "Email ID *",
            isEmail = true,
            modifier = Modifier
                .padding(top = 20.dp)
        )
        TranslatedText(
            text = "You'll receive updates about conversions on this mail",
            style = MaterialTheme.typography.labelSmall,
            modifier = Modifier.padding(top = 7.dp)
        )

        CrackinTextField(
            isEnabled = auth.currentUser?.phoneNumber.isNullOrEmpty(),
            value = mobileNumber,
            leadingIcon = {
                Icon(
                    imageVector = Icons.Filled.Phone,
                    contentDescription = "Mobile Number",
                    modifier = Modifier.size(20.dp)
                )
            },
            label = "Mobile Number *",
            isNumeric = true,
            modifier = Modifier
                .padding(top = 20.dp)
        )
        TranslatedText(
            text = "This is where we will contact you. This will not be shared with any user.",
            style = MaterialTheme.typography.labelSmall,
            modifier = Modifier.padding(top = 7.dp)
        )

        Button(
            onClick = {
                saveUserToDatabase(context, auth.currentUser?.uid.toString(), fullName.value.text, email.value.text, mobileNumber.value.text, navController)
//                navController.navigate(MainActivity.HomeScreenRoute)
            },
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFFA24C13),
                contentColor = Color.White
            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(5.dp, 35.dp, 5.dp, 7.dp)
        ) {

            TranslatedText(text = "Save and Continue", style = MaterialTheme.typography.titleSmall)
        }
    }
}