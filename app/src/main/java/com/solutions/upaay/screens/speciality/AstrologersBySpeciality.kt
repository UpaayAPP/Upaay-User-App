package com.solutions.upaay.screens.speciality

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.firebase.firestore.FirebaseFirestore
import com.solutions.upaay.screens.home.components.astrologer.components.cards.Astrologer
import com.solutions.upaay.screens.home.components.astrologer.components.cards.AstrologerCard

@Composable
fun AstrologerBySpeciality(
    selectedTopic: String,
    navController: NavController,
    modifier: Modifier = Modifier,
    selectAstrologerForCall: (Astrologer) -> Unit
) {
    val db = FirebaseFirestore.getInstance()
    var astrologers by remember { mutableStateOf<List<Astrologer>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(selectedTopic) {
        isLoading = true
        db.collection("astrologers")
            .whereArrayContains("speciality", selectedTopic)
            .get()
            .addOnSuccessListener { snapshot ->
                astrologers = snapshot.documents.mapNotNull { it.toObject(Astrologer::class.java) }
                isLoading = false
            }
            .addOnFailureListener {
                isLoading = false
            }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Astrologers Specialised in \"$selectedTopic\"",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 10.dp)
        )

        if (isLoading) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
        } else if (astrologers.isEmpty()) {
            Text(
                text = "No astrologers found for \"$selectedTopic\"",
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
        } else {
            LazyColumn {
                items(astrologers) { astrologer ->
                    AstrologerCard(
                        astrologer = astrologer,
                        navController = navController,
                        isForDetailScreen = false,
                        isForChatScreen = true,
                        triggerBottomSheet = {
                            selectAstrologerForCall(astrologer)
                        }
                    )
                }
            }
        }
    }
}

