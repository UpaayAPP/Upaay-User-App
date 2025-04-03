package com.solutions.upaay.screens.chat

import androidx.annotation.Keep
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.compose.rememberAsyncImagePainter
import com.solutions.upaay.MainActivity
import com.solutions.upaay.utils.loading.LoadingStateManager
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.solutions.upaay.utils.translate.TranslatedText
import kotlinx.coroutines.tasks.await

@Keep
data class RecentChat(
    val userId: String = "",
    val chatId: String = "",
    val userName: String = "",
    val profileImageUrl: String = "",
    val lastMessage: String = "",
    val timestamp: Long = 0L
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyChatsScreen(navController: NavController, userId : String) {

    val firestore = FirebaseFirestore.getInstance()
    var recentChats by remember { mutableStateOf<List<RecentChat>>(emptyList()) }

    LaunchedEffect(Unit) {
        fetchRecentChats(firestore, userId) { chats ->
            recentChats = chats
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp)
    ) {
        TopAppBar(
            title = { Text("Recent Chats", fontWeight = FontWeight.Bold) },
            navigationIcon = {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                }
            }
        )
        Spacer(modifier = Modifier.height(12.dp))

//        TranslatedText(
//            text = "Recent Chats",
//            style = MaterialTheme.typography.headlineSmall,
//            fontWeight = FontWeight.Bold,
//            modifier = Modifier.padding(vertical = 8.dp)
//        )

        if (recentChats.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "No recent chats",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray
                )
            }
        } else {
            Column {
                recentChats.forEach { chat ->
                    RecentChatItem(chat = chat) {
                        navController.navigate(MainActivity.ChatScreenRoute(chat.userId))
                    }
                }
            }
        }
    }
}

const val defaultProfileUrl =
    "https://static.vecteezy.com/system/resources/previews/036/280/650/non_2x/default-avatar-profile-icon-social-media-user-image-gray-avatar-icon-blank-profile-silhouette-illustration-vector.jpg"

@Composable
fun RecentChatItem(chat: RecentChat, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {

        Image(
            painter = rememberAsyncImagePainter(
                model = chat.profileImageUrl.ifEmpty { defaultProfileUrl }
            ),
            contentDescription = "Profile Image",
            modifier = Modifier
                .size(43.dp)
                .clip(CircleShape)
        )


        Spacer(modifier = Modifier.width(12.dp))

        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = chat.userName,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = chat.lastMessage,
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray
            )
        }

        Text(
            text = formatTimestamp(chat.timestamp),
            style = MaterialTheme.typography.labelSmall,
            color = Color.Gray
        )
    }
}

fun formatTimestamp(timestamp: Long): String {
    // Format the timestamp to display time or date (e.g., "10:30 AM" or "Jan 20")
    val date = java.util.Date(timestamp)
    val format = java.text.SimpleDateFormat("hh:mm a", java.util.Locale.getDefault())
    return format.format(date)
}

suspend fun fetchRecentChats(
    firestore: FirebaseFirestore,
    astrologerId: String,
    onComplete: (List<RecentChat>) -> Unit
) {
    val recentChats = mutableListOf<RecentChat>()
    LoadingStateManager.showLoading()

    try {
        val chatDocs = firestore.collection("users")
            .document(astrologerId)
            .collection("chats")
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .get()
            .await()

        for (doc in chatDocs.documents) {
            val chatId = doc.getString("chatId") ?: continue
            val astrologerId = doc.getString("astrologerId") ?: continue
            val timestamp = doc.getTimestamp("timestamp")?.toDate()?.time ?: 0L

            // Fetch user details
            val userDoc = firestore.collection("astrologers").document(astrologerId).get().await()
            val userName = userDoc.getString("name") ?: "Unknown"
            val profileImageUrl = userDoc.getString("profileImageUrl") ?: ""

            // Fetch last message
            val lastMessageDoc = firestore.collection("chats")
                .document(chatId)
                .collection("messages")
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .limit(1)
                .get()
                .await()

            val lastMessage = if (lastMessageDoc.documents.isNotEmpty()) {
                val lastDoc = lastMessageDoc.documents[0]
                val text = lastDoc.getString("text")?.trim() ?: ""
                val image = lastDoc.getString("imageMessageRef")?.trim() ?: ""

                when {
                    text.isNotEmpty() && image.isNotEmpty() -> text // text with image
                    text.isNotEmpty() -> text
                    image.isNotEmpty() -> "Sent an image"
                    else -> "Tap to view"
                }
            } else {
                "Tap to view"
            }

            recentChats.add(
                RecentChat(
                    userId = astrologerId,
                    chatId = chatId,
                    userName = userName,
                    profileImageUrl = profileImageUrl,
                    lastMessage = lastMessage,
                    timestamp = timestamp
                )
            )
        }

        LoadingStateManager.hideLoading()
        onComplete(recentChats)
    } catch (e: Exception) {
        e.printStackTrace()
        onComplete(emptyList())
    }
}
data class RecentConversation(
    val profilePicture: String,
    val name: String,
    val lastMessage: String,
    val lastMessageTime: String,
    val isSeen: Boolean,
)

@Composable
fun RecentConversationCard(
    recentConversation: RecentConversation,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(5.dp, 4.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(10.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp, 17.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            AsyncImage(
                model = "https://static.wikia.nocookie.net/pewdiepieminecraft/images/8/8b/Jack_a_boy.jpg/revision/latest?cb=20191207132404",
                contentDescription = "Profile",
                modifier = Modifier
                    .size(37.dp)
                    .clip(CircleShape)
            )

            Spacer(modifier = Modifier.width(14.dp))

            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 8.dp)
            ) {
                // Name
                Text(
                    text = recentConversation.name,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(6.dp))
                // Last Message Row
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Seen Tick
                    Icon(
                        imageVector = Icons.Default.Done, // Use your tick icon
                        contentDescription = "Seen Status",
                        tint = if (recentConversation.isSeen) Color.Green else Color.Gray,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    // Last Message
                    Text(
                        text = recentConversation.lastMessage,
                        style = MaterialTheme.typography.bodySmall,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )
                }
            }
            // Last Message Time
            Text(
                text = recentConversation.lastMessageTime,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                modifier = Modifier.padding(start = 8.dp)
            )
        }
    }
}




