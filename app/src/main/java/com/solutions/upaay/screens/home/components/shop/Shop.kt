package com.solutions.upaay.screens.home.components.shop
// Core Compose

// Custom or Shared Pref Functions

// Navigation
import android.content.SharedPreferences
import android.widget.Toast
import androidx.annotation.Keep
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import com.solutions.upaay.MainActivity
import com.solutions.upaay.screens.home.components.shop.components.cards.ProductCard
import com.solutions.upaay.screens.home.components.shop.components.screens.ProductScreen
import com.solutions.upaay.screens.home.components.shop.components.screens.addToCart
import com.solutions.upaay.screens.home.components.shop.components.screens.isProductInCart
import kotlinx.coroutines.tasks.await

@Keep
data class Product(
    val id: String = "",
    val name: String = "",
    val description: String = "",
    val price: String = "",
    val comparedPrice: String = "",
    val images: List<String> = emptyList(),
    val inStock: Boolean = true
)

@Composable
fun Shop(navController: NavController, sharedPreferences: SharedPreferences) {
    val firestore = Firebase.firestore
    var products by remember { mutableStateOf<List<Product>>(emptyList()) }

    LaunchedEffect(Unit) {
        val snapshot = firestore.collection("products").get().await()
        products = snapshot.documents.mapNotNull { doc ->
            doc.toObject(Product::class.java)?.copy(id = doc.id)
        }
    }

    if (products.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }

    Column {


        Spacer(modifier = Modifier.height(12.dp))

        Button(
            onClick = {
                navController.navigate(MainActivity.MyOrdersScreenRoute)
            },
            modifier = Modifier
                .fillMaxWidth().padding(horizontal = 10.dp)
                .border(
                    width = 1.dp,
                    color = Color(0xFFA24C13),
                    shape = MaterialTheme.shapes.medium
                ),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.Transparent,
                contentColor = Color(0xFFA24C13),
            ),
        ) {
            Text("My Orders", color = Color(0xFFA24C13))
        }

        if (products.size == 1) {
            ProductScreen(
                navController = navController,
                sharedPreferences = sharedPreferences,
                productFromCaller = products[0]
            )
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(vertical = 10.dp)
            ) {
                items(products) { product ->
                    ProductCardHorizontal(
                        product = product,
                        onClick = {
                            navController.navigate(MainActivity.ProductScreenRoute(product.id))
                        },
                        sharedPreferences = sharedPreferences
                    )
                }
            }
        }
    }
}

@Composable
fun ProductCardHorizontal(
    product: Product,
    navController: NavController? = null,
    sharedPreferences: SharedPreferences,
    showAddToCart: Boolean = false,
    showRemoveButton: Boolean = false,
    onRemoveClick: (() -> Unit)? = null,
    onClick: (() -> Unit)? = null
) {
    val context = LocalContext.current
    val isInStock = product.inStock
    val isInCart = remember { isProductInCart(sharedPreferences, product.id) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 8.dp)
            .clickable {
                onClick?.invoke()
                    ?: navController?.navigate(MainActivity.ProductScreenRoute(product.id))
            },
        shape = RoundedCornerShape(10.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(modifier = Modifier.padding(10.dp)) {
            AsyncImage(
                model = product.images.firstOrNull(),
                contentDescription = "Product Image",
                modifier = Modifier
                    .size(100.dp)
                    .clip(RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = product.name,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 2
                )

                Spacer(modifier = Modifier.height(6.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "₹${product.price}",
                        color = Color(0xFF388E3C),
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "₹${product.comparedPrice}",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray,
                        textDecoration = TextDecoration.LineThrough
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

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

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = if (isInStock) "In Stock" else "Out of Stock",
                    color = if (isInStock) Color(0xFF2E7D32) else Color.Red,
                    style = MaterialTheme.typography.labelSmall
                )

                Spacer(modifier = Modifier.height(10.dp))

                if (showAddToCart && isInStock) {
                    Button(
                        onClick = {
                            if (!isInCart) {
                                addToCart(sharedPreferences, product.id)
                                navController?.navigate(MainActivity.CartScreenRoute)
                                Toast
                                    .makeText(context, "Added to cart", Toast.LENGTH_SHORT)
                                    .show()
                            } else {
                                navController?.navigate(MainActivity.CartScreenRoute)
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(
                                width = if (isInCart) 1.dp else 0.dp,
                                color =
                                if (isInCart) Color(0xFFA24C13) else Color.Transparent,
                                shape = MaterialTheme.shapes.medium
                            ),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isInCart) Color.White else Color(
                                0xFFA24C13
                            ),
                            contentColor = if (isInCart) Color.Black else Color.White
                        ),
                    ) {
                        Text(
                            text = if (isInCart) "View Cart" else "Add to Cart",
                            color = Color.White
                        )
                    }
                }

                if (showRemoveButton && onRemoveClick != null) {
                    OutlinedButton(
                        onClick = { onRemoveClick() },
                        modifier = Modifier.fillMaxWidth(),
                        border = BorderStroke(1.dp, Color.Red),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.Red)
                    ) {
                        Text("Remove from Cart")
                    }
                }
            }
        }
    }
}


//val sampleProduct = Product(
//    id = "sampleProduct1",
//    name = "7 chakra bracelet",
//    price = 199.0,
//    comparedPrice = 399.0,
//    description = "",
//    imageUrl = "https://astrosevatalk.com/_next/image?url=https%3A%2F%2Fapi.astrosevatalk.com%2Fshop%2F1722666696766_AdobeStock_807956690.jpeg&w=640&q=75",
//)
//
//val sampleProduct2 = Product(
//    id = "sampleProduct2",
//    name = "Turmuric & Coconut",
//    price = 199.0,
//    comparedPrice = 399.0,
//    description = "",
//    imageUrl = "https://assets.ganeshaspeaks.com/wp-content/uploads/2014/08/planets_and_gems.webp",
//)
//
//val sampleProduct3 = Product(
//    id = "sampleProduct3",
//    name = "7 Mukhi Rudraksha",
//    price = 199.0,
//    comparedPrice = 399.0,
//    description = "",
//    imageUrl = "https://astropawan.com/wp-content/uploads/2024/06/1-1.png",
//)
//
//val sampleProduct4 = Product(
//    id = "sampleProduct4",
//    name = "Maha Mrityunjaya Yantra",
//    price = 199.0,
//    comparedPrice = 399.0,
//    description = "",
//    imageUrl = "https://astropawan.com/wp-content/uploads/2024/06/Special-Hot-4.png",
//)

//@Composable
//fun Shop(navController: NavController, sharedPreferences: SharedPreferences) {
//
//    ProductScreen(
//        navController = navController,
//        sharedPreferences = sharedPreferences,
//        productId = "sampleProduct"
//    )

//    LazyColumn(
//        modifier = Modifier
//            .fillMaxSize()
//            .background(Color(0xFFFAF7F3))
//    ) {
//        item {
//            Column {
//
//
//                AsyncImage(
//                    model = "https://mir-s3-cdn-cf.behance.net/project_modules/hd/d7dfad107187879.5fa16aecd773f.jpg",
//                    contentDescription = "Banner1",
//                    modifier = Modifier
//                        .padding(10.dp, 5.dp)
//                        .fillMaxWidth()
//                        .clip(RoundedCornerShape(5.dp))
//                )
//
//                ProductsListDiv(navController, sharedPreferences, "Best Sellers", sampleProduct)
//
//                AsyncImage(
//                    model = "https://static.vecteezy.com/system/resources/previews/004/299/806/non_2x/online-shopping-on-phone-buy-sell-business-digital-web-banner-application-money-advertising-payment-ecommerce-illustration-search-vector.jpg",
//                    contentDescription = "Banner2",
//                    modifier = Modifier
//                        .padding(10.dp, 20.dp)
//                        .fillMaxWidth()
//                        .clip(RoundedCornerShape(5.dp))
//                )
//
//                Categories()
//
//                ProductsListDiv(navController, sharedPreferences, "Black Magic Stuff", sampleProduct2)
//
//                ProductsListDiv(navController, sharedPreferences, "Aura Cleansing", sampleProduct3)
//
//                ProductsListDiv(navController, sharedPreferences, "Mantra Guides", sampleProduct4)
//
//                Spacer(modifier = Modifier.height(100.dp))
//            }
//        }
//    }
//}

@Composable
fun ProductsListDiv(
    navController: NavController,
    sharedPreferences: SharedPreferences,
    heading: String,
    products: Product,
    isInLobby: Boolean = false
) {

    if (!isInLobby) {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 20.dp, bottom = 10.dp, start = 20.dp, end = 20.dp)
        ) {
            Text(
                text = heading,
                style = MaterialTheme.typography.titleSmall
            )

            Text(
                text = "View All",
                style = MaterialTheme.typography.labelMedium,
                color = Color.Gray
            )
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(start = 5.dp, top = 10.dp)
    ) {
        ProductCard(
            navController = navController,
            sharedPreferences = sharedPreferences,
            product = products,
            isInLobby = isInLobby
        )
    }

//    LazyRow(
//        modifier = Modifier.padding(start = 5.dp, top = 10.dp)
//    ) {
//        items(1) {
//        }
//    }
}
//
//@Composable
//fun Categories(modifier: Modifier = Modifier) {
//    Column(
//        modifier = modifier.padding(start = 20.dp)
//    ) {
//        Text(
//            text = "Based on categories",
//            style = MaterialTheme.typography.titleSmall,
//        )
//
//        LazyRow(
//            modifier = Modifier.padding(top = 20.dp, bottom = 20.dp),
//            horizontalArrangement = Arrangement.spacedBy(10.dp)
//        ) {
//            items(6) {
//                Box(
//                    modifier = Modifier
//                        .width(LocalConfiguration.current.screenWidthDp.dp / 4.4f)
//                        .height(LocalConfiguration.current.screenWidthDp.dp / 4.4f)
//                        .clip(CircleShape)
//                        .background(
//                            Color(0xFFF1EBD6)
//                        )
//                )
//            }
//        }
//    }
//}
