package com.solutions.upaay.screens.auth.newuser.profileinfo.screens
//
//import android.content.Context
//import android.widget.Toast
//import androidx.compose.foundation.background
//import androidx.compose.foundation.border
//import androidx.compose.foundation.clickable
//import androidx.compose.foundation.layout.Arrangement
//import androidx.compose.foundation.layout.ExperimentalLayoutApi
//import androidx.compose.foundation.layout.FlowRow
//import androidx.compose.foundation.layout.Row
//import androidx.compose.foundation.layout.Spacer
//import androidx.compose.foundation.layout.fillMaxWidth
//import androidx.compose.foundation.layout.height
//import androidx.compose.foundation.layout.padding
//import androidx.compose.foundation.layout.size
//import androidx.compose.foundation.lazy.LazyColumn
//import androidx.compose.foundation.shape.RoundedCornerShape
//import androidx.compose.material.icons.Icons
//import androidx.compose.material.icons.outlined.Close
//import androidx.compose.material3.Button
//import androidx.compose.material3.ButtonDefaults
//import androidx.compose.material3.CircularProgressIndicator
//import androidx.compose.material3.Icon
//import androidx.compose.material3.MaterialTheme
//import androidx.compose.material3.TextField
//import androidx.compose.material3.TextFieldDefaults
//import androidx.compose.runtime.Composable
//import androidx.compose.runtime.MutableState
//import androidx.compose.runtime.mutableStateOf
//import androidx.compose.runtime.remember
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.draw.clip
//import androidx.compose.ui.graphics.Color
//import androidx.compose.ui.text.font.FontWeight
//import androidx.compose.ui.text.input.TextFieldValue
//import androidx.compose.ui.unit.dp
//import androidx.navigation.NavController
//import com.google.firebase.auth.FirebaseAuth
//import com.google.firebase.firestore.FirebaseFirestore
//import com.google.firebase.firestore.SetOptions
//import com.solutions.upaay.MainActivity
//import com.solutions.upaay.globalComponents.components.CrackinTextField
//import com.solutions.upaay.utils.profile.ProfileUtils
//import com.solutions.upaay.utils.translate.TranslatedText
//
//@Composable
//fun UserDetails(context: Context, navController: NavController) {
//    val firestore = FirebaseFirestore.getInstance()
//    val profileUtils = ProfileUtils.userProfileState.value
//
//    val isLoading = remember {
//        mutableStateOf(false)
//    }
//
//    val dateOfBirth = remember {
//        mutableStateOf(TextFieldValue(if (profileUtils.dateOfBirth != null) profileUtils.dateOfBirth.toString() else ""))
//    }
//    val timeOfBirth = remember {
//        mutableStateOf(TextFieldValue(if (profileUtils.timeOfBirth != null) profileUtils.timeOfBirth.toString() else ""))
//    }
//    val placeOfBirth = remember {
//        mutableStateOf(TextFieldValue(if (profileUtils.placeOfBirth != null) profileUtils.placeOfBirth.toString() else ""))
//    }
//    val horoscope = remember {
//        mutableStateOf(TextFieldValue(if (profileUtils.horoscope != null) profileUtils.horoscope.toString() else ""))
//    }
//    val currentAddress = remember {
//        mutableStateOf(TextFieldValue(if (profileUtils.currentAddress != null) profileUtils.currentAddress.toString() else ""))
//    }
//
//    val language = remember {
//        mutableStateOf(TextFieldValue(""))
//    }
//    val languages = remember {  mutableStateOf<List<String>>(profileUtils.languages) }
//
//    LazyColumn(
//        modifier = Modifier.padding(horizontal = 5.dp)
//    ) {
//        item {
//
//            TranslatedText(
//                text = "Basic details about you",
//                style = MaterialTheme.typography.titleLarge,
//                modifier = Modifier.padding(top = 20.dp, start = 10.dp)
//            )
//
//            TranslatedText(
//                text = "Let astrologers have an idea when you contact them",
//                style = MaterialTheme.typography.labelLarge,
//                fontWeight = FontWeight.Light,
//                modifier = Modifier.padding(top = 10.dp, start = 10.dp)
//            )
//
//            DetailColumn(heading = "What is your date of birth *", value = dateOfBirth.value) {
//                dateOfBirth.value = it
//            }
//            DetailColumn(
//                heading = "What was the time at your birth? *",
//                value = timeOfBirth.value
//            ) {
//                timeOfBirth.value = it
//            }
//            DetailColumn(
//                heading = "What was the place of your birth? *",
//                value = placeOfBirth.value
//            ) {
//                placeOfBirth.value = it
//            }
//            DetailColumn(
//                heading = "What is your horoscope? (Optional)",
//                value = horoscope.value
//            ) {
//                horoscope.value = it
//            }
//            DetailColumn(
//                heading = "What's your current address? *",
//                value = currentAddress.value
//            ) {
//                currentAddress.value = it
//            }
//
//            SelectableListSkillsValues(
//                "Select or type languages you are comfortable in * -",
//                language,
//                languages
//            )
//
//            Row(
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .padding(20.dp, 40.dp, 20.dp, 0.dp)
//            ) {
//
//                Spacer(modifier = Modifier.weight(1f))
//
//                Button(
//                    onClick = {
//                        if (dateOfBirth.value.text.isEmpty() || timeOfBirth.value.text.isEmpty() || placeOfBirth.value.text.isEmpty() || currentAddress.value.text.isEmpty() || languages.value.isEmpty()) {
//                            Toast.makeText(
//                                context,
//                                "Please complete all fields in the education details form.",
//                                Toast.LENGTH_SHORT
//                            ).show()
//                        } else {
//                            // store these details and move to MainActivity again
//                            val updatedData = mapOf(
//                                "dateOfBirth" to dateOfBirth.value.text,
//                                "timeOfBirth" to timeOfBirth.value.text,
//                                "placeOfBirth" to placeOfBirth.value.text,
//                                "currentAddress" to currentAddress.value.text,
//                                "horoscope" to horoscope.value.text,
//                                "languages" to languages.value
//                            )
//                            val uid = FirebaseAuth.getInstance().currentUser?.uid
//                            if (uid != null) {
//                                firestore.collection("users").document(uid)
//                                    .set(updatedData, SetOptions.merge())
//                                    .addOnSuccessListener {
//                                        isLoading.value = false
//                                        navController.navigate(MainActivity.HomeScreenRoute(true))
//                                    }
//                                    .addOnFailureListener { e ->
//                                        isLoading.value = false
//                                        Toast.makeText(
//                                            context,
//                                            "Please try again",
//                                            Toast.LENGTH_SHORT
//                                        )
//                                            .show()
//                                    }
//                            }
//                        }
//                    },
//                    colors = ButtonDefaults.buttonColors(
//                        containerColor = Color(0xFF2A7BF5),
//                        contentColor = Color.White
//                    ),
//                ) {
//                    if (isLoading.value)
//                        CircularProgressIndicator(modifier = Modifier.size(20.dp))
//                    else
//                        TranslatedText(
//                            text = "Save & Continue",
//                            style = MaterialTheme.typography.titleSmall
//                        )
//                }
//            }
//
//            Spacer(modifier = Modifier.height(300.dp))
//        }
//    }
//}
//
//@Composable
//fun DetailColumn(
//    heading: String,
//    value: TextFieldValue,
//    onChange: (value: TextFieldValue) -> Unit
//) {
//    TranslatedText(
//        text = heading,
//        style = MaterialTheme.typography.labelLarge,
//        fontWeight = FontWeight.Normal,
//        modifier = Modifier.padding(top = 30.dp, start = 10.dp)
//    )
//
//    TextField(
//        value = value,
//        onValueChange = {
//            onChange(it) // Pass the updated TextFieldValue directly
//        },
//        colors = TextFieldDefaults.colors(
//            unfocusedContainerColor = Color.Transparent,
//            focusedContainerColor = Color.Transparent,
//        ),
//        label = {
//            TranslatedText(
//                "Type here...",
//                style = MaterialTheme.typography.bodySmall
//            )
//        },
//        modifier = Modifier
//            .fillMaxWidth()
//            .padding(5.dp, 0.dp)
//    )
//}
//
//@Composable
//fun SelectableListSkillsValues(
//    heading: String,
//    skills: MutableState<TextFieldValue>,
//    keySkills: MutableState<List<String>>,
//) {
//    TranslatedText(
//        text = heading,
//        style = MaterialTheme.typography.labelLarge,
//        fontWeight = FontWeight.Normal,
//        modifier = Modifier.padding(top = 30.dp, start = 10.dp)
//    )
//
//    CrackinTextField(
//        value = skills,
//        label = "Enter Language Name",
//        placeholder = "Type here then click on Add Language",
//        modifier = Modifier.padding(5.dp, 5.dp, 5.dp, 0.dp)
//    )
//
//    Button(
//        onClick = {
//            val newSkill = skills.value.text.trim()
//            if (newSkill.isNotEmpty() && !keySkills.value.contains(newSkill)) {
//                // Update the entire list to trigger recomposition
//                keySkills.value += newSkill
//                skills.value = TextFieldValue("") // Clear the text field
//            }
//        },
//        modifier = Modifier.padding(top = 5.dp, start = 5.dp)
//    ) {
//        TranslatedText(text = "Add Language")
//    }
//
//    // Display suggested skills
//    SuggestedValuesForLanguage(
//        keySkills.value,
//        { selectedSkill -> keySkills.value -= selectedSkill }) { selectedSkill ->
//        if (!keySkills.value.contains(selectedSkill)) {
//            // Update the entire list to trigger recomposition
//            keySkills.value += selectedSkill
//        }
//    }
//
//    TranslatedText(
//        text = "Selected languages",
//        style = MaterialTheme.typography.labelMedium,
//        fontWeight = FontWeight.Normal,
//        modifier = Modifier.padding(top = 20.dp, start = 10.dp)
//    )
//
//    TranslatedText(
//        text = if (keySkills.value.isEmpty()) "No Language Selected Yet" else keySkills.value.joinToString(
//            ", "
//        ),
//        style = MaterialTheme.typography.bodyLarge,
//        modifier = Modifier.padding(top = 10.dp, start = 10.dp)
//    )
//
//}
//
//@OptIn(ExperimentalLayoutApi::class)
//@Composable
//fun SuggestedValuesForLanguage(
//    addedSkills: List<String>,
//    onDeselect: (value: String) -> Unit,
//    onSelect: (value: String) -> Unit
//) {
//    TranslatedText(
//        text = "Suggested Languages",
//        style = MaterialTheme.typography.labelMedium,
//        fontWeight = FontWeight.Normal,
//        modifier = Modifier.padding(top = 10.dp, start = 10.dp)
//    )
//
//    FlowRow(
//        modifier = Modifier.padding(top = 15.dp, start = 10.dp),
//        verticalArrangement = Arrangement.spacedBy(10.dp),
//        horizontalArrangement = Arrangement.spacedBy(10.dp)
//    ) {
//        val suggestedSkills =
//            listOf("English", "Hindi", "Punjabi", "Haryanavi", "Marathi", "Bengali")
//
//        // Display selectable skills
//        suggestedSkills.forEach { skill ->
//            SelectableItem(
//                text = skill,
//                selected = addedSkills.contains(skill),
//                onDeselect = { onDeselect(skill) },
//                onClick = { onSelect(skill) }
//            )
//        }
//    }
//}
//
//
//@Composable
//fun SelectableItem(
//    modifier: Modifier = Modifier,
//    text: String,
//    selected: Boolean = false,
//    showCloseIcon: Boolean = true,
//    onDeselect: () -> Unit,
//    onClick: () -> Unit
//) {
//
//    Row(
//        verticalAlignment = Alignment.CenterVertically,
//        horizontalArrangement = Arrangement.spacedBy(10.dp),
//        modifier = modifier
//            .border(
//                1.dp,
//                if (selected) Color(0xFFC26941) else Color.Gray,
//                RoundedCornerShape(30.dp)
//            )
//            .clickable {
//                if (selected && !showCloseIcon) {
//                    onDeselect()
//                } else {
//                    if (!selected) {
//                        onClick()
//                    }
//                }
//            }
//            .clip(RoundedCornerShape(30.dp))
//            .background(if (selected) Color(0xFFC26941).copy(0.3f) else Color.Transparent)
//            .padding(15.dp, 8.dp)
//    ) {
//
//        TranslatedText(
//            text = text,
//            style = MaterialTheme.typography.bodyMedium,
//            maxLines = 1,
//            fontWeight = FontWeight.Normal
//        )
//
//        if (selected && showCloseIcon) {
//            Icon(
//                imageVector = Icons.Outlined.Close, contentDescription = "Unselect",
//                modifier = Modifier
//                    .size(20.dp)
//                    .clickable {
//                        onDeselect()
//                    }
//            )
//        }
//    }
//}
