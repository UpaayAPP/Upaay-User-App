package com.solutions.upaay.screens.auth.welcome

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.solutions.upaay.R
import com.solutions.upaay.globalComponents.components.AutoScrollingBanner
import com.solutions.upaay.utils.translate.TranslatedText

@Composable
fun AuthWelcomeScreen(onClick: () -> Unit) {

    val imageUrls = listOf(
        R.drawable.welcome_banner_1,
        R.drawable.welcome_banner_2,
        R.drawable.welcome_banner_3
    )

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxSize()
            .padding(vertical = 20.dp)

    ) {

//        Box(
//            modifier = Modifier
//                .fillMaxWidth()
//                .weight(1f)
//                .clip(RoundedCornerShape(15.dp))
//                .background(Color.DarkGray.copy(alpha = 0.4f))
//        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 20.dp)
                .weight(1f)
        ) {
            AutoScrollingBanner(
                modifier = Modifier.align(Alignment.Center),
                images = imageUrls,
                scrollInterval = 5000,
                boxHeight = LocalConfiguration.current.screenHeightDp / 2.5f,
                imageContentScale = ContentScale.Fit,
                imageStartPadding = 10
            )
        }

        TranslatedText(
            text = "Connect with India's Top Astrologers On The Upaay App",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(20.dp, 25.dp, 20.dp, 10.dp).align(Alignment.CenterHorizontally)
        )

        Pointer(
            "10 Thousand+ Astrology Specialist Gurus",
            "Practicing assessments before real interviews improve your chances by 90%"
        )

        Pointer(
            "Daily Horoscopes, To-Dos, Videos and Posts",
            "Find knowledgable & shareable videos and posts by authentic astrologers"
        )

        Pointer(
            "Trusted by 100,000+ people in India",
            "Improve your skills with our professional assessments"
        )


//        Spacer(modifier = Modifier.weight(1f))

        Button(
            onClick = {
                onClick()
            },
            colors = ButtonDefaults.buttonColors(
//                containerColor = Color(0xFF5324FD),
                containerColor = Color(0xFF2A7BF5),
                contentColor = Color.White
            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp, 20.dp, 20.dp, 10.dp)
        ) {

            TranslatedText(
                text = "Get Started",
                style = MaterialTheme.typography.titleSmall,
                modifier = Modifier.padding(vertical = 8.dp)
            )
        }


    }
}

@Composable
fun Pointer(heading: String, description: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(20.dp, 10.dp, 20.dp, 5.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Default.Star,
            contentDescription = "Point",
            tint = Color(0xFFFFB907),
            modifier = Modifier
                .size(23.dp)
                .padding(2.dp)
        )

        Spacer(modifier = Modifier.width(5.dp))

        TranslatedText(
            text = heading,
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Normal,
        )

    }
}












