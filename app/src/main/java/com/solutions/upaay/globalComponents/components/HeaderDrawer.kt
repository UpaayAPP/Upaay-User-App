package com.solutions.upaay.globalComponents.components

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.outlined.AssignmentReturn
import androidx.compose.material.icons.outlined.BarChart
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.GraphicEq
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.LocalLibrary
import androidx.compose.material.icons.outlined.Logout
import androidx.compose.material.icons.outlined.PlayCircleOutline
import androidx.compose.material.icons.outlined.Policy
import androidx.compose.material.icons.rounded.Book
import androidx.compose.material.icons.rounded.ExpandLess
import androidx.compose.material.icons.rounded.ExpandMore
import androidx.compose.material.icons.rounded.HelpOutline
import androidx.compose.material.icons.rounded.Language
import androidx.compose.material.icons.rounded.LightMode
import androidx.compose.material.icons.rounded.Nightlight
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material.icons.rounded.Policy
import androidx.compose.material.icons.rounded.RemoveRedEye
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material.icons.rounded.VerifiedUser
import androidx.compose.material.icons.rounded.WorkOutline
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.google.firebase.auth.FirebaseAuth
import com.solutions.upaay.MainActivity
import com.solutions.upaay.screens.home.handleChangeSelectedItem
import com.solutions.upaay.utils.profile.ProfileUtils
import com.solutions.upaay.utils.translate.TranslatableText
import com.solutions.upaay.utils.translate.TranslatedText

@Composable
fun HeaderDrawer(
    auth: FirebaseAuth,
    isCurrentThemeDark: Boolean,
    toggleUiTheme: () -> Unit,
    selectedLanguage: String, updateLanguage: (String) -> Unit,
    navController: NavController,
    closeDrawer: () -> Unit
) {
    val context = LocalContext.current
    val sharedPreferences = context.getSharedPreferences("UserSettings", Context.MODE_PRIVATE)

    var isLanguageDropdownOpen by remember { mutableStateOf(false) }
    var tempSelectedLanguage by remember { mutableStateOf(selectedLanguage) }

//    var currentLanguage by remember {
//        mutableStateOf(
//            sharedPreferences.getString(
//                "language",
//                "English"
//            ) ?: "English"
//        )
//    }
//
//    val languageMap = mapOf(
//        "English" to "en",
//        "Hindi" to "hi",
//        "Telugu" to "te",
//        "Tamil" to "ta",
//        "Gujarati" to "gu",
//        "Marathi" to "mr"
//    )

//    var showLanguageDialog by remember { mutableStateOf(false) } // 🔥 Controls Dialog Visibility


    ModalDrawerSheet(
    ) {

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.surface)
                .padding(15.dp)
        ) {
            // Drawer Header

            item {
                Row {
                    if (
                        auth.currentUser?.photoUrl.toString()
                            .isNotEmpty() && auth.currentUser?.photoUrl.toString() != "null"
                    ) {
                        AsyncImage(
                            model = auth.currentUser?.photoUrl.toString(),
                            contentDescription = "Your Profile",
                            modifier = Modifier
                                .size(42.dp)
                                .clip(CircleShape)
                        )
                    } else {
                        AsyncImage(
                            ProfileUtils.userProfileState.value.profileImageUrl
                                ?: "https://i0.wp.com/static.vecteezy.com/system/resources/previews/036/280/650/original/default-avatar-profile-icon-social-media-user-image-gray-avatar-icon-blank-profile-silhouette-illustration-vector.jpg?ssl=1",
                            contentDescription = "Your Profile",
                            contentScale = ContentScale.Inside,
                            modifier = Modifier
                                .size(42.dp)
                                .clip(CircleShape)
                        )
//                                Icon(
//                                    imageVector = Icons.Rounded.Person,
//                                    contentDescription = "Your Profile",
//                                    modifier = Modifier
//                                        .size(42.dp)
//                                        .clip(CircleShape)
//                                        .background(Color(0xFFD3E1EA).copy(0.5f))
//                                        .padding(3.dp)
//                                )
                    }

                    Column(
                        modifier = Modifier
                            .padding(start = 10.dp, top = 5.dp)
                            .clickable {
                                closeDrawer()
                                navController.navigate(MainActivity.MyProfileScreenRoute)
                            }
                    ) {

                        TranslatedText(
                            text = ProfileUtils.userProfileState.value.name,
                            style = MaterialTheme.typography.titleSmall,
                        )

                        TranslatedText(
                            text = "Update Your Profile",
                            color = Color(0xFF4D79FA),
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(top = 5.dp)
                        )

                    }
                }

                HeaderSpacer()

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 5.dp)
                        .clickable {
                            closeDrawer()
                            handleChangeSelectedItem("Explore")
//                        handleOpenCurrentJourneyBottomSheet()
                        }
                ) {

                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {

                        Icon(
                            imageVector = Icons.Rounded.RemoveRedEye,
                            contentDescription = "",
                            modifier = Modifier.size(21.dp)
                        )

                        TranslatedText(
                            text = "Explore Latest Posts",
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier.padding(start = 13.dp)
                        )
                    }

                    Icon(
                        imageVector = Icons.Outlined.PlayCircleOutline,
                        contentDescription = "",
                        tint = Color(0xFFF44336),
                        modifier = Modifier.size(20.dp)
                    )
                }

                HeaderSpacer()

//            Spacer(modifier = Modifier.height(5.dp))

                HeaderItem(icon = Icons.Rounded.Search, title = "Search astrologers") {
                    closeDrawer()
                    navController.navigate(MainActivity.SearchScreenRoute)
                }

                HeaderItem(icon = Icons.Rounded.WorkOutline, title = "Connect with astrologers") {
                    closeDrawer()
                    handleChangeSelectedItem("Astrologers")
                }

//                HeaderItem(icon = Icons.Outlined.LocalLibrary, title = "Call with astrologers") {
//                    closeDrawer()
//                    handleChangeSelectedItem("Call")
//                }

                HeaderItem(icon = Icons.Outlined.BarChart, title = "Recent Conversations") {
                    closeDrawer()
                    navController.navigate(MainActivity.MyChatsScreenRoute)
                }

                HeaderItem(
                    icon = Icons.Outlined.GraphicEq,
                    title = "Explore Upaay Shop"
                ) {
                    closeDrawer()
                    handleChangeSelectedItem("Shop")
                }

                HeaderSpacer()

                HeaderItem(icon = Icons.Rounded.VerifiedUser, title = "About Us") {
                    closeDrawer()
                    navController.navigate(MainActivity.PoliciesAboutUsRoute)
                }

                HeaderItem(icon = Icons.Rounded.Book, title = "Terms and Conditions") {
                    closeDrawer()
                    navController.navigate(MainActivity.PoliciesTermsAndConditionsRoute)
                }

                HeaderItem(icon = Icons.Outlined.Policy, title = "Privacy Policy") {
                    closeDrawer()
                    navController.navigate(MainActivity.PoliciesPrivacyPolicyRoute)
                }

                HeaderItem(icon = Icons.Outlined.AssignmentReturn, title = "Refund Policy") {
                    closeDrawer()
                    navController.navigate(MainActivity.PoliciesRefundPolicyRoute)
                }

                HeaderSpacer()

                HeaderItem(icon = Icons.Outlined.Logout, title = "Log out") {
                    FirebaseAuth.getInstance().signOut()
                    navController.navigate(MainActivity.AuthSignInScreenRoute) {
                        popUpTo(0) { inclusive = true }
                    }
                }

                HeaderSpacer()

//                Spacer(modifier = Modifier.height(5.dp))
//
//                HeaderItem(icon = Icons.Rounded.HelpOutline, title = "Need help") {
//                    closeDrawer()
//                }
//
//                HeaderItem(icon = Icons.Rounded.Settings, title = "Settings") {
//                    closeDrawer()
//                }
//
//                HeaderItem(icon = Icons.Outlined.Email, title = "Write to us") {
//                    closeDrawer()
//                }
//
//                HeaderItem(icon = Icons.Outlined.Info, title = "About us") {
//                    closeDrawer()
//                }
//
//                HeaderItem(icon = Icons.Outlined.Logout, title = "Log out") {
//                    FirebaseAuth.getInstance().signOut()
//                    navController.navigate(MainActivity.AuthWelcomeScreenRoute) {
//                        popUpTo(0) { inclusive = true }
//                    }
//                }
//
//                HeaderSpacer()

                Column {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 2.dp, vertical = 4.dp)
                            .clickable { isLanguageDropdownOpen = !isLanguageDropdownOpen },
                        elevation = CardDefaults.elevatedCardElevation(1.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Rounded.Language,
                                        contentDescription = "Language",
                                        modifier = Modifier
                                            .padding(end = 10.dp)
                                            .size(23.dp)
                                    )
                                    Column {
                                        TranslatableText(
                                            originalText = "Language Preference",
                                            style = MaterialTheme.typography.bodyMedium,
                                            fontWeight = FontWeight.Normal
                                        )
                                        //    val languageMap = mapOf(

                                        TranslatableText(
                                            originalText = if (selectedLanguage == "en") "English : Enabled" else if (selectedLanguage == "hi") "Hindi : Enabled" else if (selectedLanguage == "te") "Telugu : Enabled" else if (selectedLanguage == "ta") "Tamil : Enabled" else if (selectedLanguage == "mr") "" else "Marathi : Enabled",
                                            style = MaterialTheme.typography.labelMedium,
                                            modifier = Modifier
                                                .padding(top = 5.dp)
                                                .alpha(0.6f)
                                        )
                                    }
                                }
                                Icon(
                                    imageVector = if (isLanguageDropdownOpen) Icons.Rounded.ExpandLess else Icons.Rounded.ExpandMore,
                                    contentDescription = "Expand Language Selection",
                                    modifier = Modifier.size(24.dp)
                                )
                            }

                            if (isLanguageDropdownOpen) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(top = 8.dp),
                                    verticalArrangement = Arrangement.Bottom
                                ) {
                                    listOf("en" to "English", "hi" to "Hindi", "te" to "Telugu", "ta" to "Tamil", "mr" to "Marathi").forEach { (code, name) ->
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(vertical = 1.dp)
                                                .clickable {
                                                    tempSelectedLanguage = code
                                                }
                                        ) {
                                            RadioButton(
                                                selected = tempSelectedLanguage == code,
                                                onClick = { tempSelectedLanguage = code }
                                            )
                                            Text(
                                                text = name,
                                                modifier = Modifier.padding(start = 5.dp),
                                                style = MaterialTheme.typography.bodyMedium
                                            )
                                        }
                                    }

                                    // Apply Button (Only when selection changes)
                                    if (tempSelectedLanguage != selectedLanguage) {
                                        Button(
                                            onClick = {
                                                updateLanguage(tempSelectedLanguage) // ✅ Save preference
                                                isLanguageDropdownOpen = false
                                            },
                                            modifier = Modifier.fillMaxWidth(),
                                            colors = ButtonDefaults.buttonColors(
                                                containerColor = Color(0xFFA24C13),
                                                contentColor = Color.White
                                            ),
                                            shape = RoundedCornerShape(8.dp)
                                        ) {
                                            Text(text = "Apply")
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                // 🔹 Clickable Language Preference Item
//                Row(
//                    modifier = Modifier
//                        .fillMaxWidth()
//                        .clickable { showLanguageDialog = true } // 🔥 Opens Dialog
//                        .padding(vertical = 15.dp),
//                    verticalAlignment = Alignment.CenterVertically
//                ) {
//                    Icon(
//                        imageVector = Icons.Default.Language,
//                        contentDescription = "Language Icon",
//                        modifier = Modifier.size(24.dp)
//                    )
//
//                    Spacer(modifier = Modifier.width(10.dp))
//
//                    Column {
//                        TranslatedText(
//                            text = "Language Preference",
//                            style = MaterialTheme.typography.bodyMedium
//                        )
//                        languageMap.entries.find { it.value == currentLanguage }?.key?.let {
//                            TranslatedText(
//                                text = it,
//                                style = MaterialTheme.typography.labelSmall,
//                                color = Color.Gray
//                            )
//                        }
//                    }
//                }

//                Row(
//                    verticalAlignment = Alignment.CenterVertically,
//                    horizontalArrangement = Arrangement.SpaceBetween,
//                    modifier = Modifier
//                        .fillMaxWidth()
//                        .padding(horizontal = 5.dp)
//                ) {
//
//                    Row {
//
//                        Icon(
//                            imageVector = if (isCurrentThemeDark) Icons.Rounded.Nightlight else Icons.Rounded.LightMode,
//                            contentDescription = "",
//                            modifier = Modifier
//                                .padding(top = 5.dp)
//                                .size(23.dp)
//                        )
//
//                        Column(modifier = Modifier.padding(start = 13.dp)) {
//
//                            TranslatedText(
//                                text = "Display Preference",
//                                style = MaterialTheme.typography.bodyMedium,
//                                fontWeight = FontWeight.Normal,
//                            )
//
//                            TranslatedText(
//                                text = if (isCurrentThemeDark) "Dark Mode : Enabled" else "Dark Mode : Disabled",
//                                style = MaterialTheme.typography.labelMedium,
//                                modifier = Modifier
//                                    .padding(top = 7.dp)
//                                    .alpha(0.6f)
//                            )
//
//                        }
//                    }
//
//                    Switch(
//                        checked = isCurrentThemeDark,
//                        onCheckedChange = { toggleUiTheme() },
//                        colors = SwitchDefaults.colors(
//                            checkedThumbColor = Color(0xFF4D79FA),
//                            uncheckedThumbColor = Color.Gray,
//                            uncheckedTrackColor = Color.LightGray
//                        )
//                    )
//                }

                // 🔹 Language Selection Dialog
//                LanguagePreferenceDialog(
//                    showDialog = showLanguageDialog,
//                    onDismiss = { showLanguageDialog = false },
//                    currentLanguage = currentLanguage,
//                    onLanguageChange = { newLanguage -> currentLanguage = newLanguage },
//                    onSaveLanguage = { selectedLanguageCode ->  // 🔥 Now accepts language code
//                        sharedPreferences.edit().putString("language", selectedLanguageCode).apply()
//                        CURRENTLY_SELECTED_LANGUAGE =
//                            selectedLanguageCode // 🔥 Store correct language code
//
//                        Toast.makeText(
//                            context,
//                            "Language Updated to ${languageMap.entries.find { it.value == selectedLanguageCode }?.key}",
//                            Toast.LENGTH_SHORT
//                        ).show()
//                    }
//                )
            }

//            Spacer(modifier = Modifier.weight(1f))
//
//            TranslatedText(
//                text = "Version 1.0.0",
//                style = MaterialTheme.typography.labelSmall,
//                modifier = Modifier.padding(start = 35.dp).alpha(0.4f)
//            )
        }
    }
}

@Composable
fun HeaderSpacer(bottomPadding: Int = 15) {
    Spacer(
        modifier = Modifier
            .padding(top = 15.dp, bottom = bottomPadding.dp, start = 5.dp, end = 5.dp)
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(0.3f))
            .height(1.dp)
    )
}

@Composable
fun HeaderItem(icon: ImageVector, title: String, onClick: () -> Unit = {}) {

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                onClick()
            }
            .padding(start = 5.dp, top = 12.dp, bottom = 14.dp)
    ) {

        Icon(
            imageVector = icon,
            contentDescription = "",
            modifier = Modifier.size(18.dp)
        )

        TranslatedText(
            text = title,
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Normal,
            modifier = Modifier.padding(start = 13.dp)
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LanguagePreferenceDialog(
    showDialog: Boolean,
    onDismiss: () -> Unit,
    currentLanguage: String, // Should be a language code (e.g., "en", "hi")
    onLanguageChange: (String) -> Unit,
    onSaveLanguage: (String) -> Unit
) {
    if (!showDialog) return

    val context = LocalContext.current

    val languageMap = mapOf(
        "en" to "English",
        "hi" to "Hindi",
        "te" to "Telugu",
        "ta" to "Tamil",
        "gu" to "Gujarati",
        "mr" to "Marathi"
    )

    var selectedLanguageCode by remember { mutableStateOf(currentLanguage) }
    val selectedLanguageName = languageMap[selectedLanguageCode] ?: "English"

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                // 🔹 Heading
                TranslatedText(
                    text = "Select Language",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(10.dp))

                // 🔹 Dropdown Menu for Language Selection
                var isDropdownExpanded by remember { mutableStateOf(false) }
                ExposedDropdownMenuBox(
                    expanded = isDropdownExpanded,
                    onExpandedChange = { isDropdownExpanded = !isDropdownExpanded }
                ) {
                    OutlinedTextField(
                        value = selectedLanguageName,
                        onValueChange = {},
                        readOnly = true,
                        trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(expanded = isDropdownExpanded)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor()
                    )

                    ExposedDropdownMenu(
                        expanded = isDropdownExpanded,
                        onDismissRequest = { isDropdownExpanded = false }
                    ) {
                        languageMap.forEach { (code, name) ->
                            DropdownMenuItem(
                                text = { TranslatedText(text = name) },
                                onClick = {
                                    selectedLanguageCode = code // ✅ Store language code
                                    isDropdownExpanded = false
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // 🔹 Update Button (Enabled only if language is changed)
                Button(
                    onClick = {
                        onSaveLanguage(selectedLanguageCode) // ✅ Pass language code
                        onDismiss() // Close the dialog
                    },
                    enabled = selectedLanguageCode != currentLanguage, // ✅ Compare codes
                    modifier = Modifier.fillMaxWidth()
                ) {
                    TranslatedText(text = "Update Language")
                }
            }
        }
    }
}

