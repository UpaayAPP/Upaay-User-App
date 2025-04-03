package com.solutions.upaay.screens.call.audio_call


import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.solutions.upaay.MainActivity
import com.solutions.upaay.utils.translate.TranslatedText

@Composable
fun OngoingCallBanner(
    navController: NavController,
    callData: Map<String, Any>,
    onDismiss: () -> Unit
) {
    val astrologerName = callData["astrologerName"] as? String ?: "In Call"

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        tonalElevation = 4.dp,
        color = Color(0xFF1565C0),
        shape = RoundedCornerShape(8.dp)
    ) {
        Row(
            modifier = Modifier
                .clickable {
                    val callId = callData["callId"] as? String ?: return@clickable
                    val userId = callData["userId"] as? String ?: return@clickable
                    val astrologerId = callData["astrologerId"] as? String ?: return@clickable
                    navController.navigate(MainActivity.AudioCallScreenRoute(userId, astrologerId, callId))
                }
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                TranslatedText("In Call with $astrologerName", color = Color.White, fontWeight = FontWeight.Bold)
                TranslatedText("Tap to return to call", color = Color.White.copy(alpha = 0.8f))
            }

            TextButton(onClick = onDismiss) {
                TranslatedText("End", color = Color.White)
            }
        }
    }
}
