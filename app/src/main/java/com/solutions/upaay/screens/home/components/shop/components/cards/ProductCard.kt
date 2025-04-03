package com.solutions.upaay.screens.home.components.shop.components.cards

import android.content.SharedPreferences
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.solutions.upaay.MainActivity
import com.solutions.upaay.screens.home.components.shop.Product
import com.solutions.upaay.screens.home.components.shop.components.screens.addToCart
import com.solutions.upaay.screens.home.components.shop.components.screens.isProductInCart

@Composable
fun ProductCard(
    modifier: Modifier = Modifier,
    navController: NavController,
    sharedPreferences: SharedPreferences,
    product: Product,
    isInLobby : Boolean = false
) {
    val isItemInCart = remember { isProductInCart(sharedPreferences, product.id) }
    Card(
        shape = RoundedCornerShape(11.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(
            containerColor = if(isInLobby) Color(0xFFF8F1E4) else Color.White
        ),
        modifier = modifier
            .width(LocalConfiguration.current.screenWidthDp.dp / 2.5f)
            .padding(5.dp)
            .clickable {
                navController.navigate(MainActivity.ProductScreenRoute(product.id))
            },
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .padding(10.dp)
        ) {

            AsyncImage(
                model = product.images[0],
                contentDescription = "Product Image",
                modifier = Modifier
                    .padding(horizontal = 3.dp)
                    .aspectRatio(ratio = 1f)
                    .width(LocalConfiguration.current.screenWidthDp.dp / 2.5f)
//                    .width(LocalConfiguration.current.screenWidthDp.dp / 2.5f)

            )

            Text(
                text = product.name,
                overflow = TextOverflow.Ellipsis,
                maxLines = 1,
                style = MaterialTheme.typography.labelLarge,
                modifier = Modifier
                    .padding(top = 10.dp)
            )

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .padding(top = 5.dp)
            ) {

                repeat(5) {
                    Icon(
                        imageVector = Icons.Filled.Star,
                        contentDescription = "Rating $it",
                        tint = Color(0xFFFFAA01),
                        modifier = Modifier.size(16.dp)
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

            Spacer(modifier = Modifier.height(10.dp))

//            Button(
//                onClick = {
//                    if (!isItemInCart) {
//                        addToCart(sharedPreferences, product.id)
//                        navController.navigate(MainActivity.CartScreenRoute)
//                    } else {
//                        navController.navigate(MainActivity.ProductScreenRoute(product.id))
//                    }
//                },
//                shape = RoundedCornerShape(8.dp),
//                colors = ButtonDefaults.outlinedButtonColors(
//                    containerColor = Color.Transparent
//                ),
//                border = BorderStroke(1.dp,  if (isItemInCart) Color(0xFF5E5E5E) else Color(0xFFE09500)),
//                modifier = Modifier
//                    .height(32.dp)
//                    .padding(start = 8.dp)
//            ) {
//                Text(
//                    text = if (isItemInCart) "In Cart" else "Add To Cart",
//                    fontSize = 13.sp,
//                    fontWeight = FontWeight.Bold,
//                    color = if (isItemInCart) Color(0xFF5E5E5E) else Color(0xFFE09500)
//                )
//            }
        }
    }
}