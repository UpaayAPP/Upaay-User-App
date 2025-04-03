package com.solutions.upaay.screens.home.components.lobby.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.Alignment
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.solutions.upaay.MainActivity
import com.solutions.upaay.screens.home.components.lobby.AstrologyTopic
import com.solutions.upaay.utils.translate.TranslatedText

@Composable
fun Categories(topics: List<AstrologyTopic>, navController: NavController) {
    val boxSize = LocalConfiguration.current.screenWidthDp.dp / 6.5f

    LazyRow(
        modifier = Modifier
            .padding(vertical = 20.dp)
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        items(topics.size) { index ->
            val topic = topics[index]
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.width(boxSize).clickable {
                    navController.navigate(MainActivity.AstrologersBySpecialityScreenRoute(topic.name))
                }
            ) {
                Box(
                    modifier = Modifier
                        .size(boxSize)
                        .clip(CircleShape)
                        .background(Color(0xFFF8EACE))
                ) {
                    AsyncImage(
                        model = topic.imageUrl,
                        contentDescription = topic.name,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.clip(CircleShape)
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                TranslatedText(
                    text = topic.name,
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}