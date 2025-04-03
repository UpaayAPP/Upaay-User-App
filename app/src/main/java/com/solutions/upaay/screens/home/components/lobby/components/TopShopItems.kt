package com.solutions.upaay.screens.home.components.lobby.components

import android.content.SharedPreferences
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.solutions.upaay.screens.home.components.shop.Product
import com.solutions.upaay.screens.home.components.shop.ProductsListDiv
import com.solutions.upaay.utils.translate.TranslatedText


@Composable
fun TopShopItems(
    navController: NavController,
    sharedPreferences: SharedPreferences,
    changeSelectedItem: (newValue: String) -> Unit
) {
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 27.dp, bottom = 7.dp, start = 10.dp, end = 20.dp)
    ) {
        TranslatedText(
            text = "Solutions to your problems",
            style = MaterialTheme.typography.titleSmall
        )

        TranslatedText(
            text = "View",
            style = MaterialTheme.typography.labelMedium,
            color = Color.DarkGray,
            modifier = Modifier.clickable {
                changeSelectedItem("Shop")
            }
        )
    }

    TranslatedText(
        text = "Discover Upaay's handcrafted - best quality items",
        style = MaterialTheme.typography.labelMedium,
        color = Color.Black.copy(0.7f),
        textAlign = TextAlign.Start,
        modifier = Modifier.fillMaxWidth().padding(start = 10.dp)
    )

//    ProductsListDiv(navController, sharedPreferences, "Best Sellers", sampleProduct, isInLobby = true)
}