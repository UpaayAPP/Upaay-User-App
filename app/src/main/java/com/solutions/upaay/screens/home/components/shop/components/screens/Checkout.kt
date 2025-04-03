package com.solutions.upaay.screens.home.components.shop.components.screens

import android.content.SharedPreferences
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.solutions.upaay.screens.home.components.shop.Product
import com.solutions.upaay.utils.profile.ProfileUtils
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.util.UUID

@Composable
fun CheckoutScreen(
    navController: NavController,
    sharedPreferences: SharedPreferences,
    userId: String
) {
    val profileUtils = ProfileUtils.userProfileState.value
    val firestore = FirebaseFirestore.getInstance()
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var cartItems by remember { mutableStateOf<List<Product>>(emptyList()) }

    // Address Fields
    var fullName by remember { mutableStateOf(profileUtils.name ?: "") }
    var mobile by remember { mutableStateOf(profileUtils.mobileNumber ?: "") }
    var addressLine1 by remember { mutableStateOf("") }
    var addressLine2 by remember { mutableStateOf("") }
    var landmark by remember { mutableStateOf("") }
    var city by remember { mutableStateOf("") }
    var state by remember { mutableStateOf("") }
    var pincode by remember { mutableStateOf("") }

    var selectedPayment by remember { mutableStateOf("COD") }
    var isPlacingOrder by remember { mutableStateOf(false) }

    val primaryColor = Color(0xFFA24C13)

    LaunchedEffect(Unit) {
        val cartIds = sharedPreferences.getStringSet("cart", emptySet()) ?: emptySet()
        val products = firestore.collection("products").get().await().mapNotNull { it.toProduct() }
        cartItems = products.filter { it.id in cartIds }
    }

    val totalAmount = cartItems.sumOf { it.price.toDoubleOrNull() ?: 0.0 }
    val totalComparedAmount = cartItems.sumOf { it.comparedPrice.toDoubleOrNull() ?: 0.0 }
//    val tax = totalAmount * 0.05
    val grandTotal = totalAmount

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        item {
            Text(
                "Order Summary",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(Modifier.height(8.dp))
        }

        items(cartItems) { product ->
            Card(
                Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                shape = RoundedCornerShape(8.dp),
                elevation = CardDefaults.cardElevation(2.dp)
            ) {
                Row(Modifier.padding(10.dp)) {
                    AsyncImage(
                        model = product.images.firstOrNull(),
                        contentDescription = null,
                        modifier = Modifier
                            .size(60.dp)
                            .clip(RoundedCornerShape(6.dp)),
                        contentScale = ContentScale.Crop
                    )
                    Spacer(Modifier.width(12.dp))
                    Column {
                        Text(product.name, maxLines = 1, fontWeight = FontWeight.SemiBold)
                        Text("₹${product.price}", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        item {
            Spacer(Modifier.height(24.dp))
            Text(
                "Shipping Address",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(Modifier.height(12.dp))

            AddressField("Full Name", fullName) { fullName = it }
            AddressField("Mobile Number", mobile) { mobile = it }
            AddressField("Address Line 1", addressLine1) { addressLine1 = it }
            AddressField("Address Line 2 (Optional)", addressLine2) { addressLine2 = it }
            AddressField("Landmark", landmark) { landmark = it }
            AddressField("City", city) { city = it }
            AddressField("State", state) { state = it }
            AddressField("Pincode", pincode) { pincode = it }

            Spacer(Modifier.height(20.dp))
            Text(
                "Payment Method",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Row(verticalAlignment = Alignment.CenterVertically) {
                RadioButton(
                    selected = selectedPayment == "COD",
                    onClick = { selectedPayment = "COD" },
                    colors = RadioButtonDefaults.colors(selectedColor = primaryColor)
                )
                Text("Cash on Delivery", modifier = Modifier.padding(end = 16.dp))

                RadioButton(
                    selected = selectedPayment == "Online",
                    onClick = { selectedPayment = "Online" },
                    colors = RadioButtonDefaults.colors(selectedColor = primaryColor)
                )
                Text("Pay Online")
            }

            Spacer(Modifier.height(20.dp))
            Text(
                "Bill Details",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(Modifier.height(8.dp))
            Row(
                verticalAlignment = Alignment.Bottom,
            ) {
                Text(
                    text = "Subtotal: ₹%.2f".format(totalAmount),
                )
                Text(
                    text = "₹${"%.2f".format(totalComparedAmount)}",
                    textDecoration = TextDecoration.LineThrough,
                    modifier = Modifier.padding(start = 5.dp)
                )
            }
//            Text("Tax (5%%): ₹%.2f".format(tax))
            Text("Total Amount: ₹%.2f".format(grandTotal), fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(24.dp))

            Button(
                onClick = {
                    if (selectedPayment == "Online") {
                        Toast.makeText(context, "Online payments coming soon!", Toast.LENGTH_SHORT)
                            .show()
                        return@Button
                    }

                    // Validate all required fields
                    if (
                        fullName.isBlank() || mobile.isBlank() || pincode.isBlank() ||
                        state.isBlank() || city.isBlank() || addressLine1.isBlank()
                    ) {
                        Toast.makeText(
                            context,
                            "Please fill all address fields",
                            Toast.LENGTH_SHORT
                        ).show()
                        return@Button
                    }

                    scope.launch {
                        isPlacingOrder = true
                        val orderId = UUID.randomUUID().toString()

                        // Create main order document
                        val orderData = mapOf(
                            "userId" to userId,
                            "name" to fullName,
                            "mobile" to mobile,
                            "shippingAddress" to mapOf(
                                "pincode" to pincode,
                                "state" to state,
                                "city" to city,
                                "line1" to addressLine1,
                                "line2" to addressLine2
                            ),
                            "totalAmount" to grandTotal,
                            "paymentMethod" to selectedPayment,
                            "timestamp" to Timestamp.now()
                        )

                        try {
                            // Save order
                            firestore.collection("orders")
                                .document(orderId)
                                .set(orderData)
                                .await()

                            // Save all products under /productDetails
                            cartItems.forEach { product ->
                                val productDetails = mapOf(
                                    "id" to product.id,
                                    "name" to product.name,
                                    "price" to product.price,
                                    "image" to product.images.firstOrNull().orEmpty()
                                )

                                firestore.collection("orders")
                                    .document(orderId)
                                    .collection("productDetails")
                                    .add(productDetails)
                                    .await()
                            }

                            // Save reference in user's productOrders
                            firestore.collection("users")
                                .document(userId)
                                .collection("productOrders")
                                .document(orderId)
                                .set(
                                    mapOf(
                                        "orderId" to orderId,
                                        "timestamp" to Timestamp.now()
                                    )
                                )

                            // Clear cart
                            sharedPreferences.edit().putStringSet("cart", emptySet()).apply()

                            Toast.makeText(
                                context,
                                "Order placed successfully!",
                                Toast.LENGTH_SHORT
                            ).show()
                            navController.popBackStack()

                        } catch (e: Exception) {
                            Toast.makeText(
                                context,
                                e.message ?: "Order failed!",
                                Toast.LENGTH_SHORT
                            ).show()
                        } finally {
                            isPlacingOrder = false
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                colors = ButtonDefaults.buttonColors(containerColor = primaryColor),
                enabled = !isPlacingOrder
            ) {
                if (isPlacingOrder) {
                    CircularProgressIndicator(
                        color = Color.White,
                        strokeWidth = 2.dp,
                        modifier = Modifier.size(20.dp)
                    )
                } else {
                    Text("Proceed to Buy", color = Color.White)
                }
            }

            Spacer(Modifier.height(100.dp))
        }
    }
}

@Composable
fun AddressField(label: String, value: String, onChange: (String) -> Unit) {
    OutlinedTextField(
        value = value,
        onValueChange = onChange,
        label = { Text(label) },
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        shape = RoundedCornerShape(8.dp)
    )
}

fun DocumentSnapshot.toProduct(): Product? {
    return try {
        Product(
            id = getString("id") ?: id,
            name = getString("name") ?: "",
            description = getString("description") ?: "",
            price = getString("price") ?: "0",
            comparedPrice = getString("comparedPrice") ?: "0",
            images = get("images") as? List<String> ?: emptyList(),
            inStock = getBoolean("inStock") ?: true
        )
    } catch (e: Exception) {
        null
    }
}
