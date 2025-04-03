package com.solutions.upaay.screens.policies

import com.solutions.upaay.R
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun AboutUsScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Text(
            text = "About Upaay",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "UPAAY is a virtual platform that brings astrology and puja services to your fingertips, enabling users to perform sacred rituals and connect with expert astrologers from the comfort of their homes. Designed to cater to your spiritual and astrological needs, UPAAY offers a range of features to help you stay connected to your faith and seek guidance for life’s challenges. You can purchase various spiritual items like Kavach, evil eye bracelet, house Protection Kavach, etc., etc. from the Upaay store page in the application.",
            style = MaterialTheme.typography.bodyLarge
        )

        Spacer(modifier = Modifier.height(32.dp))

        // 👤 Chief Marketing Officer Section
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Image(
                painter = painterResource(id = R.drawable.upaay_cfo),
                contentDescription = "Chief Marketing Officer",
                modifier = Modifier
                    .size(120.dp)
//                    .border(2.dp, Color.Gray, CircleShape)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text("Madhav Joshi", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Text("Chief Marketing Officer", style = MaterialTheme.typography.bodyMedium, color = Color.Gray)
        }

        Spacer(modifier = Modifier.height(32.dp))

        Text(
            text = "Features:",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "• Astrology Consultations: Connect with experienced astrologers for personalized predictions and insights into your future, career, relationships, and more.",
            style = MaterialTheme.typography.bodyLarge
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "How It Works",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Users can connect with astrologers for personalized consultations and guidance.",
            style = MaterialTheme.typography.bodyLarge
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "UPAAY combines tradition with technology, making it easier than ever to stay connected to your spiritual roots while embracing modern convenience. Whether you’re seeking divine blessings or astrological advice, UPAAY is your trusted companion for spiritual growth and well-being.",
            style = MaterialTheme.typography.bodyLarge
        )
    }
}

