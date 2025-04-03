package com.solutions.upaay.screens.home.components.lobby.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.solutions.upaay.utils.translate.TranslatedText

@Composable
fun TopReelsToday(
    heading: String = "Today's Special",
    description: String = "Discover what to do and what not to do today",
    isInLobby: Boolean = false
) {

    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                top = if (isInLobby) 27.dp else 20.dp,
                bottom = if (isInLobby) 7.dp else 0.dp,
                start = if (isInLobby) 10.dp else 5.dp,
                end = if (isInLobby) 20.dp else 5.dp
            )
    ) {
        TranslatedText(
            text = heading,
            style = MaterialTheme.typography.titleSmall
        )

        TranslatedText(
            text = "View All",
            style = MaterialTheme.typography.labelMedium,
            color = Color.DarkGray
        )
    }

    TranslatedText(
        text = description,
        style = MaterialTheme.typography.labelMedium,
        color = Color.Black.copy(0.7f),
        textAlign = TextAlign.Start,
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = if (isInLobby) 10.dp else 5.dp)
    )

    LazyRow(
        modifier = Modifier.padding(start = if (isInLobby) 10.dp else 5.dp, top = if (isInLobby) 20.dp else 10.dp)
    ) {
        items(8) {
            Box(
                modifier = Modifier
                    .padding(end = 10.dp)
                    .width(120.dp)
                    .height(200.dp)
                    .clip(RoundedCornerShape(5.dp))
                    .background(
                        if (isInLobby) Color(
                            0xFFF8F1E4
                        ) else Color.White
                    )
            )
        }
    }

}