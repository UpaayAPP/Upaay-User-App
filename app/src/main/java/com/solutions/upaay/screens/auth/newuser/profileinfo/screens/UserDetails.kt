package com.solutions.upaay.screens.auth.newuser.profileinfo.screens

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.solutions.upaay.MainActivity
import com.solutions.upaay.globalComponents.components.CrackinTextField
import com.solutions.upaay.utils.profile.ProfileUtils
import com.solutions.upaay.utils.translate.TranslatedText

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun UserDetails(context: Context, navController: NavController) {
    val firestore = FirebaseFirestore.getInstance()
    val profile = ProfileUtils.userProfileState.value
    val scope = rememberCoroutineScope()

    var currentStep by remember { mutableStateOf(1) }
    val isLoading = remember { mutableStateOf(false) }

    val dateOfBirth = remember { mutableStateOf(profile.dateOfBirth ?: "") }
    val timeOfBirth = remember { mutableStateOf(profile.timeOfBirth ?: "") }
    val placeOfBirth = remember { mutableStateOf(TextFieldValue(profile.placeOfBirth ?: "")) }
    val currentAddress = remember { mutableStateOf(TextFieldValue(profile.currentAddress ?: "")) }
    val knowsHoroscope = remember { mutableStateOf<Boolean?>(null) }
    val horoscope = remember { mutableStateOf(profile.horoscope ?: "") }
    val horoscopeList = listOf(
        "Aries (मेष)", "Taurus (वृषभ)", "Gemini (मिथुन)", "Cancer (कर्क)",
        "Leo (सिंह)", "Virgo (कन्या)", "Libra (तुला)", "Scorpio (वृश्चिक)",
        "Sagittarius (धनु)", "Capricorn (मकर)", "Aquarius (कुंभ)", "Pisces (मीन)"
    )

    val language = remember { mutableStateOf(TextFieldValue("")) }
    val languages = remember { mutableStateOf<List<String>>(profile.languages) }

    val datePickerDialog = remember {
        DatePickerDialog(context, { _, year, month, dayOfMonth ->
            dateOfBirth.value = "$dayOfMonth/${month + 1}/$year"
        }, 2000, 0, 1)
    }

    val timePickerDialog = remember {
        TimePickerDialog(context, { _, hourOfDay, minute ->
            timeOfBirth.value = String.format("%02d:%02d", hourOfDay, minute)
        }, 12, 0, false)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,

        ) {

        // Back Button
        if (currentStep > 1) {
            Icon(
                imageVector = Icons.Default.ArrowBack,
                contentDescription = "Back",
                modifier = Modifier
                    .clickable { currentStep-- }
                    .padding(bottom = 16.dp)
            )
        }

        when (currentStep) {

            // Step 1: DOB and TOB
            1 -> {
                TranslatedText(
                    "When were you born?",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                Spacer(Modifier.height(24.dp))
                Button(
                    onClick = { datePickerDialog.show() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(
                            width = if (dateOfBirth.value.isNotEmpty()) 1.dp else 0.dp,
                            color = if (dateOfBirth.value.isNotEmpty()) Color(0xFFA24C13) else Color.Transparent,
                            shape = MaterialTheme.shapes.medium
                        ),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (dateOfBirth.value.isNotEmpty()) Color.White else Color(
                            0xFFA24C13
                        ),
                        contentColor = if (dateOfBirth.value.isNotEmpty()) Color.Black else Color.White
                    )
                ) {
                    Text(
                        text = if (dateOfBirth.value.isNotEmpty()) dateOfBirth.value else "Select Date of Birth"
                    )
                }

                Spacer(Modifier.height(16.dp))

                Button(
                    onClick = { timePickerDialog.show() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(
                            width = if (timeOfBirth.value.isNotEmpty()) 1.dp else 0.dp,
                            color = if (timeOfBirth.value.isNotEmpty()) Color(0xFFA24C13) else Color.Transparent,
                            shape = MaterialTheme.shapes.medium
                        ),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (timeOfBirth.value.isNotEmpty()) Color.White else Color(
                            0xFFA24C13
                        ),
                        contentColor = if (timeOfBirth.value.isNotEmpty()) Color.Black else Color.White
                    )
                ) {
                    Text(
                        text = if (timeOfBirth.value.isNotEmpty()) timeOfBirth.value else "Select Time of Birth"
                    )
                }

                Spacer(Modifier.height(32.dp))

                Button(
                    onClick = { currentStep++ },
                    enabled = dateOfBirth.value.isNotEmpty() && timeOfBirth.value.isNotEmpty(),
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (dateOfBirth.value.isNotEmpty() && timeOfBirth.value.isNotEmpty()) Color(
                            0xFFA24C13
                        ) else Color(0xFFA24C13).copy(alpha = 0.4f),
                        contentColor = Color.White,
                    )
                ) {
                    Text("Next")
                }
            }

            // Step 2: Place and Address
            2 -> {
                TranslatedText(
                    "Where were you born?",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                Spacer(Modifier.height(16.dp))

                DetailColumn("Place of Birth", placeOfBirth.value) { placeOfBirth.value = it }
                DetailColumn("Current Address", currentAddress.value) { currentAddress.value = it }

                Spacer(Modifier.height(32.dp))
                Button(
                    onClick = { currentStep++ },
                    enabled = placeOfBirth.value.text.isNotBlank() && currentAddress.value.text.isNotBlank(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFA24C13),
                        contentColor = Color.White,
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Next")
                }
            }

            // Step 3: Horoscope Optional
            3 -> {
                TranslatedText(
                    "Do you know your Horoscope?",
                    style = MaterialTheme.typography.titleLarge
                )
                Spacer(Modifier.height(24.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    Button(
                        onClick = {
                            knowsHoroscope.value = true
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFA24C13),
                            contentColor = Color.White,
                        ),
                    ) { Text("Yes") }

                    Button(
                        onClick = {
                            knowsHoroscope.value = false
                            currentStep++
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFA24C13),
                            contentColor = Color.White,
                        ),
                    ) { Text("No") }
                }

                knowsHoroscope.value?.let { knows ->
                    if (knows) {
                        Spacer(Modifier.height(20.dp))
                        TranslatedText(
                            "Select your Horoscope",
                            style = MaterialTheme.typography.labelLarge
                        )
                        Spacer(Modifier.height(10.dp))
                        FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            horoscopeList.forEach {
                                SelectableItem(
                                    text = it,
                                    selected = it == horoscope.value,
                                    onDeselect = { horoscope.value = "" },
                                    onClick = { horoscope.value = it }
                                )
                            }
                        }

                        Spacer(Modifier.height(32.dp))
                        Button(
                            onClick = { currentStep++ },
                            enabled = horoscope.value.isNotEmpty(),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFFF7A32B),
                                contentColor = Color.White,
                            ),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Next")
                        }
                    }
                }
            }

            // Step 4: Languages + Final Save
            4 -> {
                TranslatedText(
                    "What languages are you comfortable in?",
                    style = MaterialTheme.typography.titleLarge
                )
                SmartLanguageSelector(
                    skills = language,
                    keySkills = languages
                )

                Spacer(Modifier.height(24.dp))
                Button(
                    onClick = {
                        if (languages.value.isEmpty()) {
                            Toast.makeText(
                                context,
                                "Please select at least one language",
                                Toast.LENGTH_SHORT
                            ).show()
                        } else {
                            isLoading.value = true
                            val updatedData = mapOf(
                                "dateOfBirth" to dateOfBirth.value,
                                "timeOfBirth" to timeOfBirth.value,
                                "placeOfBirth" to placeOfBirth.value.text,
                                "currentAddress" to currentAddress.value.text,
                                "horoscope" to if (knowsHoroscope.value == true) horoscope.value else "",
                                "languages" to languages.value
                            )
                            val uid = FirebaseAuth.getInstance().currentUser?.uid
                            if (uid != null) {
                                firestore.collection("users").document(uid)
                                    .set(updatedData, SetOptions.merge())
                                    .addOnSuccessListener {
                                        isLoading.value = false
                                        navController.navigate(MainActivity.HomeScreenRoute(true)) {
                                            popUpTo(0) { inclusive = true } // clears entire backstack
                                        }
                                    }
                                    .addOnFailureListener {
                                        isLoading.value = false
                                        Toast.makeText(
                                            context,
                                            "Failed. Try again.",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                            }else{
                                isLoading.value = false
                                Toast.makeText(
                                    context,
                                    "Failed. Try again.",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    },
                    enabled = languages.value.isNotEmpty(),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2A7BF5)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    if (isLoading.value) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            color = Color.White
                        )
                    } else {
                        Text("Save & Continue", color = Color.White)
                    }
                }

//                Spacer(modifier = Modifier.height(200.dp))
            }
        }
    }
}



@Composable
fun DetailColumn(
    heading: String,
    value: TextFieldValue,
    onChange: (value: TextFieldValue) -> Unit
) {
    TranslatedText(
        text = heading,
        style = MaterialTheme.typography.labelLarge,
        fontWeight = FontWeight.Normal,
        modifier = Modifier.padding(top = 30.dp, start = 10.dp)
    )

    TextField(
        value = value,
        onValueChange = {
            onChange(it) // Pass the updated TextFieldValue directly
        },
        colors = TextFieldDefaults.colors(
            unfocusedContainerColor = Color.Transparent,
            focusedContainerColor = Color.Transparent,
        ),
        label = {
            TranslatedText(
                "Type here...",
                style = MaterialTheme.typography.bodySmall
            )
        },
        modifier = Modifier
            .fillMaxWidth()
            .padding(5.dp, 0.dp)
    )
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun SmartLanguageSelector(
    skills: MutableState<TextFieldValue>,
    keySkills: MutableState<List<String>>
) {
    val suggestedSkillsBase = listOf("English", "Hindi", "Punjabi", "Haryanavi", "Marathi", "Bengali")
    var showCustomInput by remember { mutableStateOf(false) }

    // Merge: user-added (if any) + suggestions (minus duplicates)
    val allSuggestions = remember(keySkills.value) {
        keySkills.value + suggestedSkillsBase.filterNot { keySkills.value.contains(it) }
    }

    TranslatedText(
        text = "Choose your preferred languages",
        style = MaterialTheme.typography.labelLarge,
        fontWeight = FontWeight.Normal,
        modifier = Modifier.padding(top = 10.dp, start = 10.dp)
    )

    FlowRow(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 15.dp, start = 10.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        allSuggestions.forEach { language ->
            val selected = keySkills.value.contains(language)
            SelectableItem(
                text = language,
                selected = selected,
                onDeselect = {
                    keySkills.value = keySkills.value - language
                },
                onClick = {
                    keySkills.value = keySkills.value + language
                }
            )
        }
    }

    Spacer(Modifier.height(10.dp))

    if (!showCustomInput) {
        Text(
            text = "Choose another language?",
            style = MaterialTheme.typography.bodyMedium.copy(color = Color(0xFFE95432)),
            modifier = Modifier
                .padding(horizontal = 10.dp)
                .clickable { showCustomInput = true }
        )
    } else {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 10.dp, vertical = 5.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            TextField(
                value = skills.value,
                onValueChange = { skills.value = it },
                placeholder = { Text("Enter language") },
                modifier = Modifier.weight(1f),
                singleLine = true,
                colors = TextFieldDefaults.colors(
                    unfocusedContainerColor = Color.Transparent,
                    focusedContainerColor = Color.Transparent
                )
            )

            Button(onClick = {
                val lang = skills.value.text.trim()
                if (lang.isNotEmpty() && !keySkills.value.contains(lang)) {
                    keySkills.value = listOf(lang) + keySkills.value // Add to start
                    skills.value = TextFieldValue("")
                }
            },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFE95432),
                    contentColor = Color.White
                )

                ) {
                Text("Add")
            }
        }
    }
}


@Composable
fun SelectableItem(
    modifier: Modifier = Modifier,
    text: String,
    selected: Boolean = false,
    showCloseIcon: Boolean = true,
    onDeselect: () -> Unit,
    onClick: () -> Unit
) {

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        modifier = modifier
            .border(
                1.dp,
                if (selected) Color(0xFFC26941) else Color.Gray,
                RoundedCornerShape(30.dp)
            )
            .clickable {
                if (selected && !showCloseIcon) {
                    onDeselect()
                } else {
                    if (!selected) {
                        onClick()
                    }
                }
            }
            .clip(RoundedCornerShape(30.dp))
            .background(if (selected) Color(0xFFC26941).copy(0.3f) else Color.Transparent)
            .padding(15.dp, 8.dp)
    ) {

        TranslatedText(
            text = text,
            style = MaterialTheme.typography.bodyMedium,
            maxLines = 1,
            fontWeight = FontWeight.Normal
        )

        if (selected && showCloseIcon) {
            Icon(
                imageVector = Icons.Outlined.Close, contentDescription = "Unselect",
                modifier = Modifier
                    .size(20.dp)
                    .clickable {
                        onDeselect()
                    }
            )
        }
    }
}
