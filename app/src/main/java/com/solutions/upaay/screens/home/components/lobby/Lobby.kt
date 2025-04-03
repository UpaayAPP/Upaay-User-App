package com.solutions.upaay.screens.home.components.lobby

import android.content.SharedPreferences
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.solutions.upaay.MainActivity
import com.solutions.upaay.screens.home.components.lobby.components.Categories
import com.solutions.upaay.screens.home.components.lobby.components.SearchBarDiv
import com.solutions.upaay.screens.home.components.lobby.components.SlidingBanners
import com.solutions.upaay.screens.home.components.lobby.components.TopAstrologers
import com.solutions.upaay.screens.home.components.lobby.components.TopShopItems

data class AstrologyTopic(val name: String, val imageUrl: String)

val astrologyTopics = listOf(
    AstrologyTopic("Love", "https://media.istockphoto.com/id/629494452/photo/love-concept.jpg?s=612x612&w=0&k=20&c=iu5Z7uHczSaYtPrH4mIVmigYEWbCuPAWU23hfWYub0M="),
    AstrologyTopic("Career", "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcThqdN-yAt9ue9tVoV0gknlWbIaR6GvwK2Yvw&s"),
    AstrologyTopic("Health", "https://www.indastro.com/img/upload/1533895343Medical-Astrolog.jpg"),
    AstrologyTopic("Finance", "https://5.imimg.com/data5/WU/HA/GLADMIN-26044452/finance-astrology.png"),
    AstrologyTopic("Family", "https://mind.family/wp-content/uploads/2024/06/Zodiac-Signs-That-Enjoy-Family-Time-More-Than-Others.jpg")
)

@Composable
fun Lobby(
    navController: NavController,
    sharedPreferences: SharedPreferences,
    changeSelectedItem: (newValue: String) -> Unit
) {
//    AsyncImage(
//        model = "https://www.drikpanchang.com/images/icon/deepak/deepam.gif.pagespeed.ce.H0RitBz0ww.gif",
//        contentDescription = "Profile",
//        modifier = Modifier
//            .size(57.dp)
//            .clip(CircleShape)
//    )

    LazyColumn(
        modifier = Modifier
            .background(
                Color(0xFFFAF7F3)
//                brush = Brush.verticalGradient(
//                    colors = listOf(
//                        Color(0xFFC19A6B), // Soft Tawny Brown
//                        Color(0xFFFFE4CC)  // Peachy Light Orange
////                        Color(0xFF9575CD), // Lavender Mist
////                        Color(0xFFFFC107)  // Golden Yellow
//                    ),
//                    startY = 0f,
//                    endY = Float.POSITIVE_INFINITY
//                )
            )
//            .background(Color(0xFFF8F1E4))
            .fillMaxSize()
    ) {
        item {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                SearchBarDiv {
                    navController.navigate(MainActivity.SearchScreenRoute)
                }
                Categories(astrologyTopics, navController)
                SlidingBanners()
//                TodaysHoroscope(panchangViewModel)

                // Commenting this for play store
//                TopReelsToday(
//                    "Top Reels Today",
//                    "Take a look at what's trending on the Upaay App Today!",
//                    true
//                )
                TopAstrologers(navController = navController)
                TopShopItems(navController = navController, sharedPreferences = sharedPreferences, changeSelectedItem)
                Spacer(modifier = Modifier.height(100.dp))
            }
        }
    }
}


