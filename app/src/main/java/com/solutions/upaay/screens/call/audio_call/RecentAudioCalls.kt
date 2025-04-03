package com.solutions.upaay.screens.call.audio_call


import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.solutions.upaay.utils.loading.LoadingStateManager
import com.solutions.upaay.utils.translate.TranslatedText
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

suspend fun getFirebaseServerTimestamp(): Date? = suspendCoroutine { continuation ->
    val firestore = FirebaseFirestore.getInstance()
    val ref = firestore.collection("server_time").document("now_temp")
    ref.set(mapOf("timestamp" to FieldValue.serverTimestamp()))
        .addOnSuccessListener {
            ref.get().addOnSuccessListener { snapshot ->
                val serverTime = snapshot.getTimestamp("timestamp")?.toDate()
                continuation.resume(serverTime)
            }
        }
        .addOnFailureListener {
            continuation.resume(null)
        }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecentAudioCalls(navController: NavController) {
    val firestore = FirebaseFirestore.getInstance()
    val astrologerId = FirebaseAuth.getInstance().currentUser?.uid ?: return
    val callCollection = firestore.collection("users").document(astrologerId)
        .collection("audiocalls")

    val recentCalls = remember { mutableStateListOf<Map<String, Any>>() }
    var lastVisibleCall by remember { mutableStateOf<DocumentSnapshot?>(null) }
    var selectedFilter by remember { mutableStateOf("Today") }
    var hasMore by remember { mutableStateOf(true) }
    var isLoadingMore by remember { mutableStateOf(false) }

    val filters = listOf("Today", "Yesterday", "This Week", "This Month", "All")

    var firebaseNow by remember { mutableStateOf<Date?>(null) }

    // Fetch server time first
    LaunchedEffect(Unit) {
        val serverTime = getFirebaseServerTimestamp()
        firebaseNow = serverTime
    }

    fun isSameDay(date1: Date, date2: Date): Boolean {
        val cal1 = Calendar.getInstance().apply { time = date1 }
        val cal2 = Calendar.getInstance().apply { time = date2 }
        return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR)
    }

    fun loadRecentCalls(initial: Boolean = false) {
        if (firebaseNow == null) return

        LoadingStateManager.showLoading()
        isLoadingMore = true

        var query = callCollection
            .orderBy("callStartTimestamp", Query.Direction.DESCENDING)

        if (selectedFilter == "All") {
            query = query.limit(10)
            if (!initial && lastVisibleCall != null) {
                query = query.startAfter(lastVisibleCall!!)
            }
        }

        query.get().addOnSuccessListener { snapshot ->
            val nowCal = Calendar.getInstance().apply { time = firebaseNow!! }
            val newCalls = mutableListOf<Map<String, Any>>()

            val documents = snapshot.documents
            for (document in documents) {
                val data = document.data ?: continue
                val status = data["status"] as? String ?: "ended"
                if (status == "connecting" || status == "ongoing") continue

                val timestamp = data["callStartTimestamp"] as? Timestamp ?: continue
                val callDate = timestamp.toDate()
                val callCal = Calendar.getInstance().apply { time = callDate }

                val include = when (selectedFilter) {
                    "Today" -> isSameDay(callDate, firebaseNow!!)
                    "Yesterday" -> {
                        val yesterday = Calendar.getInstance().apply {
                            time = firebaseNow!!
                            add(Calendar.DAY_OF_YEAR, -1)
                        }
                        isSameDay(callDate, yesterday.time)
                    }

                    "This Week" -> {
                        callCal.get(Calendar.WEEK_OF_YEAR) == nowCal.get(Calendar.WEEK_OF_YEAR) &&
                                callCal.get(Calendar.YEAR) == nowCal.get(Calendar.YEAR)
                    }

                    "This Month" -> {
                        callCal.get(Calendar.MONTH) == nowCal.get(Calendar.MONTH) &&
                                callCal.get(Calendar.YEAR) == nowCal.get(Calendar.YEAR)
                    }

                    else -> true
                }

                if (include) {
                    newCalls.add(data)
                }
            }

            if (initial) {
                recentCalls.clear()
            }

            recentCalls.addAll(newCalls)

            if (documents.isNotEmpty()) {
                lastVisibleCall = documents.last()
            }

            hasMore = documents.size >= 10
            isLoadingMore = false
            LoadingStateManager.hideLoading()
        }
    }

    // Load when filter changes and after firebaseNow is available
    LaunchedEffect(selectedFilter, firebaseNow) {
        if (firebaseNow != null) {
            lastVisibleCall = null
            hasMore = true
            loadRecentCalls(initial = true)
        }
    }

    // UI Section
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp)
    ) {

        item {
            TopAppBar(
                title = { Text("Recent Calls", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
            Spacer(modifier = Modifier.height(12.dp))

//            TranslatedText("Recent Calls", style = MaterialTheme.typography.headlineSmall)
//            Spacer(modifier = Modifier.height(8.dp))

            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(filters) { filter ->
                    val isSelected = filter == selectedFilter
                    Text(
                        text = filter,
                        modifier = Modifier
                            .border(1.dp, Color.Black, RoundedCornerShape(20.dp))
                            .background(
                                if (isSelected) Color.Black.copy(alpha = 0.1f) else Color.Transparent,
                                shape = RoundedCornerShape(20.dp)
                            )
                            .clickable { selectedFilter = filter }
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        color = Color.Black,
                        fontSize = 14.sp
                    )
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        if (recentCalls.isEmpty()) {
            item {
                val message =
                    if (selectedFilter == "All") "No calls yet" else "No calls $selectedFilter"

                TranslatedText(
                    message,
                    color = Color.Gray,
                    textAlign = TextAlign.Center
//                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
            }
        }

        items(recentCalls) { call ->
            val userId = call["userId"] as? String ?: ""
            val callStart = call["callStartTimestamp"] as? Timestamp
            val callEnd = call["endTime"] as? Timestamp
            val initialUserBalance = (call["initialUserBalance"] as? Number)?.toDouble()?: 0.0
            val amountDeducted = (call["amountDeducted"] as? Number)?.toDouble() ?: 0.0

            var userName by remember { mutableStateOf("Loading...") }
            var profileImageUrl by remember { mutableStateOf("") }

            LaunchedEffect(userId) {
                firestore.collection("users").document(userId).get()
                    .addOnSuccessListener { document ->
                        userName = document.getString("name") ?: "Unknown"
                        profileImageUrl = document.getString("profileImageUrl")
                            ?: "https://i0.wp.com/static.vecteezy.com/system/resources/previews/036/280/650/original/default-avatar-profile-icon-social-media-user-image-gray-avatar-icon-blank-profile-silhouette-illustration-vector.jpg?ssl=1"
                    }
            }

            val duration = if (callStart != null && callEnd != null) {
                val diff = callEnd.seconds - callStart.seconds
                val minutes = diff / 60
                val seconds = diff % 60
                "$minutes min $seconds sec"
            } else ""

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 6.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // 🕒 Call started time - Centered top
                    callStart?.toDate()?.let { date ->
                        Text(
                            text = "Call started at: ${SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault()).format(date)}",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color.Gray,
                            textAlign = TextAlign.Start,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    // 🧍 Profile image + name + duration
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Image(
                            painter = rememberAsyncImagePainter(profileImageUrl),
                            contentDescription = "Profile Image",
                            modifier = Modifier
                                .size(50.dp)
                                .clip(CircleShape)
                        )

                        Spacer(modifier = Modifier.width(12.dp))

                        Column {
                            Text(
                                userName,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                            if (duration.isNotEmpty()) {
                                Text(
                                    duration,
                                    fontSize = 13.sp,
                                    color = Color.DarkGray
                                )
                            }
                        }
                    }

                    // 💰 Amount + Balance row
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(end = 10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Amount Deducted:\n₹${String.format("%.2f", amountDeducted)}",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            textAlign = TextAlign.Center,
                            color = Color(0xFFDD2020)
                        )
                        Text(
                            text = "Final Balance:\n₹${String.format("%.2f", initialUserBalance - amountDeducted)}",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            textAlign = TextAlign.Center,
                            color = Color.Black
                        )
                    }
                }
            }

        }

        if (selectedFilter == "All" && hasMore) {
            item {
                Button(
                    onClick = { loadRecentCalls() },
                    enabled = !isLoadingMore,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp)
                ) {
                    if (isLoadingMore) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            strokeWidth = 2.dp
                        )
                        Spacer(Modifier.width(8.dp))
                        Text("Loading...")
                    } else {
                        Text("Load More")
                    }
                }
            }
        }
    }
}