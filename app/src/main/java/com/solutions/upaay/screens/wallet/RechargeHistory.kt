package com.solutions.upaay.screens.wallet

import androidx.compose.runtime.Composable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.Query
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RechargeHistoryScreen(navController: NavController, userId: String) {
    val firestore = FirebaseFirestore.getInstance()
    var payments by remember { mutableStateOf<List<DocumentSnapshot>>(emptyList()) }
    var lastDocument by remember { mutableStateOf<DocumentSnapshot?>(null) }
    var isLoading by remember { mutableStateOf(false) }
    var isEndReached by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()

    val pageSize = 10

    LaunchedEffect(Unit) {
        loadMorePayments(firestore, userId, null, pageSize) { newPayments, lastDoc ->
            payments = newPayments
            lastDocument = lastDoc
            isEndReached = newPayments.size < pageSize
        }
    }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        TopAppBar(
            title = { Text("Recent Transactions") },
            navigationIcon = {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                }
            },
        )

        Spacer(modifier = Modifier.height(12.dp))

        if (payments.isEmpty() && !isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("No payments Yet.", color = Color.Gray, fontSize = 16.sp)
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.weight(1f)
            ) {
                items(payments) { doc ->
                    RechargeItemCard(doc)
                }

                item {
                    if (!isEndReached) {
                        Button(
                            onClick = {
                                if (!isLoading) {
                                    isLoading = true

                                    coroutineScope.launch {
                                        loadMorePayments(
                                            firestore, userId, lastDocument, pageSize
                                        ) { newPayments, lastDoc ->
                                            payments += newPayments
                                            lastDocument = lastDoc
                                            isLoading = false
                                            isEndReached = newPayments.size < pageSize
                                        }
                                    }
                                }
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Load More")
                        }
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(50.dp))
                }
            }
        }
    }
}

suspend fun loadMorePayments(
    firestore: FirebaseFirestore,
    userId: String,
    lastDoc: DocumentSnapshot?,
    limit: Int,
    onResult: (List<DocumentSnapshot>, DocumentSnapshot?) -> Unit
) {
    val query = firestore.collection("users")
        .document(userId)
        .collection("payments")
        .orderBy("paymentCallbackAt", Query.Direction.DESCENDING)
        .let { if (lastDoc != null) it.startAfter(lastDoc) else it }
        .limit(limit.toLong())

    val snapshot = query.get().await()
    val documents = snapshot.documents
    val lastVisible = documents.lastOrNull()
    onResult(documents, lastVisible)
}

@Composable
fun RechargeItemCard(doc: DocumentSnapshot) {
    val amount = doc.getDouble("amount") ?: 0.0
    val transactionId = doc.getString("transactionId") ?: "N/A"
    val history = doc.getString("history") ?: "Recharge"
    val before = doc.getDouble("Balance Updates.checkHistoryBalance.beforePayment") ?: 0.0
    val after = doc.getDouble("Balance Updates.checkHistoryBalance.afterPayment") ?: 0.0
    val timestamp = doc.getTimestamp("historyCreatedAt")?.toDate()
    val formattedTime = timestamp?.let {
        SimpleDateFormat("dd MMM yyyy • hh:mm a", Locale.getDefault()).format(it)
    } ?: "Unknown Time"

    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(4.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(modifier = Modifier.padding(12.dp, 8.dp)) {
            Text(
                text = formattedTime,
                fontSize = 12.sp,
                color = Color.Gray,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(12.dp))


            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column {
                    Text(text = history, fontWeight = FontWeight.Bold)
                    Text(text = "Txn ID: $transactionId", fontSize = 12.sp, color = Color.Gray)
                }
                Text(text = "₹$amount", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Before: ₹$before", fontSize = 12.sp)
                Text("After: ₹$after", fontSize = 12.sp)
            }
        }
    }
}

//
//@Composable
//fun SampleRechargeItemCard() {
//    Card(
//        modifier = Modifier.fillMaxWidth(),
//        elevation = CardDefaults.cardElevation(4.dp),
//        shape = RoundedCornerShape(12.dp),
//        colors = CardDefaults.cardColors(containerColor = Color.White)
//    ) {
//        Column(modifier = Modifier.padding(12.dp, 8.dp)) {
//            Text(
//                text = "12 March, 2069",
//                fontSize = 12.sp,
//                color = Color.Gray,
//                modifier = Modifier.fillMaxWidth(),
//                textAlign = TextAlign.Center
//            )
//
//            Spacer(modifier = Modifier.height(12.dp))
//
//
//            Row(
//                horizontalArrangement = Arrangement.SpaceBetween,
//                modifier = Modifier.fillMaxWidth()
//            ) {
//                Column {
//                    Text(text = "Recharge Via UPI", fontWeight = FontWeight.Bold)
//                    Text(text = "Txn ID: $123884899954", fontSize = 12.sp, color = Color.Gray)
//                }
//                Text(text = "₹100", fontWeight = FontWeight.Bold, fontSize = 16.sp)
//            }
//
//            Spacer(modifier = Modifier.height(10.dp))
//
//            Row(
//                horizontalArrangement = Arrangement.SpaceBetween,
//                modifier = Modifier.fillMaxWidth()
//            ) {
//                Text("Before: ₹100", fontSize = 12.sp)
//                Text("After: ₹200", fontSize = 12.sp)
//            }
//        }
//    }
//}
