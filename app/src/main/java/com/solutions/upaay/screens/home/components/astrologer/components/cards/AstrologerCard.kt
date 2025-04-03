package com.solutions.upaay.screens.home.components.astrologer.components.cards

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.annotation.Keep
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Message
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.solutions.upaay.MainActivity
import com.solutions.upaay.utils.translate.TranslatedText

@Keep
data class Astrologer(
    val uid: String = "",
    val name: String = "",
//    val description: String? = null,
    val experience: String = "",
    val isOnline: Boolean = false,
    val profileImageUrl: String? = null,
    val audioRatePerMinute: Double? = 0.0,
    val videoRatePerMinute: Double? = 0.0,
    val ratePerMessage: Double? = 0.0,
    val speciality: List<String> = emptyList(),
    val languages: List<String> = emptyList(),
    val totalOrders: Long? = 356,
    val waitTime: Long? = 7,
    val isInCall: Boolean = false
)

fun openWhatsApp(context: Context, phoneNumber: String, message: String) {
    try {
        val url = "https://wa.me/$phoneNumber?text=${Uri.encode(message)}"
        val intent = Intent(Intent.ACTION_VIEW)
        intent.data = Uri.parse(url)
        context.startActivity(intent)
    } catch (e: Exception) {
        Toast.makeText(context, "WhatsApp not installed.", Toast.LENGTH_SHORT).show()
    }
}


@Composable
fun AstrologerCard(
    modifier: Modifier = Modifier,
    navController: NavController,
    isForChatScreen: Boolean,
    isForDetailScreen: Boolean = false,
    astrologer: Astrologer,
//    astrologerId: String = "",
//    name: String = "Loading...",
//    speciality: String = "...",
//    languages: List<String> = emptyList(),
//    experience: String = "...",
//    totalOrders: Number? = 1350,
//    ratePerMessage: Int = 23,
//    audioRatePerMinute: Int = 49,
//    videoRatePerMinute: Int = 55,
//    isOnline: Boolean = true,
//    waitTime: Long? = 7,
//    profileImageRes: Int,
//    profileImageUrl: String? = null,
    triggerBottomSheet: () -> Unit = {}
) {

    var menuExpanded by remember { mutableStateOf(false) }
    val context = LocalContext.current

    Card(
        shape = RoundedCornerShape(11.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        modifier = modifier
            .fillMaxWidth()
            .padding(5.dp)
            .then(
                if (!isForDetailScreen) {
                    Modifier.clickable {
                        navController.navigate(MainActivity.AstrologerDetailsScreenRoute(astrologer.uid))
                    }
                } else {
                    Modifier
                }
            )

    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .height(IntrinsicSize.Min)
        ) {
            // Profile Image
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

//                Image(
//                    painter = painterResource(id = profileImageRes),
//                    contentDescription = "Astrologer Profile",
//                    modifier = Modifier
//                        .size(70.dp)
//                        .background(Color.Gray, CircleShape)
//                        .clip(CircleShape)
//                        .border(2.dp, Color.Yellow, CircleShape),
//                    contentScale = ContentScale.Crop
//                )
                if (!astrologer.profileImageUrl.isNullOrEmpty()) {
                    AsyncImage(
                        model = astrologer.profileImageUrl,
                        contentDescription = "Profile",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(60.dp)
                            .clip(CircleShape)
                    )

                } else {
                    AsyncImage(
                        model = "https://w7.pngwing.com/pngs/507/691/png-transparent-nakshatra-hindu-astrology-horoscope-astrological-sign-astrology-decor-aries-ascendant-thumbnail.png",
//                        model = "https://www.astrobharati.in/Upload/astrologers/profile_pic/Dr.Vinayak%20Bhat/1.Vinayak%20bhat.jpg",
                        contentDescription = "Profile",
                        modifier = Modifier
                            .size(60.dp)
                            .clip(CircleShape)
                    )
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .padding(top = 5.dp)
                ) {
                    repeat(5) {
                        Icon(
                            imageVector = Icons.Filled.Star,
                            contentDescription = "Rating $it",
                            tint = Color.Gray,
                            modifier = Modifier.size(15.dp)
                        )
                    }
                }

                if (
                    astrologer.totalOrders != null && astrologer.totalOrders > 0
                ) {
                    TranslatedText(
                        text = "${astrologer.totalOrders}+ orders",
                        color = Color.Gray,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.W400
                    )
                } else {
                    TranslatedText(
                        text = "450+ orders",
                        color = Color.Gray,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.W400
                    )
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                // Name and Status
                if (isForDetailScreen) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(5.dp)
                    ) {
                        TranslatedText(
                            text = astrologer.name,
                            overflow = TextOverflow.Ellipsis,
                            maxLines = 1,
                            style = MaterialTheme.typography.titleSmall
                        )

                        // 🔴 Call Status Indicator
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(start = 3.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(6.dp)
                                    .clip(CircleShape)
                                    .background(
                                        if (astrologer.isInCall) Color.Red else Color(
                                            0xFF4CAF50
                                        )
                                    ) // Red or Green
                            )
//                            Spacer(modifier = Modifier.width(4.dp))
//                            TranslatedText(
//                                text = if (astrologer.isInCall) "On a call" else "Available",
//                                fontSize = 12.sp,
//                                color = if (astrologer.isInCall) Color.Red else Color(0xFF4CAF50),
//                                fontWeight = FontWeight.Medium
//                            )
                        }

                        AsyncImage(
                            model = "https://static.vecteezy.com/system/resources/thumbnails/017/350/123/small_2x/green-check-mark-icon-in-round-shape-design-png.png",
                            contentDescription = "Verified",
                            modifier = Modifier
                                .size(20.dp)
                                .clip(CircleShape)
                        )

//                        Box(
//                            modifier = Modifier
//                                .background(
//                                    color = Color(0xFFFFE22B),
//                                    shape = RoundedCornerShape(5.dp)
//                                )
//                                .padding(horizontal = 8.dp, vertical = 3.dp)
//                                .clickable {
//                                    navController.navigate(MainActivity.AstrologerUploadsScreenRoute)
//                                },
//                            contentAlignment = Alignment.Center
//                        ) {
//                            TranslatedText(
//                                text = "Follow",
//                                color = Color.Black,
//                                style = MaterialTheme.typography.labelSmall,
//                                fontSize = 11.sp
//                            )
//                        }
                    }
                } else {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(5.dp)
                    ) {
                        TranslatedText(
                            text = astrologer.name,
                            overflow = TextOverflow.Ellipsis,
                            maxLines = 1,
                            style = MaterialTheme.typography.titleSmall
                        )

                        // 🔴 Call Status Indicator
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(start = 3.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(6.dp)
                                    .clip(CircleShape)
                                    .background(
                                        if (astrologer.isInCall) Color.Red else Color(
                                            0xFF4CAF50
                                        )
                                    ) // Red or Green
                            )
//                            Spacer(modifier = Modifier.width(4.dp))
//                            TranslatedText(
//                                text = if (astrologer.isInCall) "On a call" else "Available",
//                                fontSize = 12.sp,
//                                color = if (astrologer.isInCall) Color.Red else Color(0xFF4CAF50),
//                                fontWeight = FontWeight.Medium
//                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(7.dp))

                if (
                    !astrologer.speciality.isNullOrEmpty()
                ) {

                    TranslatedText(
                        text = astrologer.speciality.take(3).joinToString(", "),
                        overflow = TextOverflow.Ellipsis,
                        maxLines = 1,
                        style = MaterialTheme.typography.labelMedium
                    )
                    Spacer(modifier = Modifier.height(7.dp))
                }


                if (!astrologer.languages.isNullOrEmpty()) {
                    TranslatedText(
                        text = astrologer.languages.take(2).joinToString(", "),
                        overflow = TextOverflow.Ellipsis,
                        maxLines = 1,
                        style = MaterialTheme.typography.labelMedium
                    )
                    Spacer(modifier = Modifier.height(7.dp))
                }

                if (
                    !astrologer.experience.isNullOrEmpty()
                ) {
                    TranslatedText(
                        text = "Exp: ${astrologer.experience}",
                        overflow = TextOverflow.Ellipsis,
                        maxLines = 1, style = MaterialTheme.typography.labelMedium
                    )

                    Spacer(modifier = Modifier.height(10.dp))
                }

                Spacer(modifier = Modifier.weight(1f))

//                Row(
//                    verticalAlignment = Alignment.Bottom,
//                ) {
//                    TranslatedText(
//                        text = if (isForChatScreen) "₹${astrologer.ratePerMessage}" else "₹${astrologer.audioRatePerMinute}",
//                        style = MaterialTheme.typography.titleMedium,
//                        color = Color.Gray
//                    )
//                    TranslatedText(
//                        text = "/min",
//                        style = MaterialTheme.typography.labelMedium,
//                    )
//                }
            }

            Column(
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxHeight()
            ) {

                if (isForDetailScreen) {
                    Box {
                        Icon(
                            imageVector = Icons.Default.MoreHoriz,
                            contentDescription = "More Options",
                            modifier = Modifier
                                .size(24.dp)
                                .clickable { menuExpanded = true }
                        )

                        DropdownMenu(
                            expanded = menuExpanded,
                            onDismissRequest = { menuExpanded = false }
                        ) {
                            DropdownMenuItem(
                                text = { TranslatedText("Contact Support") },
                                onClick = {
                                    menuExpanded = false
                                    openWhatsApp(
                                        context,
                                        "+919318345767",
                                        "Hello, I have a query about ${astrologer.name}. Please help me out."
                                    )
                                }
                            )
                        }
                    }
                } else {

                    AsyncImage(
                        model = "https://static.vecteezy.com/system/resources/thumbnails/017/350/123/small_2x/green-check-mark-icon-in-round-shape-design-png.png",
                        contentDescription = "Verified",
                        modifier = Modifier
                            .size(20.dp)
                            .clip(CircleShape)
                    )
//                    Column(
//                        horizontalAlignment = Alignment.CenterHorizontally,
//                        verticalArrangement = Arrangement.Bottom
//                    ) {
//
//                        Button(
//                            onClick = {
//                                if (isForChatScreen) {
//
//                                    navController.navigate(MainActivity.ChatScreenRoute(astrologerId = astrologer.uid))
//                                } else {
//
//                                    triggerBottomSheet()
////                                   handleCallAstrologer(navController = navController, astrologerId = "BQKMoOgdnTaMiGYEA1WJXnbWMBg1");
//                                }
//                            },
//                            shape = RoundedCornerShape(8.dp),
//                            colors = ButtonDefaults.outlinedButtonColors(
//                                containerColor = Color.Transparent,
//                                contentColor = Color.Red
//                            ),
//                            border = BorderStroke(1.dp, Color.Red),
//                            modifier = Modifier
//                                .height(32.dp)
//                                .padding(start = 8.dp)
//                        ) {
//                            TranslatedText(
//                                text = if (isForChatScreen) "Chat Now" else "Call Now",
//                                fontSize = 12.sp,
//                                fontWeight = FontWeight.Bold,
//                                color = Color.Red
//                            )
//                        }
//
//                        Spacer(modifier = Modifier.height(6.dp))
//
//                        if (astrologer.isOnline) {
//
//                            Row(
//                                verticalAlignment = Alignment.CenterVertically,
//                            ) {
//                                Box(
//                                    modifier = Modifier
//                                        .size(6.dp)
//                                        .background(Color.Green, CircleShape)
//                                )
//                                Spacer(modifier = Modifier.width(6.dp))
//                                TranslatedText(
//                                    text = "Online",
//                                    style = MaterialTheme.typography.labelMedium
//                                )
//                            }
//                        } else {
//                            if (
//                                astrologer.waitTime != null
//                            ) {
//                                TranslatedText(
//                                    text = if (astrologer.waitTime == 0L) "Available" else "wait - ${astrologer.waitTime}s",
//                                    style = MaterialTheme.typography.labelMedium,
//                                    color = Color.Red
//                                )
//                            } else {
//                                TranslatedText(
//                                    text = "wait - 7s",
//                                    style = MaterialTheme.typography.labelMedium,
//                                    color = Color.Red
//                                )
//                            }
//                        }
//                    }
                }
            }
        }

        if (isForDetailScreen) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(Color.Gray.copy(0.2f))
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp, 10.dp, 10.dp, 10.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.clickable {
                        navController.navigate(MainActivity.ChatScreenRoute(astrologerId = astrologer.uid))
//                        Toast.makeText(context, astrologerId, Toast.LENGTH_SHORT).show()
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.Message,
                        contentDescription = "Chat Icon",
                        tint = Color.DarkGray,
                        modifier = Modifier.size(19.dp)
                    )
                    Spacer(modifier = Modifier.width(7.dp)) // Space between icon and text
                    TranslatedText(
                        text = "80k mins",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.DarkGray,
                    )
                }

                Box(
                    modifier = Modifier
                        .width(1.dp)
                        .height(20.dp)
                        .background(Color.Gray.copy(0.5f))
                )

                // Call / Phone Section
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.clickable {
                        triggerBottomSheet()
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.Phone,
                        contentDescription = "Phone Icon",
                        tint = Color.DarkGray,
                        modifier = Modifier.size(19.dp)
                    )
                    Spacer(modifier = Modifier.width(5.dp)) // Space between icon and text
                    TranslatedText(
                        text = "5k mins",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.DarkGray,
                    )
                }
            }
        } else {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 5.dp, start = 10.dp, end = 10.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp, Alignment.CenterHorizontally)
            ) {
                // Message Button
                Button(
                    onClick = {
                        navController.navigate(MainActivity.ChatScreenRoute(astrologerId = astrologer.uid))
                    },
                    modifier = Modifier
                        .weight(1f)
                        .height(36.dp),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF7A32B))
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        TranslatedText(
                            text = "₹${astrologer.ratePerMessage?.toInt() ?: 0}",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.width(6.dp))

                        Box(
                            modifier = Modifier
                                .height(18.dp)
                                .width(1.dp)
                                .background(Color.White.copy(alpha = 0.5f))
                        )

                        Spacer(modifier = Modifier.width(6.dp))

                        TranslatedText(
                            text = "Message",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color.White
                        )
                    }
                }

                // --- Call Button ---
                Button(
                    onClick = { triggerBottomSheet() },
                    modifier = Modifier
                        .weight(1f)
                        .height(36.dp),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE95432))
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        TranslatedText(
                            text = "₹${astrologer.audioRatePerMinute?.toInt() ?: 0}",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.width(6.dp))

                        Box(
                            modifier = Modifier
                                .height(18.dp)
                                .width(1.dp)
                                .background(Color.White.copy(alpha = 0.5f))
                        )

                        Spacer(modifier = Modifier.width(6.dp))

                        TranslatedText(
                            text = "Call Now",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color.White
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun AstrologerCardSmaller(
    modifier: Modifier = Modifier,
    navController: NavController,
    isForChatScreen: Boolean,
    astrologer: Astrologer,
    isInLobby: Boolean = false,
    triggerBottomSheet: () -> Unit = {}
) {
    Card(
        shape = RoundedCornerShape(11.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isInLobby) Color(0xFFF8F1E4) else Color.White
        ),
        modifier = modifier
            .padding(5.dp)
            .clickable {
                navController.navigate(MainActivity.AstrologerDetailsScreenRoute(astrologerId = astrologer.uid))
            },
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .padding(10.dp)
        ) {

            if (!astrologer.profileImageUrl.isNullOrEmpty()) {
                AsyncImage(
                    model = astrologer.profileImageUrl,
                    contentDescription = "Profile",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(50.dp)
                        .clip(CircleShape)
                )
            } else {
                AsyncImage(
                    model = "https://w7.pngwing.com/pngs/507/691/png-transparent-nakshatra-hindu-astrology-horoscope-astrological-sign-astrology-decor-aries-ascendant-thumbnail.png",
//                        model = "https://www.astrobharati.in/Upload/astrologers/profile_pic/Dr.Vinayak%20Bhat/1.Vinayak%20bhat.jpg",
                    contentDescription = "Profile",
                    modifier = Modifier
                        .size(45.dp)
                        .clip(CircleShape)
                )
            }

            TranslatedText(
                text = astrologer.name,
                overflow = TextOverflow.Ellipsis,
                maxLines = 1,
                style = if (isInLobby) MaterialTheme.typography.titleSmall else MaterialTheme.typography.bodySmall,
                modifier = Modifier
                    .padding(top = 5.dp)
            )

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .padding(top = 5.dp)
            ) {

                repeat(5) {
                    Icon(
                        imageVector = Icons.Filled.Star,
                        contentDescription = "Rating $it",
                        tint = Color.Gray,
                        modifier = Modifier.size(if (isInLobby) 15.dp else 14.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                verticalAlignment = Alignment.Bottom,
            ) {
                TranslatedText(
                    text = if (isForChatScreen) "₹${astrologer.ratePerMessage}" else "₹${astrologer.audioRatePerMinute}",
                    style = if (isInLobby) MaterialTheme.typography.titleMedium else MaterialTheme.typography.titleSmall,
                    color = Color.Gray
                )
                TranslatedText(
                    text = "/min",
                    style = MaterialTheme.typography.labelMedium,
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Button(
                onClick = {
                    if (isForChatScreen) {

                        navController.navigate(MainActivity.ChatScreenRoute(astrologerId = astrologer.uid))
                    } else {
//                        handleCallAstrologer(navController = navController, astrologerId = "BQKMoOgdnTaMiGYEA1WJXnbWMBg1");
                        triggerBottomSheet()
                    }
                },
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.outlinedButtonColors(
                    containerColor = Color.Transparent,
                    contentColor = Color.Red
                ),
                border = BorderStroke(1.dp, Color.Red),
                modifier = Modifier
                    .height(32.dp)
                    .padding(start = 8.dp)
            ) {
                TranslatedText(
                    text = if (isForChatScreen) "Chat Now" else "Call Now",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Red
                )
            }
        }
    }
}

fun handleCallAstrologer(navController: NavController, astrologerId: String) {
    val userId = FirebaseAuth.getInstance().currentUser?.uid

    if (userId != null) {
        val callRef =
            FirebaseFirestore.getInstance().collection("calls")
                .document()
        val callId = callRef.id

        val callData = mapOf(
            "callerId" to userId,
            "receiverId" to astrologerId,
            "status" to "ringing" // Track call status
        )

        callRef.set(callData).addOnSuccessListener {
            navController.navigate(
                MainActivity.AudioCallScreenRoute(
                    userId,
                    astrologerId,
                    callId
                )
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CallAstrologerBottomSheet(
    navController: NavController,
    astrologerId: String,
    astrologerName: String,
    onDismiss: () -> Unit // Callback to close the sheet
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val bottomSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    val firestore = FirebaseFirestore.getInstance()
    val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return

    var userBalance by remember { mutableStateOf<Double?>(null) }
    var ratePerMessage by remember { mutableStateOf<Double?>(null) }
    var isLoading by remember { mutableStateOf(true) }

    // Fetch user balance and astrologer's rate
    LaunchedEffect(astrologerId) {
        firestore.runTransaction { transaction ->
            val userRef = firestore.collection("users").document(userId)
            val astrologerRef = firestore.collection("astrologers").document(astrologerId)

            // Fetch user balance
            val userSnapshot = transaction.get(userRef)
            val fetchedUserBalance = userSnapshot.getDouble("balance") ?: 0.0

            // Fetch astrologer's rate per message
            val astrologerSnapshot = transaction.get(astrologerRef)
            val fetchedRatePerMessage = astrologerSnapshot.getDouble("audioRatePerMinute") ?: 0.0

            Pair(fetchedUserBalance, fetchedRatePerMessage)
        }.addOnSuccessListener { (balance, rate) ->
            userBalance = balance
            ratePerMessage = rate
            isLoading = false
        }.addOnFailureListener {
            Toast.makeText(context, "Failed to fetch data", Toast.LENGTH_SHORT).show()
            onDismiss()
        }
    }

    ModalBottomSheet(
        sheetState = bottomSheetState,
        onDismissRequest = { onDismiss() }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (isLoading) {
                CircularProgressIndicator()
                Spacer(modifier = Modifier.height(12.dp))
                TranslatedText("Checking balance...", fontSize = 16.sp)
            } else {
                val balance = userBalance ?: 0.0
                val rate = ratePerMessage ?: 0.0

                if (balance < rate) {
                    // ❌ Insufficient balance UI
                    TranslatedText(
                        text = "Insufficient Balance",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Red
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    TranslatedText(
                        text = "You need ₹${String.format("%.0f", rate)} to proceed.",
                        fontSize = 14.sp,
                        color = Color.Gray
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    Button(
                        onClick = {
                            onDismiss()
                            navController.navigate(MainActivity.WalletScreenRoute)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFEA4335), // Red theme
                            contentColor = Color.White
                        )
                    ) {
                        TranslatedText("Recharge Now", fontSize = 16.sp)
                    }
                } else {
                    // ✅ Sufficient balance UI
                    TranslatedText(
                        text = "Call $astrologerName?",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.SemiBold,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    Button(
                        onClick = {
                            onDismiss()
                            handleCallAstrologer(navController, astrologerId)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF388E3C),
                            contentColor = Color.White
                        )
                    ) {
                        TranslatedText("Call Now", fontSize = 16.sp, fontWeight = FontWeight.Medium)
                    }
                }
            }
        }
    }

}
