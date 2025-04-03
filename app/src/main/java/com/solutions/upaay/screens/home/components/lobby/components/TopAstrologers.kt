package com.solutions.upaay.screens.home.components.lobby.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.solutions.upaay.screens.home.components.astrologer.AstrologerRepository
import com.solutions.upaay.screens.home.components.astrologer.components.cards.Astrologer
import com.solutions.upaay.screens.home.components.astrologer.components.cards.AstrologerCardSmaller
import com.solutions.upaay.screens.home.handleChangeSelectedItem
import com.solutions.upaay.utils.translate.TranslatedText

//val astrologer = Astrologer(
//    uid = "BQKMoOgdnTaMiGYEA1WJXnbWMBg1",
//    name = "Astrologer Name",
//    speciality = "Tarot, Vedic, Life Coach",
//    languages = listOf("Hindi, English, Swedish"),
//    experience = "10+ years",
//    ratePerMessage = 23.0,
//    audioRatePerMinute = 49.0,
//    videoRatePerMinute = 55.0,
//    isOnline = true,
//    waitTime = 7,
//    totalOrders = 123
//)

@Composable
fun TopAstrologers( navController: NavController, showViewAllButton : Boolean = true) {

    val astrologers = AstrologerRepository.astrologers.take(5)

    Row(
        horizontalArrangement = if(showViewAllButton) Arrangement.SpaceBetween else Arrangement.Start,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 27.dp, bottom = 7.dp, start = 10.dp, end = 20.dp)
    ) {
        TranslatedText(
            text = "Top Astrologers",
            style = MaterialTheme.typography.titleSmall
        )

        if(showViewAllButton) {
            TranslatedText(
                text = "View All",
                style = MaterialTheme.typography.labelMedium,
                color = Color.DarkGray,
                modifier = Modifier.clickable {
                    handleChangeSelectedItem("Astrologers")
//                navController.popBackStack()
                }
            )
        }
    }

    TranslatedText(
        text = "Find the best rated astrologers with highest ratings",
        style = MaterialTheme.typography.labelMedium,
        color = Color.Black.copy(0.7f),
        textAlign = TextAlign.Start,
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 10.dp)
    )

    LazyRow(
        modifier = Modifier.padding(start = 5.dp, top = 10.dp).fillMaxWidth(),
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.CenterVertically
    ) {
        items(astrologers) { astrologer ->
            AstrologerCardSmaller(
                navController = navController,
                astrologer = astrologer,
                isForChatScreen = true,
                isInLobby = true
            )
        }
    }
}