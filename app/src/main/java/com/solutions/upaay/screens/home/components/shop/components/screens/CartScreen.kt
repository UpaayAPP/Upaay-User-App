package com.solutions.upaay.screens.home.components.shop.components.screens

import android.content.SharedPreferences
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Divider
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import com.solutions.upaay.MainActivity
import com.solutions.upaay.screens.home.components.shop.Product
import com.solutions.upaay.screens.home.components.shop.ProductCardHorizontal
import kotlinx.coroutines.tasks.await

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CartScreen(
    navController: NavController,
    sharedPreferences: SharedPreferences
) {
    val firestore = Firebase.firestore
    val cartItems = remember { mutableStateListOf<Product>() }
    val context = LocalContext.current

    // Load products from Firestore based on cart IDs
    LaunchedEffect(Unit) {
        val cartIds = sharedPreferences.getStringSet("cart", emptySet()) ?: emptySet()
        if (cartIds.isNotEmpty()) {
            val fetchedProducts = cartIds.mapNotNull { id ->
                try {
                    val snapshot = firestore.collection("products").document(id).get().await()
                    snapshot.toObject(Product::class.java)
                } catch (e: Exception) {
                    null
                }
            }
            cartItems.clear()
            cartItems.addAll(fetchedProducts)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Your Cart") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
            )
        },
        content = { padding ->
            if (cartItems.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Your cart is empty", style = MaterialTheme.typography.bodySmall)
                }
            } else {
                val subtotal = cartItems.sumOf { it.price.toDoubleOrNull() ?: 0.0 }
                val subtotalCompared = cartItems.sumOf { it.comparedPrice.toDoubleOrNull() ?: 0.0 }
//                val tax = (subtotal * 0.05).toBigDecimal().setScale(2, java.math.RoundingMode.HALF_EVEN).toDouble()
//                val total = subtotal + tax
                val total = subtotal

                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                ) {
                    items(cartItems) { product ->
                        ProductCardHorizontal(
                            product = product,
                            sharedPreferences = sharedPreferences,
                            showRemoveButton = true,
                            onRemoveClick = {
                                removeFromCart(sharedPreferences, product.id)
                                cartItems.remove(product)
                            },
                            onClick = {
                                navController.navigate(
                                    MainActivity.ProductScreenRoute(
                                        product.id
                                    )
                                )
                            }
                        )
                    }

                    item {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Divider(thickness = 1.dp, color = Color.LightGray)
                            Spacer(modifier = Modifier.height(12.dp))

                            Text("Price Summary", fontWeight = FontWeight.SemiBold)
                            Spacer(modifier = Modifier.height(8.dp))

                            SummaryRow(
                                "Subtotal", "₹${"%.2f".format(subtotal)}",
                                bold = false,
                                isComparedPriceIncluded = true,
                                comparedPrice = "₹${"%.2f".format(subtotalCompared)}"
                            )
//                            SummaryRow("Tax (5%)", "₹${"%.2f".format(tax)}")
                            SummaryRow("Total", "₹${"%.2f".format(total)}", bold = true)

                            Spacer(modifier = Modifier.height(20.dp))

                            Button(
                                onClick = {
                                    navController.navigate(MainActivity.CheckoutScreenRoute)
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(50.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(
                                        0xFF388E3C
                                    )
                                )
                            ) {
                                Text("Checkout", color = Color.White)
                            }
                        }
                    }
                }
            }
        }
    )
}

@Composable
fun SummaryRow(
    label: String,
    value: String,
    bold: Boolean = false,
    isComparedPriceIncluded: Boolean = false,
    comparedPrice: String = ""
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = if (bold) MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold)
            else MaterialTheme.typography.bodySmall
        )

        if (isComparedPriceIncluded) {
            Row(
                verticalAlignment = Alignment.Bottom,
            ) {
                Text(
                    text = value,
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.Gray
                )
                Text(
                    text = comparedPrice,
                    textDecoration = TextDecoration.LineThrough,
                    style = MaterialTheme.typography.labelLarge,
                    modifier = Modifier.padding(start = 5.dp),
                    color = Color.Gray
                )
            }
        } else {
            Text(
                text = value,
                style = if (bold) MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold)
                else MaterialTheme.typography.bodySmall
            )
        }
    }
}


@Composable
fun CartItemCard(
    product: Product,
    onRemoveClick: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(10.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        border = CardDefaults.outlinedCardBorder(),
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            AsyncImage(
                model = product.images[0],
                contentDescription = "Product Image",
                modifier = Modifier
                    .padding(horizontal = 3.dp)
                    .width(LocalConfiguration.current.screenWidthDp.dp / 5f)
                    .height(LocalConfiguration.current.screenWidthDp.dp / 5f)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(text = product.name, style = MaterialTheme.typography.titleMedium)
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .padding(top = 3.dp)
                ) {

                    repeat(5) {
                        Icon(
                            imageVector = Icons.Filled.Star,
                            contentDescription = "Rating $it",
                            tint = Color(0xFFFFAA01),
                            modifier = Modifier.size(15.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(7.dp))

                Row(
                    verticalAlignment = Alignment.Bottom,
                ) {
                    Text(
                        text = "₹${product.price}",
                        style = MaterialTheme.typography.titleMedium,
                        color = Color.Gray
                    )
                    Text(
                        text = "₹${product.comparedPrice}",
                        textDecoration = TextDecoration.LineThrough,
                        style = MaterialTheme.typography.labelLarge,
                        modifier = Modifier.padding(start = 5.dp)
                    )
                }

            }
            IconButton(onClick = onRemoveClick) {
                Icon(Icons.Default.Delete, contentDescription = "Remove from cart")
            }
        }
    }
}

// Shared Preferences Helpers
fun addToCart(sharedPreferences: SharedPreferences, productId: String) {
    val cart =
        sharedPreferences.getStringSet("cart", mutableSetOf())?.toMutableSet() ?: mutableSetOf()
    cart.add(productId)
    sharedPreferences.edit().putStringSet("cart", cart).apply()
}

fun removeFromCart(sharedPreferences: SharedPreferences, productId: String) {
    val cart =
        sharedPreferences.getStringSet("cart", mutableSetOf())?.toMutableSet() ?: mutableSetOf()
    cart.remove(productId)
    sharedPreferences.edit().putStringSet("cart", cart).apply()
}

fun isProductInCart(sharedPreferences: SharedPreferences, productId: String): Boolean {
    val cart = sharedPreferences.getStringSet("cart", emptySet()) ?: emptySet()
    return cart.contains(productId)
}
