package com.solutions.upaay.screens.home.components.shop.components.screens

import android.content.SharedPreferences
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Divider
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import com.solutions.upaay.MainActivity
import com.solutions.upaay.screens.home.components.astrologer.components.cards.AstrologerReviewCard
import com.solutions.upaay.screens.home.components.astrologer.components.screens.astrologerReviews
import com.solutions.upaay.screens.home.components.shop.Product
import com.solutions.upaay.utils.translate.TranslatedText
import kotlinx.coroutines.tasks.await

val sampleDescription = """About the product -

A Rudraksha hand band is more than just a piece of jewelry; it is a sacred adornment that carries deep spiritual significance and numerous benefits. Crafted from the holy seeds of the Rudraksha tree, this hand band combines the timeless charm of natural elements with the profound energy of spirituality. Whether you are seeking inner peace, protection, or a meaningful accessory, the Rudraksha hand band is a perfect choice.

Key Features and Benefits of the Rudraksha Hand Band:
Spiritual Significance:

Rudraksha beads are considered sacred in Hinduism and are closely associated with Lord Shiva.
They symbolize purity, devotion, and a connection to divine energy.
Holistic Benefits:

Helps in balancing energy levels and promoting a sense of calm and tranquility.
Known to reduce stress and anxiety, offering a grounding effect on the mind and body.
Health Advantages:

Believed to regulate blood pressure and enhance overall well-being.
Improves concentration, focus, and mental clarity.
Protection:

Acts as a shield against negative energies and harmful influences.
Provides spiritual protection by creating a protective aura around the wearer.
Material and Design:

Made from natural, genuine Rudraksha beads sourced from the Himalayan region.
Strung together with durable, high-quality elastic or thread to ensure long-lasting wear.
Available in various sizes and styles, from simple designs to more intricate patterns with silver or gold embellishments.
Symbol of Simplicity and Elegance:

A versatile accessory that can be worn with both traditional and modern outfits.
Adds a touch of spirituality and style to your appearance.
Ideal for Daily Wear:

Lightweight and comfortable, making it suitable for everyday use.
Easy to maintain and clean, requiring only occasional wiping with a soft cloth.
Perfect Gift:

An ideal gift for loved ones to express care, love, and blessings.
Suitable for all occasions, including birthdays, anniversaries, or festivals.
Why Choose a Rudraksha Hand Band?
Eco-Friendly and Sustainable: Made from natural seeds, promoting environmental consciousness.
Cultural Heritage: A timeless symbol of Indian culture and spiritual tradition.
Unisex Appeal: Designed to suit men and women of all ages.
Customizable Options: Choose from different bead sizes, band lengths, and additional embellishments.
The Rudraksha hand band is not just an accessory but a spiritual companion that enhances your overall life journey. By wearing it, you embrace positivity, inner strength, and a harmonious balance between your mind, body, and soul. Whether for yourself or as a thoughtful gift, the Rudraksha hand band is a timeless treasure that embodies both beauty and divinity."""

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ProductScreen(
    navController: NavController,
    sharedPreferences: SharedPreferences,
    productId: String? = null,
    productFromCaller: Product? = null
) {
    val context = LocalContext.current
    val firestore = Firebase.firestore

    var product by remember { mutableStateOf<Product?>(productFromCaller) }
    val isItemInCart = remember(product) { product?.let { isProductInCart(sharedPreferences, it.id) } ?: false }

    LaunchedEffect(productFromCaller, productId) {
        if (productFromCaller == null && productId != null) {
            try {
                val doc = firestore.collection("products").document(productId).get().await()
                product = doc.toObject(Product::class.java)?.copy(id = doc.id)
            } catch (e: Exception) {
                Toast.makeText(context, "Failed to load product", Toast.LENGTH_SHORT).show()
            }
        }
    }

    product?.let { prod ->
        Scaffold {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(it)
            ) {
                item {
                    // Images Carousel
                    LazyRow(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp, 0.dp, 16.dp, 10.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(prod.images) { imageUrl ->
                            AsyncImage(
                                model = imageUrl,
                                contentDescription = "Product Image",
                                modifier = Modifier
                                    .width(LocalConfiguration.current.screenWidthDp.dp / 1.1f)
                                    .aspectRatio(1f)
                                    .clip(RoundedCornerShape(12.dp)),
                                contentScale = ContentScale.Crop
                            )
                        }
                    }

                    // Thumbnails
                    LazyRow(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(prod.images) { imageUrl ->
                            AsyncImage(
                                model = imageUrl,
                                contentDescription = "Thumbnail",
                                modifier = Modifier
                                    .size(60.dp)
                                    .clip(RoundedCornerShape(6.dp)),
                                contentScale = ContentScale.Crop
                            )
                        }
                    }

                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = prod.name,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(vertical = 8.dp)
                        )

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            repeat(5) {
                                Icon(
                                    imageVector = Icons.Filled.Star,
                                    contentDescription = "Rating $it",
                                    tint = Color(0xFFFFAA01),
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "₹${prod.price}",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF388E3C)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "₹${prod.comparedPrice}",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.Gray,
                                textDecoration = TextDecoration.LineThrough
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        when {
                            !prod.inStock -> {
                                Button(
                                    onClick = {},
                                    modifier = Modifier.fillMaxWidth(),
                                    enabled = false,
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = MaterialTheme.colorScheme.secondary.copy(alpha = 0.5f),
                                        disabledContentColor = Color.White
                                    )
                                ) {
                                    Text("Out Of Stock")
                                }
                            }

                            isItemInCart -> {
                                Button(
                                    onClick = {
                                        navController.navigate(MainActivity.CartScreenRoute)
                                    },
                                    modifier = Modifier.fillMaxWidth().border(
                                        width = 1.dp,
                                        color = Color(0xFFA24C13),
                                        shape = MaterialTheme.shapes.medium
                                    ),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = Color.Transparent,
                                        contentColor = Color(0xFFA24C13),
                                    ),
                                ) {
                                    Text("View Cart", color = Color(0xFFA24C13))
                                }
                            }

                            else -> {
                                Button(
                                    onClick = {
                                        addToCart(sharedPreferences, prod.id)
                                        navController.navigate(MainActivity.CartScreenRoute)
                                        Toast.makeText(context, "Added to cart!", Toast.LENGTH_SHORT).show()
                                    },
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = Color(0xFFA24C13),
                                        contentColor = Color.White,
                                    ),
                                ) {
                                    Text("Add to Cart", color = Color.White)
                                }
                            }
                        }


                        Spacer(modifier = Modifier.height(18.dp))

                        Text(
                            text = prod.description,
                            style = MaterialTheme.typography.bodyMedium,
                            lineHeight = 20.sp
                        )

                        Spacer(modifier = Modifier.height(24.dp))

                        Divider(color = Color(0xFFE0E0E0), thickness = 1.dp)

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = "Reviews about this product -",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(vertical = 8.dp)
                        )

                        astrologerReviews.forEach { astrologerReview ->
                            AstrologerReviewCard(navController, astrologerReview)
                        }

                        Spacer(modifier = Modifier.height(100.dp))
                    }
                }
            }
        }
    } ?: run {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
    }
}

// BottomBar Component
@Composable
fun BottomBar(onAddToCart: () -> Unit, onBuyNow: () -> Unit) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(60.dp),
        color = Color.White,
//        color = MaterialTheme.colorScheme.primary,
//        elevation = 8.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth(),
//                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Button(
                onClick = onAddToCart,
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
            ) {
               TranslatedText(text = "Add to Cart")
            }
            Spacer(modifier = Modifier.width(8.dp))
            Button(
                onClick = onBuyNow,
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF5722))
            ) {
               TranslatedText(text = "Buy Now")
            }
        }
    }
}
