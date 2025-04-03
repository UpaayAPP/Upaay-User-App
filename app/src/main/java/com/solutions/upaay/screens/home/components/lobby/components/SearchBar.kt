package com.solutions.upaay.screens.home.components.lobby.components

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.solutions.upaay.utils.translate.TranslatedText

@Composable
fun SearchBarDiv(modifier: Modifier = Modifier, trigger: () -> Unit) {

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .padding(start = 15.dp, end = 15.dp, top = 10.dp)
            .border(1.dp, Color(0xFFC19A6B), RoundedCornerShape(40.dp))
            .clip(RoundedCornerShape(40.dp))
            .fillMaxWidth()
            .height(38.dp)
            .clickable {
                trigger()
            }
            .padding(horizontal = 16.dp)
    ) {
        // Icon for the search bar
        Icon(
            imageVector = Icons.Default.Search,
            contentDescription = "Search Icon",
            tint = Color(0xFFC19A6B),
            modifier = Modifier.size(24.dp)
        )

        TranslatedText(
            text = "Search astrologers, horoscopes and more...",
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.Light,
            color = Color(0xFFC19A6B),
            modifier = Modifier
                .padding(start = 6.dp),
        )
    }

}
