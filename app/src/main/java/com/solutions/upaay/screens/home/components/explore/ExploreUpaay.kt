package com.solutions.upaay.screens.home.components.explore

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.net.Uri
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.Toast
import android.widget.VideoView
import androidx.annotation.Keep
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Comment
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.VideoSize
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.compose.rememberAsyncImagePainter
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.solutions.upaay.utils.translate.TranslatedText
import kotlinx.coroutines.tasks.await
import androidx.media3.ui.R as PlayerUiR

@Composable
fun ExploreUpaay(navController: NavController) {
    val firestore = FirebaseFirestore.getInstance()
    var uploads by remember { mutableStateOf<List<UploadWithAstrologer>>(emptyList()) }
    var lastDocument by remember { mutableStateOf<DocumentSnapshot?>(null) }
    var isLoading by remember { mutableStateOf(false) }
    var isEndReached by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        isLoading = true
        val (newUploads, lastDoc) = fetchUploads(firestore)
        uploads = newUploads
        lastDocument = lastDoc
        isLoading = false
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        when {
            isLoading && uploads.isEmpty() -> {
                CircularProgressIndicator()
            }

            uploads.isEmpty() -> {
                TranslatedText(
                    text = "No Posts Yet",
                    fontSize = 16.sp,
                    color = Color.Gray,
                    modifier = Modifier.align(Alignment.Center)
                )
            }

            else -> {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(uploads) { uploadWithAstrologer ->
                        UploadItem(uploadWithAstrologer)
                    }

                    item {
                        Spacer(modifier = Modifier.height(200.dp))
                    }
                }
            }
        }
    }

}

@Composable
fun UploadItem(uploadWithAstrologer: UploadWithAstrologer) {
    val upload = uploadWithAstrologer.upload
    val context = LocalContext.current
    val currentUserId = FirebaseAuth.getInstance().currentUser?.uid

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clip(RoundedCornerShape(12.dp))
//            .background(
//                brush = Brush.linearGradient(
//                    colors = listOf(
//                        Color(0xFFF8F1E4),
//                        Color(0xFFE7CFBE),
//                    )
//                )
//            )
            .clickable {

            },
//        elevation = CardDefaults.elevatedCardElevation(
//            defaultElevation = 0.5.dp,
//        ),
        colors = CardDefaults.cardColors(
            containerColor = Color.Transparent // Prevents default card color
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Image(
                    painter = rememberAsyncImagePainter(if(uploadWithAstrologer.astrologerProfileUrl.isNullOrEmpty()) "https://w7.pngwing.com/pngs/507/691/png-transparent-nakshatra-hindu-astrology-horoscope-astrological-sign-astrology-decor-aries-ascendant-thumbnail.png" else uploadWithAstrologer.astrologerProfileUrl),
                    contentDescription = "Profile Image",
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    TranslatedText(
                        uploadWithAstrologer.astrologerName,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                    TranslatedText(
                        formatTimestamp(upload.timestamp),
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                }
            }
            upload.text?.let {
                TranslatedText(it, fontSize = 14.sp, modifier = Modifier.padding(8.dp))
            }

            when (upload.type) {
                "Image" -> {
                    Image(
                        painter = rememberAsyncImagePainter(upload.url),
                        contentDescription = "Uploaded Image",
                        modifier = Modifier
                            .fillMaxWidth()
                            .aspectRatio(1f),
                        contentScale = ContentScale.Crop
                    )
                }

                "Video" -> {
                    VideoPlayer(upload.url)
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 10.dp),
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically
            ) {
                currentUserId?.let { LikeButton(upload.contentId, it, upload.likes) }
                currentUserId?.let { CommentButton(upload.contentId, it, upload.comments) }
//                ShareButton(upload.contentId, context)
            }
        }
    }
}

@Composable
fun VideoPlayer(videoUrl: String, modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val videoHeight = remember { mutableStateOf(0) }
    val videoWidth = remember { mutableStateOf(0) }
    val iconSizePx = with(LocalDensity.current) { 15.dp.roundToPx() }

    val exoPlayer = remember(videoUrl) {
        ExoPlayer.Builder(context).build().apply {
            val mediaItem = MediaItem.fromUri(videoUrl)
            setMediaItem(mediaItem)
            prepare()
            playWhenReady = false

            // Listen for video size to update layout
            addListener(object : Player.Listener {
                override fun onVideoSizeChanged(videoSize: VideoSize) {
                    super.onVideoSizeChanged(videoSize)
                    videoWidth.value = videoSize.width
                    videoHeight.value = videoSize.height
                }
            })
        }
    }

    val aspectRatio = if (videoWidth.value > 0 && videoHeight.value > 0) {
        videoWidth.value.toFloat() / videoHeight.value
    } else {
        9f / 16f // fallback until video loads
    }

    AndroidView(
        factory = {
            PlayerView(it).apply {
                player = exoPlayer
                useController = true
                layoutParams = FrameLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                )
                post {
                    val iconIds = listOf(
                        PlayerUiR.id.exo_play,
                        PlayerUiR.id.exo_pause,
                        PlayerUiR.id.exo_rew,
                        PlayerUiR.id.exo_ffwd,
                        PlayerUiR.id.exo_fullscreen
                    )

                    iconIds.forEach { id ->
                        findViewById<View?>(id)?.apply {
                            layoutParams = layoutParams?.apply {
                                width = iconSizePx
                                height = iconSizePx
                            }
                        }
                    }
                }
            }
        },
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(aspectRatio)
    )
}


@Composable
fun LikeButton(uploadId: String, currentUserId: String, likesCount: Int) {
    val firestore = FirebaseFirestore.getInstance()
    val likesRef = firestore.collection("uploads").document(uploadId).collection("likesCollection")
        .document(currentUserId)
    var isLiked by remember { mutableStateOf(false) }
    var likeCount by remember { mutableStateOf(likesCount) }

    LaunchedEffect(uploadId) {
        likesRef.get().addOnSuccessListener { document ->
            isLiked = document.exists()
        }
    }

    Row(
        modifier = Modifier.clickable {
            if (isLiked) {
                likesRef.delete().addOnSuccessListener {
                    likeCount -= 1
                    isLiked = false
                    firestore.collection("uploads").document(uploadId).update("likes", likeCount)
                }
            } else {
                likesRef.set(mapOf("liked" to true)).addOnSuccessListener {
                    likeCount += 1
                    isLiked = true
                    firestore.collection("uploads").document(uploadId).update("likes", likeCount)
                }
            }
        }
    ) {
        Icon(
            imageVector = if (isLiked) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
            contentDescription = "Like",
            tint = if (isLiked) Color.Red else Color.Black,
            modifier = Modifier.animateContentSize()
        )
        Text(text = "$likeCount Likes", fontSize = 14.sp, modifier = Modifier.padding(start = 7.dp))
    }
}

// Comment Button (Opens Bottom Sheet)
@Composable
fun CommentButton(uploadId: String, currentUserId: String, commentsCount: Int) {
    val showBottomSheet = remember { mutableStateOf(false) }
    var totalCommentCount by remember { mutableStateOf(commentsCount) }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .padding(start = 30.dp)
            .clickable { showBottomSheet.value = true }
    ) {
        Icon(
            Icons.Filled.Comment,
            contentDescription = "Comments",
            tint = Color.Black,
        )
        Text(
            text = "$totalCommentCount Comments",
            fontSize = 14.sp,
            modifier = Modifier.padding(start = 9.dp)
        )
    }

    if (showBottomSheet.value) {
        CommentBottomSheet(
            uploadId = uploadId,
            currentUserId = currentUserId,
            onDismiss = { showBottomSheet.value = false },
            onCommentPosted = {
                totalCommentCount += 1
            }
        )
    }
}


// Comment Bottom Sheet
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CommentBottomSheet(uploadId: String, currentUserId: String, onDismiss: () -> Unit, onCommentPosted: () -> Unit) {
    val firestore = FirebaseFirestore.getInstance()
    val commentsRef =
        firestore.collection("uploads").document(uploadId).collection("commentsCollection")
    var commentText by remember { mutableStateOf(TextFieldValue("")) }
    var comments by remember { mutableStateOf<List<CommentWithUser>>(emptyList()) }

    LaunchedEffect(uploadId) {
        commentsRef.orderBy("timestamp", Query.Direction.ASCENDING)
            .addSnapshotListener { snapshot, _ ->
                val fetchedComments = snapshot?.toObjects(Comment::class.java) ?: emptyList()

                val updatedComments = mutableListOf<CommentWithUser>()

                fetchedComments.forEach { comment ->
                    firestore.collection("users").document(comment.userId).get()
                        .addOnSuccessListener { userSnapshot ->
                            val userName = userSnapshot.getString("name") ?: "Unknown"
                            val profileImageUrl = userSnapshot.getString("profileImageUrl") ?: ""

                            updatedComments.add(
                                CommentWithUser(
                                    comment = comment,
                                    userName = userName,
                                    profileImageUrl = profileImageUrl
                                )
                            )

                            // Update state when all user data is fetched
                            if (updatedComments.size == fetchedComments.size) {
                                comments = updatedComments
                            }
                        }
                }
            }
    }


    ModalBottomSheet(onDismissRequest = { onDismiss() }) {
        Column(modifier = Modifier.padding(16.dp)) {
            if (comments.isEmpty()) {
                TranslatedText(
                    "No comments yet",
                    fontSize = 14.sp,
                    color = Color.Gray,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            } else {
                comments.forEach { commentWithUser ->
                    CommentItem(commentWithUser)
                }
            }

            Spacer(modifier = Modifier.height(30.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextField(
                    value = commentText,
                    onValueChange = { commentText = it },
                    placeholder = { Text("Write a comment...") },
                    modifier = Modifier
                        .weight(1f)
                        .background(Color.White, RoundedCornerShape(5.dp))
                        .border(1.dp, Color.Gray, RoundedCornerShape(5.dp))
                )

                Button(
                    onClick = {
                        val newComment = Comment(currentUserId, commentText.text, Timestamp.now())

                        commentsRef.add(newComment).addOnSuccessListener {
                            FirebaseFirestore.getInstance().collection("uploads")
                                .document(uploadId)
                                .update("comments", FieldValue.increment(1))

                            onCommentPosted()
                        }

                        commentText = TextFieldValue("")
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                    modifier = Modifier
                        .padding(start = 8.dp)
                        .border(5.dp, Color(0xFFD5874F), RoundedCornerShape(5.dp))
                        .background(Color(0xFFD5874F), RoundedCornerShape(5.dp))
                ) {
                    Text("Post", color = Color.White)
                }

            }
        }
    }
}

// 🟢 Comment Item UI
@Composable
fun CommentItem(commentWithUser: CommentWithUser) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White, RoundedCornerShape(8.dp))
            .padding(10.dp)
    ) {

        AsyncImage(
            model = if (commentWithUser.profileImageUrl.isNotEmpty())
                commentWithUser.profileImageUrl
            else
                "https://i0.wp.com/static.vecteezy.com/system/resources/previews/036/280/650/original/default-avatar-profile-icon-social-media-user-image-gray-avatar-icon-blank-profile-silhouette-illustration-vector.jpg?ssl=1",
            contentDescription = "Profile Image",
            contentScale = ContentScale.Inside,
            modifier = Modifier
                .size(35.dp)
                .clip(CircleShape)
        )

        Spacer(modifier = Modifier.width(10.dp))

        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(commentWithUser.userName, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                Text(
                    formatTimestamp(commentWithUser.comment.timestamp),
                    fontSize = 12.sp,
                    color = Color.Gray
                )
            }
            TranslatedText(
                commentWithUser.comment.text,
                fontSize = 14.sp,
                modifier = Modifier.padding(top = 4.dp)
            )
        }
    }
}

// 🟡 Data Model for Comment with User Info
@Keep
data class CommentWithUser(
    val comment: Comment,
    val userName: String,
    val profileImageUrl: String
)


// Share Button
@Composable
fun ShareButton(uploadId: String, context: Context) {
    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    val postUrl = "https://upaay.com/post/$uploadId"

    IconButton(modifier = Modifier.padding(start = 20.dp), onClick = {
        clipboard.setPrimaryClip(ClipData.newPlainText("Post Link", postUrl))
        Toast.makeText(context, "Link Copied!", Toast.LENGTH_SHORT).show()
    }) {
        Icon(
            Icons.Filled.Share,
            contentDescription = "Share",
            tint = Color.Black,
        )
    }
}

// Helper Functions
fun formatTimestamp(timestamp: Timestamp): String {
    val timeInMillis = timestamp.seconds * 1000 + timestamp.nanoseconds / 1000000
    return java.text.SimpleDateFormat("hh:mm a", java.util.Locale.getDefault())
        .format(java.util.Date(timeInMillis))
}

@Keep
data class Comment(
    val userId: String = "",
    val text: String = "",
    val timestamp: Timestamp = Timestamp.now()
)


// This is the old working code of this screen without like and comment functionality
// @Composable
//fun ExploreUpaay(navController: NavController) {
//    val firestore = FirebaseFirestore.getInstance()
//
//    var uploads by remember { mutableStateOf<List<UploadWithAstrologer>>(emptyList()) }
//    var lastDocument by remember { mutableStateOf<DocumentSnapshot?>(null) }
//    var isLoading by remember { mutableStateOf(false) }
//    var isEndReached by remember { mutableStateOf(false) }
//
//    // Initial Fetch
//    LaunchedEffect(Unit) {
//        isLoading = true
//        val (newUploads, lastDoc) = fetchUploads(firestore)
//        uploads = newUploads
//        lastDocument = lastDoc
//        isLoading = false
//    }
//
//    Box(
//        modifier = Modifier
//            .fillMaxSize()
//            .background(Color(0xFFFAF7F3))
//    ) {
//        Column(modifier = Modifier.padding(16.dp)) {
//            if (isLoading && uploads.isEmpty()) {
//                CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
//            } else if (uploads.isEmpty()) {
//                TranslatedText(
//                    text = "No Posts Yet",
//                    modifier = Modifier.fillMaxSize(),
//                    textAlign = TextAlign.Center
//                )
//            } else {
//                LazyColumn(
//                    modifier = Modifier.fillMaxSize(),
//                    verticalArrangement = Arrangement.spacedBy(16.dp)
//                ) {
//                    items(uploads) { uploadWithAstrologer ->
//                        UploadItem(uploadWithAstrologer)
//                    }
//
//                    // Pagination Loader
//                    item {
//                        if (!isEndReached) {
//                            LaunchedEffect(Unit) {
//                                isLoading = true
//                                val (newUploads, lastDoc) = fetchUploads(firestore, lastDocument)
//
//                                if (newUploads.isNotEmpty()) {
//                                    uploads = uploads + newUploads
//                                    lastDocument = lastDoc
//                                } else {
//                                    isEndReached = true
//                                }
//                                isLoading = false
//                            }
//                        }
//                    }
//                }
//            }
//        }
//    }
//}
//
//
//@Composable
//fun UploadItem(uploadWithAstrologer: UploadWithAstrologer) {
//    val upload = uploadWithAstrologer.upload
//
//    Column(modifier = Modifier.fillMaxWidth()) {
//        Row(
//            modifier = Modifier
//                .fillMaxWidth()
//                .padding(vertical = 5.dp),
//            verticalAlignment = Alignment.CenterVertically
//        ) {
//            // Astrologer's Profile Image
//            Image(
//                painter = rememberAsyncImagePainter(
//                    model = uploadWithAstrologer.astrologerProfileUrl ?: defaultProfileUrl
//                ),
//                contentDescription = "Profile Image",
//                modifier = Modifier
//                    .size(33.dp)
//                    .clip(CircleShape)
//            )
//
//            Spacer(modifier = Modifier.width(12.dp))
//
//            Column(
//                modifier = Modifier.weight(1f)
//            ) {
//                // Astrologer Name
//                TranslatedText(
//                    text = uploadWithAstrologer.astrologerName,
//                    style = MaterialTheme.typography.bodyMedium,
//                    fontWeight = FontWeight.Bold
//                )
//
//                // Upload Timestamp
//                TranslatedText(
//                    text = formatTimestamp(upload.timestamp),
//                    style = MaterialTheme.typography.bodySmall,
//                    color = Color.Gray
//                )
//            }
//        }
//
//        // Post Text
//        upload.text?.let {
//            TranslatedText(
//                text = it,
//                style = MaterialTheme.typography.bodyMedium,
//                modifier = Modifier.padding(5.dp, 8.dp),
//                textAlign = TextAlign.Start
//            )
//        }
//
//        // Display Image or Video
//        if (upload.type != "text") {
//            upload.url.let {
//                val painter = rememberAsyncImagePainter(upload.url)
//                Image(
//                    painter = painter,
//                    contentDescription = "Uploaded content",
//                    modifier = Modifier
//                        .fillMaxWidth()
//                        .height(250.dp),
//                    contentScale = ContentScale.Crop
//                )
//            }
//        }
//
//        Spacer(modifier = Modifier.height(8.dp))
//
//        // Display Likes, Comments, and Shares
//        Row(
//            modifier = Modifier.fillMaxWidth(),
//            horizontalArrangement = Arrangement.spacedBy(16.dp)
//        ) {
//            Row(verticalAlignment = Alignment.CenterVertically) {
//                Icon(Icons.Filled.Favorite, contentDescription = "Likes", tint = Color.Red)
//                Spacer(modifier = Modifier.width(4.dp))
//                TranslatedText(text = "${upload.likes} Likes")
//            }
//
//            Row(verticalAlignment = Alignment.CenterVertically) {
//                Icon(Icons.Filled.Comment, contentDescription = "Comments")
//                Spacer(modifier = Modifier.width(4.dp))
//                TranslatedText(text = "${upload.comments} Comments")
//            }
//
//            Row(verticalAlignment = Alignment.CenterVertically) {
//                Icon(Icons.Filled.Share, contentDescription = "Shares")
//                Spacer(modifier = Modifier.width(4.dp))
//                TranslatedText(text = "Share")
//            }
//        }
//
//        Spacer(modifier = Modifier.height(8.dp))
//    }
//}
//

@Keep
data class Upload(
    val contentId: String,
    val url: String,
    val text: String? = null,
    val type: String,
    val timestamp: Timestamp,
    val likes: Int,
    val comments: Int
)

@Keep
data class UploadWithAstrologer(
    val upload: Upload,
    val astrologerName: String,
    val astrologerProfileUrl: String?
)

suspend fun fetchUploads(
    firestore: FirebaseFirestore,
    lastDocument: DocumentSnapshot? = null, // Track last document for pagination
    limit: Long = 7 // Fetch 7 uploads at a time
): Pair<List<UploadWithAstrologer>, DocumentSnapshot?> {
    val uploadsList = mutableListOf<UploadWithAstrologer>()

    try {
        var query = firestore.collection("uploads")
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .limit(limit)

        if (lastDocument != null) {
            query = query.startAfter(lastDocument)
        }

        val querySnapshot = query.get().await()
        val lastVisible = querySnapshot.documents.lastOrNull() // Track last document

        for (document in querySnapshot) {
            val contentId = document.id
            val text = document.getString("text")
            val url = document.getString("url") ?: ""
            val type = document.getString("type") ?: "Text"
            val timestamp = document.getTimestamp("timestamp") ?: Timestamp.now()
            val likes = document.getLong("likes")?.toInt() ?: 0
            val comments = document.getLong("comments")?.toInt() ?: 0
            val astrologerId = document.getString("astrologerId") ?: ""

            // Fetch astrologer details
            val astrologerRef = firestore.collection("astrologers").document(astrologerId)
            val astrologerSnapshot = astrologerRef.get().await()
            val astrologerName = astrologerSnapshot.getString("name") ?: "Unknown"
            val astrologerProfileUrl = astrologerSnapshot.getString("profileImageUrl")

            val upload = Upload(
                contentId = contentId,
                url = url,
                text = text,
                type = type,
                timestamp = timestamp,
                likes = likes,
                comments = comments
            )

            val uploadWithAstrologer = UploadWithAstrologer(
                upload = upload,
                astrologerName = astrologerName,
                astrologerProfileUrl = astrologerProfileUrl
            )

            uploadsList.add(uploadWithAstrologer)
        }

        return Pair(uploadsList, lastVisible)
    } catch (e: Exception) {
        return Pair(emptyList(), null)
    }
}


// This is for the explore page like instagram

//@Composable
//fun ExploreUpaay(modifier: Modifier = Modifier) {
//
//    val items = List(6) { it + 1 }
//    LazyColumn(
//        modifier = modifier.fillMaxSize().background(Color(0xFFFAF7F3)),
//        verticalArrangement = Arrangement.spacedBy(8.dp),
//    ) {
//        itemsIndexed(items) { index, _ ->
//            Row(
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .padding(horizontal = 8.dp),
//                horizontalArrangement = Arrangement.spacedBy(8.dp)
//            ) {
//
//                val isReelOnLeft = index % 2 == 1
//
//                if (isReelOnLeft) {
//                    ReelPlaceholder()
//                    PostColumnPlaceholder(modifier = Modifier.weight(1f))
//                } else {
//                    PostColumnPlaceholder(modifier = Modifier.weight(1f))
//                    ReelPlaceholder()
//                }
//            }
//        }
//    }
//}

//@Composable
//fun ReelPlaceholder() {
//    Box(
//        modifier = Modifier
//            .width((LocalConfiguration.current.screenWidthDp.dp / 3.2f))
//            .height((LocalConfiguration.current.screenWidthDp.dp / 3.2f) * 2.1f)
//            .clip(RoundedCornerShape(8.dp))
//            .background(Color(0xFFFAEDDC).copy(alpha = 0.6f)),
//        contentAlignment = Alignment.Center
//    ) {
//        TranslatedText(
//            text = "Reel",
//            color = Color.DarkGray,
//            style = MaterialTheme.typography.bodySmall
//        )
//    }
//}
//
//@Composable
//fun PostColumnPlaceholder(modifier: Modifier) {
//    Column(
//        modifier = modifier,
//        verticalArrangement = Arrangement.spacedBy(8.dp)
//    ) {
//        repeat(2) {
//            Row(
//                modifier = Modifier
//                    .fillMaxWidth(),
//                horizontalArrangement = Arrangement.spacedBy(8.dp)
//            ) {
//                repeat(2) {
//                    Box(
//                        modifier = Modifier
//                            .width(LocalConfiguration.current.screenWidthDp.dp / 3.2f)
//                            .height(LocalConfiguration.current.screenWidthDp.dp / 3.2f)
//                            .clip(RoundedCornerShape(8.dp))
//                            .background(Color(0xFFFAEDDC)),
//                        contentAlignment = Alignment.Center
//                    ) {
//                        TranslatedText(
//                            text = "Post",
//                            color = Color.DarkGray,
//                            style = MaterialTheme.typography.bodySmall
//                        )
//                    }
//                }
//            }
//        }
//    }
//}

