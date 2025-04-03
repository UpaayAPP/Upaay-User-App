package com.solutions.upaay.screens.home.components.shop.components.screens

import androidx.compose.runtime.Composable
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.annotation.Keep
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Star
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.google.firebase.Firebase
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.tasks.await
import java.net.URLEncoder

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyOrdersScreen(
    userId: String,
    navController: NavController
) {
    val firestore = Firebase.firestore
    val context = LocalContext.current
    var orders by remember { mutableStateOf<List<OrderWithId>>(emptyList()) }

    LaunchedEffect(Unit) {
        val userOrdersSnapshot = firestore.collection("users")
            .document(userId)
            .collection("productOrders")
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .get().await()

        val orderIds = userOrdersSnapshot.documents.mapNotNull { it.id }

        val fetchedOrders = mutableListOf<OrderWithId>()
        for (orderId in orderIds) {
            val orderDoc = firestore.collection("orders").document(orderId).get().await()
            val orderData = orderDoc.toObject(Order::class.java) ?: continue

            val productDetailsSnapshot = firestore.collection("orders")
                .document(orderId)
                .collection("productDetails")
                .get()
                .await()

            val productDetails =
                productDetailsSnapshot.documents.mapNotNull { it.toProductSummary() }

            fetchedOrders.add(OrderWithId(orderId, orderData, productDetails))
        }

        orders = fetchedOrders
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            TopAppBar(
                title = { Text("My Orders") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
            )

            Spacer(modifier = Modifier.height(12.dp))
        }
        items(orders) { orderWithId ->
            OrderCard(
                orderWithId = orderWithId,
                onContactAdmin = {
                    val message =
                        "Hello, I need an update on my order with ID: ${orderWithId.orderId}"
                    val url =
                        "https://wa.me/919318345767?text=${URLEncoder.encode(message, "UTF-8")}"
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                    context.startActivity(intent)
                }
            )
        }
    }
}

@Composable
fun OrderCard(orderWithId: OrderWithId, onContactAdmin: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(4.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            orderWithId.products.forEach { product ->
                Row(modifier = Modifier.padding(vertical = 8.dp)) {
                    AsyncImage(
                        model = product.image,
                        contentDescription = null,
                        modifier = Modifier
                            .size(80.dp)
                            .clip(RoundedCornerShape(8.dp)),
                        contentScale = ContentScale.Crop
                    )

                    Spacer(modifier = Modifier.width(12.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(product.name, fontWeight = FontWeight.Bold)
                        Text("₹${product.price}", color = Color(0xFF388E3C))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            repeat(5) {
                                Icon(
                                    imageVector = Icons.Default.Star,
                                    contentDescription = null,
                                    tint = Color(0xFFFFAA01),
                                    modifier = Modifier.size(14.dp)
                                )
                            }
                        }
                    }
                }
            }

            orderWithId.order.currentStage?.let {
                Text(
                    text = "Current Stage: $it",
                    color = Color(0xFF2E7D32),
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(top = 6.dp)
                )
            }

            if (orderWithId.order.currentStage?.lowercase() != "delivered") {
                Spacer(modifier = Modifier.height(8.dp))
                Button(
                    onClick = onContactAdmin,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFA24C13)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Contact Us For Updates")
                }
            }
        }
    }
}

@Keep
data class ShippingAddress(
    val pincode: String = "",
    val state: String = "",
    val city: String = "",
    val line1: String = "",
    val line2: String = ""
)

@Keep
data class Order(
    val userId: String = "",
    val name: String = "",
    val mobile: String = "",
    val shippingAddress: ShippingAddress = ShippingAddress(),
    val totalAmount: Double = 0.0,
    val paymentMethod: String = "",
    val timestamp: Timestamp = Timestamp.now(),
    val currentStage: String? = null
)

@Keep
data class OrderWithId(
    val orderId: String,
    val order: Order,
    val products: List<ProductSummary>
)

@Keep
data class ProductSummary(
    val id: String = "",
    val name: String = "",
    val price: String = "",
    val image: String = ""
)

fun DocumentSnapshot.toProductSummary(): ProductSummary? {
    return try {
        ProductSummary(
            id = getString("id").orEmpty(),
            name = getString("name").orEmpty(),
            price = getString("price").orEmpty(),
            image = getString("image").orEmpty()
        )
    } catch (e: Exception) {
        null
    }
}
