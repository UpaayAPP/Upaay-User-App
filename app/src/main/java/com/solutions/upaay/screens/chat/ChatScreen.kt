package com.solutions.upaay.screens.chat

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.Keep
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Image
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.google.firebase.Firebase
import com.google.firebase.Timestamp
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.firestore
import com.google.firebase.storage.storage
import com.solutions.upaay.MainActivity
import com.solutions.upaay.R
import com.solutions.upaay.utils.database.update.processChatTransaction
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.util.UUID

@Keep
data class Message(
    val text: String = "",
    val senderId: String = "",
    val recipientId: String = "",
    val chatMessageRef: String = "", // keep using for billing, etc.
    val imageMessageRef: String = "", // for image URL
    val timestamp: Timestamp = Timestamp.now()
)


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(navController: NavController, astrologerId: String, userId: String) {
    val context = LocalContext.current
    val chatId = "${userId}_$astrologerId"
    val firestore = Firebase.firestore
    val storage = Firebase.storage
    val messagesCollection = firestore.collection("chats").document(chatId).collection("messages")
    val userDocRef =
        firestore.collection("users").document(userId).collection("chats").document(chatId)
    val astrologerDocRef =
        firestore.collection("astrologers").document(astrologerId).collection("chats")
            .document(chatId)

    var messageText by remember { mutableStateOf("") }
    var imagePreviewUri by remember { mutableStateOf<Uri?>(null) }
    val messages = remember { mutableStateListOf<Message>() }
    val coroutineScope = rememberCoroutineScope()

    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        imagePreviewUri = uri
    }

    var fullScreenImageUrl by remember { mutableStateOf<String?>(null) }
    var isSending by remember { mutableStateOf(false) }

    // For scrolling to latest message
    val listState = rememberLazyListState()
    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            listState.scrollToItem(messages.lastIndex)
        }
    }

    // Realtime Listener
    LaunchedEffect(chatId) {
        messagesCollection
            .orderBy("timestamp", Query.Direction.ASCENDING)
            .addSnapshotListener { snapshot, _ ->
                if (snapshot != null) {
                    messages.clear()
                    for (doc in snapshot.documents) {
                        val message = doc.toObject(Message::class.java)
                        if (message != null) {
                            messages.add(message)
                        }
                    }
                }
            }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        // Background Image
        Image(
            painter = painterResource(id = R.drawable.upaay_backcover),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .align(Alignment.Center)
                .fillMaxWidth(0.6f)
                .aspectRatio(1f)
                .clip(CircleShape)
                .alpha(0.1f)
//                .background(Color.Black)
        )

        Column(modifier = Modifier.fillMaxSize()) {
            // Top Bar
            TopAppBar(
                title = { Text("Chat", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )

            // Messages List
            LazyColumn(
                state = listState,
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 10.dp, vertical = 4.dp)
            ) {
                items(messages) { message ->
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        contentAlignment = if (message.senderId == userId) Alignment.CenterEnd else Alignment.CenterStart
                    ) {
                        if (!message.imageMessageRef.isNullOrEmpty()) {
                            AsyncImage(
                                model = message.imageMessageRef,
                                contentDescription = "Sent image",
                                modifier = Modifier
                                    .width(180.dp)
                                    .height(180.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .clickable { fullScreenImageUrl = message.imageMessageRef },
                                contentScale = ContentScale.Crop
                            )

                            if (message.text.isNotBlank()) {
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = message.text,
                                    style = MaterialTheme.typography.bodySmall,
                                    modifier = Modifier
                                        .background(
                                            color = if (message.senderId == userId) Color(0xFFD9FDD3) else Color.White,
                                            shape = RoundedCornerShape(12.dp)
                                        )
                                        .padding(horizontal = 12.dp, vertical = 6.dp)
                                        .widthIn(max = 300.dp)
                                )
                            }
                        } else {
                            // Text-only messages
                            Text(
                                text = message.text,
                                style = MaterialTheme.typography.bodySmall,
                                modifier = Modifier
                                    .background(
                                        color = if (message.senderId == userId) Color(0xFFD9FDD3) else Color.White,
                                        shape = RoundedCornerShape(12.dp)
                                    )
                                    .padding(horizontal = 12.dp, vertical = 10.dp)
                                    .widthIn(max = 300.dp)
                            )
                        }

                    }
                }
            }

            // Input Field & Actions


            // --- Text Field & Actions ---
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 10.dp, vertical = 8.dp),
                verticalAlignment = Alignment.Bottom
            ) {
                // Image Picker Icon
                IconButton(onClick = { launcher.launch("image/*") }) {
                    Icon(Icons.Default.Image, contentDescription = "Pick Image")
                }

                // Message Input with Attached Image Above It
                Column(modifier = Modifier.weight(1f)) {

                    if (imagePreviewUri != null) {
                        Box(
                            modifier = Modifier
                                .width(LocalConfiguration.current.screenWidthDp.dp * 0.2f)
                                .aspectRatio(9f / 16f) // Maintain screenshot-like portrait ratio
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color(0xFFEFEFEF))
                        ) {
                            AsyncImage(
                                model = imagePreviewUri,
                                contentDescription = "Preview",
                                modifier = Modifier
                                    .fillMaxSize()
                                    .clip(RoundedCornerShape(12.dp))
                                    .clickable {
                                        fullScreenImageUrl = imagePreviewUri.toString()
                                    },
                                contentScale = ContentScale.Crop
                            )

                            IconButton(
                                onClick = { imagePreviewUri = null },
                                modifier = Modifier
                                    .align(Alignment.TopEnd)
                                    .padding(6.dp)
                                    .size(22.dp)
                                    .background(Color.White.copy(alpha = 0.9f), shape = CircleShape)
                            ) {
                                Icon(
                                    Icons.Default.Close,
                                    contentDescription = "Remove",
                                    tint = Color.Black
                                )
                            }
                        }
                    }

                    TextField(
                        value = messageText,
                        onValueChange = { messageText = it },
                        placeholder = { Text("Type a message...") },
                        shape = if (imagePreviewUri == null) RoundedCornerShape(25.dp) else RoundedCornerShape(
                            bottomStart = 16.dp,
                            bottomEnd = 16.dp
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        colors = TextFieldDefaults.textFieldColors(
                            containerColor = Color(0xFFF0F0F0),
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent
                        )
                    )
                }

                IconButton(onClick = {
                    if (messageText.isNotBlank() || imagePreviewUri != null) {
                        coroutineScope.launch {
                            try {
                                isSending = true // Start loading

                                val chatMessageRef = UUID.randomUUID().toString()
                                val success = processChatTransaction(
                                    firestore = firestore,
                                    userId = userId,
                                    astrologerId = astrologerId,
                                    chatId = chatId,
                                    chatMessageRef = chatMessageRef
                                )

                                if (success) {
                                    var imageUrl: String? = null
                                    if (imagePreviewUri != null) {
                                        val imageRef =
                                            storage.reference.child("chat_images/${UUID.randomUUID()}.jpg")
                                        imageRef.putFile(imagePreviewUri!!).await()
                                        imageUrl = imageRef.downloadUrl.await().toString()
                                    }

                                    val newMessage = Message(
                                        text = messageText.trim(),
                                        senderId = userId,
                                        recipientId = astrologerId,
                                        chatMessageRef = chatMessageRef,
                                        imageMessageRef = imageUrl ?: "",
                                        timestamp = Timestamp.now()
                                    )

                                    messagesCollection.add(newMessage).await()

                                    firestore.runTransaction { transaction ->
                                        val userChatExists = transaction.get(userDocRef).exists()
                                        val astrologerChatExists =
                                            transaction.get(astrologerDocRef).exists()

                                        if (!userChatExists) {
                                            transaction.set(
                                                userDocRef,
                                                hashMapOf(
                                                    "chatId" to chatId,
                                                    "timestamp" to Timestamp.now(),
                                                    "astrologerId" to astrologerId
                                                )
                                            )
                                        }
                                        if (!astrologerChatExists) {
                                            transaction.set(
                                                astrologerDocRef,
                                                hashMapOf(
                                                    "chatId" to chatId,
                                                    "timestamp" to Timestamp.now(),
                                                    "userId" to userId
                                                )
                                            )
                                        }
                                    }.await()

                                    messageText = ""
                                    imagePreviewUri = null
                                }

                            } catch (e: Exception) {
                                val errorMessage = e.message ?: "Something went wrong"
                                Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show()

                                if (errorMessage.contains(
                                        "Insufficient balance",
                                        ignoreCase = true
                                    )
                                ) {
                                    navController.navigate(MainActivity.WalletScreenRoute)
                                }

                            } finally {
                                isSending = false // Stop loading
                            }
                        }
                    }
                }) {
                    if (isSending) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            strokeWidth = 2.dp,
                            color = MaterialTheme.colorScheme.primary
                        )
                    } else {
                        Icon(Icons.AutoMirrored.Filled.Send, contentDescription = "Send")
                    }
                }
            }
        }

        if (fullScreenImageUrl != null) {
            BackHandler(onBack = { fullScreenImageUrl = null })

            Box(
                modifier = Modifier
                    .fillMaxSize()
//                    .padding(horizontal = 20.dp, vertical = 20.dp)
                    .background(Color.Black.copy(alpha = 0.95f))
                    .clickable { fullScreenImageUrl = null },
                contentAlignment = Alignment.Center
            ) {
                AsyncImage(
                    model = fullScreenImageUrl,
                    contentDescription = "Full screen image",
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp)
                        .clip(RoundedCornerShape(16.dp)),
                    contentScale = ContentScale.Fit
                )
            }
        }

    }
}

//fun addEarningsForMessage(astrologerId: String, userId: String, ratePerMessage: Double, platformFee: Double) {
//    val astrologerRef = FirebaseFirestore.getInstance().collection("astrologers").document(astrologerId)
//    val earningsAfterFee = ratePerMessage - (ratePerMessage * platformFee)
//
//    astrologerRef.get().addOnSuccessListener { document ->
//        val currentBalance = document.getDouble("wallet.balance") ?: 0.0
//        val newBalance = currentBalance + earningsAfterFee
//
//        astrologerRef.update("wallet.balance", newBalance)
//
//        // Log transaction
//        val transaction = mapOf(
//            "type" to "chat",
//            "amount" to earningsAfterFee,
//            "userId" to userId,
//            "timestamp" to System.currentTimeMillis()
//        )
//        astrologerRef.collection("wallet").document("transactions")
//            .collection("history").add(transaction)
//    }
//}

