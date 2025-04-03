package com.solutions.upaay.screens.home.components.lobby.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.solutions.upaay.R
import com.solutions.upaay.globalComponents.components.AutoScrollingBanner

@Composable
fun SlidingBanners(modifier: Modifier = Modifier) {
//    AsyncImage(
//        model = "https://www.techugo.com/blog/wp-content/uploads/2023/05/Best-Vedic-Astrology-Apps-A-Comprehensive-Guide-on-Building-The-Cosmic-Connection.jpg",
//        contentDescription = "Banner1",
//        modifier = Modifier
//            .padding(10.dp, 5.dp)
//            .fillMaxWidth()
//            .clip(RoundedCornerShape(5.dp))
//    )
    val imageUrls = listOf(
        R.drawable.lobby_banner_1,
        R.drawable.lobby_banner_2,
        R.drawable.lobby_banner_3,
    )
    AutoScrollingBanner(images = imageUrls, scrollInterval = 5000)
}
