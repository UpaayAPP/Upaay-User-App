package com.solutions.upaay.screens.call.audio_call

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.google.firebase.firestore.FirebaseFirestore
import com.solutions.upaay.MainActivity
import com.solutions.upaay.utils.translate.TranslatedText

@Composable
fun OutgoingCallBanner(
    navController: NavController,
    callData: Map<String, Any>,
    onDismiss: () -> Unit
) {
    val callId = callData["callId"] as? String ?: return
    val userId = callData["userId"] as? String ?: return
    val astrologerId = callData["astrologerId"] as? String ?: return

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 8.dp),
        tonalElevation = 4.dp,
        color = Color(0xFF2E7D32),
        shape = RoundedCornerShape(10.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable {
                    navController.navigate(
                        MainActivity.AudioCallScreenRoute(userId, astrologerId, callId)
                    )
                }
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                TranslatedText(
                    text = "Calling, tap to open",
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(2.dp))
                TranslatedText(
                    text = "Connecting...",
                    color = Color.White.copy(alpha = 0.85f),
                    fontSize = 13.sp
                )
            }

            TextButton(onClick = {
                val firestore = FirebaseFirestore.getInstance()

                // Update all references to end call
                val updates = listOf(
                    firestore.collection("calls").document(callId),
                    firestore.collection("astrologers").document(astrologerId)
                        .collection("audiocalls").document(callId),
                    firestore.collection("users").document(userId)
                        .collection("audiocalls").document(callId)
                )

                updates.forEach { ref ->
                    ref.update("status", "ended")
                }

                onDismiss()
            }) {
                TranslatedText("Cancel", color = Color.White)
            }
        }
    }
}
