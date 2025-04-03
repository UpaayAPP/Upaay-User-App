package com.solutions.upaay.screens.home.components.profile.components

import android.app.Activity
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.solutions.upaay.utils.profile.ProfileUtils
import com.solutions.upaay.utils.translate.TranslatedText
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.net.toUri
import coil.compose.AsyncImage
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.solutions.upaay.utils.loading.LoadingStateManager
import com.solutions.upaay.utils.translate.TranslatedText
import kotlinx.coroutines.tasks.await
import java.io.File
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.FlowRow
import com.solutions.upaay.screens.auth.newuser.profileinfo.screens.SelectableItem

@Composable
fun ProfileCard() {
    val profileUtils = ProfileUtils.userProfileState.value
    val context = LocalContext.current

    var showBottomSheet by remember { mutableStateOf(false) }
    var selectedField by remember { mutableStateOf("") }
    var selectedFieldValue by remember { mutableStateOf("") }
    var showImageDialog by remember { mutableStateOf(false) }

    val datePickerDialog = remember {
        DatePickerDialog(context, { _, year, month, day ->
            updateProfileField("dateOfBirth", "$day/${month + 1}/$year", context)
        }, 1995, 0, 1)
    }

    val timePickerDialog = remember {
        TimePickerDialog(context, { _, hour, minute ->
            updateProfileField("timeOfBirth", String.format("%02d:%02d", hour, minute), context)
        }, 12, 0, false)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier.size(110.dp),
            contentAlignment = Alignment.BottomEnd
        ) {
            AsyncImage(
                model = profileUtils.profileImageUrl
                    ?: "https://www.kindpng.com/picc/m/80-807524_no-profile-hd-png-download.png",
//                    ?: "https://i0.wp.com/static.vecteezy.com/system/resources/previews/036/280/650/original/default-avatar-profile-icon-social-media-user-image-gray-avatar-icon-blank-profile-silhouette-illustration-vector.jpg?ssl=1",
                contentDescription = "Profile",
                modifier = Modifier
                    .size(100.dp)
                    .clip(CircleShape)
                    .clickable { showImageDialog = true }
            )

            Icon(
                imageVector = Icons.Filled.Edit,
                contentDescription = "Edit Profile Picture",
                tint = Color.Gray,
                modifier = Modifier
                    .size(26.dp)
                    .background(Color.Transparent, shape = CircleShape)
                    .clickable { showImageDialog = true }
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        TranslatedText(
            text = profileUtils.name,
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )

        Spacer(modifier = Modifier.height(24.dp))

        Divider(color = Color.LightGray, thickness = 1.dp)

        Spacer(modifier = Modifier.height(20.dp))

        ProfileField("Date of Birth", profileUtils.dateOfBirth ?: "Not set") {
            datePickerDialog.show()
        }

        ProfileField("Time of Birth", profileUtils.timeOfBirth ?: "Not set") {
            timePickerDialog.show()
        }

        ProfileField("Place of Birth", profileUtils.placeOfBirth ?: "Not set") {
            selectedField = "placeOfBirth"
            selectedFieldValue = profileUtils.placeOfBirth ?: ""
            showBottomSheet = true
        }

        ProfileField("Current Address", profileUtils.currentAddress ?: "Not set") {
            selectedField = "currentAddress"
            selectedFieldValue = profileUtils.currentAddress ?: ""
            showBottomSheet = true
        }

        ProfileField("Horoscope", profileUtils.horoscope?.takeIf { it.isNotEmpty() } ?: "Not set") {
            selectedField = "horoscope"
            selectedFieldValue = profileUtils.horoscope ?: ""
            showBottomSheet = true
        }

        Spacer(modifier = Modifier.height(200.dp))
    }

    if (showImageDialog) {
        ProfileImagePickerDialog(
            onDismiss = { showImageDialog = false },
            onImagePicked = { uri -> uploadProfileImageToFirebase(uri, context) }
        )
    }

    if (showBottomSheet) {
        EditFieldBottomSheet(
            fieldName = selectedField,
            fieldValue = selectedFieldValue,
            onDismiss = { showBottomSheet = false },
            onUpdate = { newValue ->
                updateProfileField(selectedField, newValue, context)
                showBottomSheet = false
            }
        )
    }
}

@Composable
fun ProfileField(label: String, value: String, onEditClick: () -> Unit) {
    Column(modifier = Modifier.padding(vertical = 8.dp)) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.Medium,
            color = Color.Gray
        )
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = value,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.weight(1f)
            )

            TextButton(onClick = onEditClick) {
                Text("Edit", color = Color(0xFF2A7BF5))
            }
        }
    }
}

@Composable
fun EditableRow(label: String, value: String, onEditClick: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 10.dp)
    ) {
        TranslatedText(
            text = label,
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp
        )

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 5.dp)
        ) {
            TranslatedText(
                text = value,
                maxLines = 1,
                fontWeight = FontWeight.W400,
                fontSize = 14.sp,
                modifier = Modifier.weight(1f)
            )

            Text(
                text = "Edit",
                color = Color.Blue,
                fontSize = 14.sp,
                textDecoration = TextDecoration.Underline,
                modifier = Modifier.clickable { onEditClick() }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun EditFieldBottomSheet(
    fieldName: String,
    fieldValue: String,
    onDismiss: () -> Unit,
    onUpdate: (String) -> Unit
) {
    var newValue by remember { mutableStateOf(fieldValue) }
    val horoscopeOptions = listOf(
        "Aries (मेष)", "Taurus (वृषभ)", "Gemini (मिथुन)", "Cancer (कर्क)",
        "Leo (सिंह)", "Virgo (कन्या)", "Libra (तुला)", "Scorpio (वृश्चिक)",
        "Sagittarius (धनु)", "Capricorn (मकर)", "Aquarius (कुंभ)", "Pisces (मीन)"
    )
    var knowsHoroscope by remember { mutableStateOf<Boolean?>(if (fieldValue.isNotEmpty()) true else null) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        modifier = Modifier
            .fillMaxWidth()
            .defaultMinSize(minHeight = 300.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Text(
                text = "Edit ${fieldName.replaceFirstChar { it.uppercase() }}",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(16.dp))

            if (fieldName == "horoscope") {
                Text("Do you know your horoscope?", style = MaterialTheme.typography.bodyMedium)
                Spacer(Modifier.height(12.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {

                    Button(
                        onClick = { knowsHoroscope = true },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (knowsHoroscope == true) Color(0xFFE95432) else Color.Transparent,
                            contentColor = if (knowsHoroscope == true) Color.White else Color.Black
                        )
                    ) {
                        Text("Yes")
                    }

                    Button(
                        onClick = {
                            knowsHoroscope = false
                            newValue = ""
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (knowsHoroscope == false) Color(0xFFE95432) else Color.Transparent,
                            contentColor = if (knowsHoroscope == false) Color.White else Color.Black
                        )
                    ) {
                        Text("No")
                    }
                }

                if (knowsHoroscope == true) {
                    Spacer(Modifier.height(16.dp))
                    FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        horoscopeOptions.forEach {
                            SelectableItem(
                                text = it,
                                selected = newValue == it,
                                onDeselect = { newValue = "" },
                                onClick = { newValue = it }
                            )
                        }
                    }
                }
            } else {
                TextField(
                    value = newValue,
                    onValueChange = { newValue = it },
                    label = { Text("Enter new value") },
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            val isChanged = newValue != fieldValue

            Button(
                onClick = { onUpdate(newValue) },
                enabled = isChanged,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFA24C13),
                    contentColor = Color.White,
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Update", color = Color.White)
            }

            Spacer(modifier = Modifier.height(200.dp))
        }
    }
}


fun updateProfileField(fieldName: String, newValue: String, context: android.content.Context) {
    val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return
    val firestore = FirebaseFirestore.getInstance()

    val userRef = firestore.collection("users").document(userId)

    LoadingStateManager.showLoading()

    userRef.update(fieldName, newValue)
        .addOnSuccessListener {
            LoadingStateManager.hideLoading()
            ProfileUtils.userProfileState.value = when (fieldName) {
                "name" -> ProfileUtils.userProfileState.value.copy(name = newValue)
                "dateOfBirth" -> ProfileUtils.userProfileState.value.copy(dateOfBirth = newValue)
                "timeOfBirth" -> ProfileUtils.userProfileState.value.copy(timeOfBirth = newValue)
                "placeOfBirth" -> ProfileUtils.userProfileState.value.copy(placeOfBirth = newValue)
                "currentAddress" -> ProfileUtils.userProfileState.value.copy(currentAddress = newValue)
                "horoscope" -> ProfileUtils.userProfileState.value.copy(horoscope = newValue)
                else -> ProfileUtils.userProfileState.value // 🔹 Fallback (in case of unhandled fields)
            }
            Toast.makeText(context, "Updated Successfully!", Toast.LENGTH_SHORT).show()
        }
        .addOnFailureListener { e ->
            LoadingStateManager.hideLoading()
            Toast.makeText(context, "Something went wrong, please try again later", Toast.LENGTH_SHORT)
                .show()
        }
}

@Composable
fun ProfileImagePickerDialog(onDismiss: () -> Unit, onImagePicked: (Uri) -> Unit) {
    val context = LocalContext.current
    val launcherGallery =
        rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            uri?.let { onImagePicked(it) }
            onDismiss()
        }

    val launcherCamera =
        rememberLauncherForActivityResult(ActivityResultContracts.TakePicturePreview()) { bitmap ->
            bitmap?.let {
                val uri = saveBitmapToCache(it, context) // 🔥 Convert bitmap to URI
                onImagePicked(uri)
            }
            onDismiss()
        }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Update Profile Photo") },
        text = { Text("Choose an option") },
        confirmButton = {
            Column {
                Button(onClick = { launcherGallery.launch("image/*") }) {
                    Text("Choose from Gallery")
                }
                Spacer(modifier = Modifier.height(8.dp))
                Button(onClick = { launcherCamera.launch(null) }) {
                    Text("Take a Photo")
                }
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}

fun saveBitmapToCache(bitmap: Bitmap, context: Context): Uri {
    val file = File(context.cacheDir, "profile_temp.jpg")
    file.outputStream().use { bitmap.compress(Bitmap.CompressFormat.JPEG, 90, it) } // Compress
    return file.toUri()
}

fun uploadProfileImageToFirebase(uri: Uri, context: Context) {
    val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return
    val storageRef =
        FirebaseStorage.getInstance().reference.child("users/profileImages/$userId.jpg")

    LoadingStateManager.showLoading()
    storageRef.putFile(uri)
        .addOnSuccessListener {
            storageRef.downloadUrl.addOnSuccessListener { downloadUrl ->
                updateUserProfileImage(downloadUrl.toString())
            }
        }
        .addOnFailureListener { e ->
            Toast.makeText(
                context,
                "Something went wrong, please try again later",
                Toast.LENGTH_SHORT
            ).show()
            LoadingStateManager.hideLoading()
        }
}

fun updateUserProfileImage(imageUrl: String) {
    val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return
    val firestore = FirebaseFirestore.getInstance().collection("users").document(userId)

    firestore.update("profileImageUrl", imageUrl).addOnSuccessListener {
        ProfileUtils.userProfileState.value = ProfileUtils.userProfileState.value.copy(
            profileImageUrl = imageUrl
        )
        LoadingStateManager.hideLoading()
    }.addOnFailureListener {
        LoadingStateManager.hideLoading()
    }
}

