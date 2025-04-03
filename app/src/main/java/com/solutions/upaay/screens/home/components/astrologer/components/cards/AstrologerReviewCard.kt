package com.solutions.upaay.screens.home.components.astrologer.components.cards

import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.solutions.upaay.utils.translate.TranslatedText

data class AstrologerReview(
    val name: String,
    val rating: Float,
    val review: String,
    val profileImageUrl: String,
)

@Composable
fun AstrologerReviewCard(
    navController: NavController,
    review: AstrologerReview
) {
    Card(
        shape = RoundedCornerShape(10.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        modifier = Modifier
            .fillMaxWidth()
            .padding(5.dp,)
    ) {

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .padding(15.dp, 15.dp, 15.dp, 5.dp)
            ) {
            AsyncImage(
                model = review.profileImageUrl,
                contentDescription = "Profile",
                modifier = Modifier
                    .size(27.dp)
                    .clip(CircleShape)
            )
            TranslatedText(
                text = review.name,
                style = MaterialTheme.typography.labelSmall,
                modifier = Modifier.padding(start = 8.dp)
            )
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .padding(15.dp, 5.dp)
        ) {

            for (i in 1..5) {
                Icon(
                    imageVector = if (i <= review.rating) Icons.Filled.Star else Icons.Outlined.Star,
                    contentDescription = if (i <= review.rating) "Filled Star" else "Outlined Star",
                    tint = if (i <= review.rating) Color(0xFFFFAA01) else Color.Gray, // Gold for filled, Gray for outlined
                    modifier = Modifier.size(17.dp)
                )
            }
        }

        TranslatedText(
            text = review.review,
            style = MaterialTheme.typography.labelMedium,
            modifier = Modifier.padding(15.dp, 5.dp, 15.dp, 15.dp)
        )
    }

}