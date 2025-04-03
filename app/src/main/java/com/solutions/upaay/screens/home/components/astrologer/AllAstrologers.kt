package com.solutions.upaay.screens.home.components.astrologer

import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.solutions.upaay.MainActivity
import com.solutions.upaay.screens.home.components.astrologer.components.cards.Astrologer
import com.solutions.upaay.screens.home.components.astrologer.components.cards.AstrologerCard
import com.solutions.upaay.utils.database.retrieve.fetchAstrologers
import com.solutions.upaay.utils.translate.TranslatedText

// Globally storing fetched astrologers
object AstrologerRepository {
    var astrologers: List<Astrologer> by mutableStateOf(emptyList())
}

@Composable
fun AllAstrologers(
    navController: NavController,
    isChatScreen: Boolean,
    selectAstrologerForCall: (astrologer: Astrologer) -> Unit
) {
//    val astrologersState = produceState<List<Astrologer>?>(initialValue = null) {
//        fetchAstrologers(
//            onResult = { value = it },
//            onError = { Log.e("Firestore", "Error fetching astrologers", it) }
//        )
//    }
//    val astrologers = astrologersState.value ?: emptyList()

    LaunchedEffect(Unit) {
        if (AstrologerRepository.astrologers.isEmpty()) {
            fetchAstrologers(
                existingUids = AstrologerRepository.astrologers.map { it.uid },
                onResult = { newAstrologers ->
                    AstrologerRepository.astrologers += newAstrologers // ✅ Store globally
                },
                onError = { Log.e("Firestore", "Error fetching astrologers", it) }
            )
        }
    }

    LazyColumn(
        contentPadding = PaddingValues(3.dp),
        verticalArrangement = Arrangement.spacedBy(3.dp),
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
//            .background(Color(0xFFFAF7F3))
    ) {
        item {

            SuggestionsCategoriesRow(navController)

            Spacer(modifier = Modifier.height(10.dp))

            RecentChatAndCallButtonsRow(
                onChatsClick = { navController.navigate(MainActivity.MyChatsScreenRoute) },
                onCallsClick = { navController.navigate(MainActivity.MyRecentAudioCallsScreenRoute) }
            )

            Spacer(modifier = Modifier.height(10.dp))

//            AsyncImage(
//                model = "https://www.shutterstock.com/image-vector/astrological-banner-planet-saturn-on-600nw-2258978885.jpg",
//                contentDescription = "Banner2",
//                contentScale = ContentScale.Crop,
//                modifier = Modifier
//                    .padding(5.dp, 10.dp, 5.dp, 5.dp)
//                    .fillMaxWidth()
//                    .height(120.dp)
//                    .clip(RoundedCornerShape(5.dp))
//            )

            AstrologerRepository.astrologers.forEach { astrologer ->
                AstrologerCard(
                    navController = navController,
                    isForChatScreen = isChatScreen,
                    astrologer = astrologer
                ) {
                    selectAstrologerForCall(astrologer)
                }
            }

//            AsyncImage(
//                model = "https://static.vecteezy.com/system/resources/previews/004/299/806/non_2x/online-shopping-on-phone-buy-sell-business-digital-web-banner-application-money-advertising-payment-ecommerce-illustration-search-vector.jpg",
//                contentDescription = "Banner2",
//                contentScale = ContentScale.Crop,
//                modifier = Modifier
//                    .padding(5.dp, 10.dp, 5.dp, 5.dp)
//                    .fillMaxWidth()
//                    .height(120.dp)
//                    .clip(RoundedCornerShape(5.dp))
//            )

//            if (selectedAstrologer != null) {
//                CallAstrologerBottomSheet(
//                    navController = navController,
//                    astrologerId = selectedAstrologer!!.uid,  // ✅ Pass correct ID
//                    astrologerName = selectedAstrologer!!.name,  // ✅ Pass correct Name
//                    onDismiss = { selectedAstrologer = null }
//                )
//            }

            Spacer(modifier = Modifier.height(100.dp))
        }
    }
}

//@Composable
//fun AllAstrologers(navController: NavController, isChatScreen: Boolean) {
//    LazyColumn(
//        contentPadding = PaddingValues(3.dp),
//        verticalArrangement = Arrangement.spacedBy(3.dp),
//        modifier = Modifier.background(Color(0xFFFAF7F3))
//    ) {
//        item {
//
//            var showBottomSheet by remember { mutableStateOf(false) }
//
//            SuggestionsCategoriesRow()
//
//            if (isChatScreen) {
//
//                AsyncImage(
//                    model = "https://www.shutterstock.com/image-vector/astrological-banner-planet-saturn-on-600nw-2258978885.jpg",
//                    contentDescription = "Banner2",
//                    contentScale = ContentScale.Crop,
//                    modifier = Modifier
//                        .padding(5.dp, 10.dp, 5.dp, 5.dp)
//                        .fillMaxWidth()
//                        .height(120.dp)
//                        .clip(RoundedCornerShape(5.dp))
//                )
//            }
//
//            AstrologerCard(
//                navController = navController,
//                isForChatScreen = isChatScreen,
//                name = "Astrologer 1",
//                experience = "${5 + 1} years",
//                isOnline = true,  // Example: Every other astrologer is offline
//                profileImageRes = R.drawable.ic_launcher_background,
//            ){
//                showBottomSheet = true
//            }
//
//            AstrologerCard(
//                navController = navController,
//                isForChatScreen = isChatScreen,
//                name = "Astrologer 2",
//                experience = "${5 + 2} years",
//                isOnline = false,  // Example: Every other astrologer is offline
//                profileImageRes = R.drawable.ic_launcher_background
//            ){
//                showBottomSheet = true
//            }
//
//            LazyRow(
//                modifier = Modifier.padding(start = 5.dp, top = 10.dp, bottom = 10.dp)
//            ) {
//                items(5) {
//                    AstrologerCardSmaller(
//                        navController = navController,
//                        isForChatScreen = isChatScreen,
//                        astrologer = astrologer,
//                        isInLobby = false
//                    ){
//                        showBottomSheet = true
//                    }
//                }
//            }
//
//            AstrologerCard(
//                navController = navController,
//                isForChatScreen = isChatScreen,
//                name = "Astrologer 3",
//                experience = "${5 + 3} years",
//                isOnline = true,  // Example: Every other astrologer is offline
//                profileImageRes = R.drawable.ic_launcher_background
//            ){
//                showBottomSheet = true
//            }
//
//            AstrologerCard(
//                navController = navController,
//                isForChatScreen = isChatScreen,
//                name = "Astrologer 4",
//                experience = "${5 + 4} years",
//                isOnline = false,  // Example: Every other astrologer is offline
//                profileImageRes = R.drawable.ic_launcher_background
//            ){
//                showBottomSheet = true
//            }
//
//            AstrologerCard(
//                navController = navController,
//                isForChatScreen = isChatScreen,
//                name = "Astrologer 5",
//                experience = "${5 + 5} years",
//                isOnline = true,  // Example: Every other astrologer is offline
//                profileImageRes = R.drawable.ic_launcher_background
//            ){
//                showBottomSheet = true
//            }
//
//            AsyncImage(
//                model = "https://static.vecteezy.com/system/resources/previews/004/299/806/non_2x/online-shopping-on-phone-buy-sell-business-digital-web-banner-application-money-advertising-payment-ecommerce-illustration-search-vector.jpg",
//                contentDescription = "Banner2",
//                contentScale = ContentScale.Crop,
//                modifier = Modifier
//                    .padding(5.dp, 10.dp, 5.dp, 5.dp)
//                    .fillMaxWidth()
//                    .height(120.dp)
//                    .clip(RoundedCornerShape(5.dp))
//            )
//
//            AstrologerCard(
//                navController = navController,
//                isForChatScreen = isChatScreen,
//                name = "Astrologer 6",
//                experience = "${5 + 6} years",
//                isOnline = true,  // Example: Every other astrologer is offline
//                profileImageRes = R.drawable.ic_launcher_background
//            ){
//                showBottomSheet = true
//            }
//
//            LazyRow(
//                modifier = Modifier.padding(start = 5.dp, top = 10.dp, bottom = 10.dp)
//            ) {
//                items(5) {
//                    AstrologerCardSmaller(
//                        navController = navController,
//                        isForChatScreen = isChatScreen,
//                        astrologer = astrologer,
//                        isInLobby = false
//                    ){
//                        showBottomSheet = true
//                    }
//                }
//            }
//
//            if (showBottomSheet) {
//                CallAstrologerBottomSheet(
//                    navController = navController,
//                    astrologerId = "BQKMoOgdnTaMiGYEA1WJXnbWMBg1",
//                    astrologerName = astrologer.name,
//                    onDismiss = { showBottomSheet = false }
//                )
//            }
//
//            Spacer(modifier = Modifier.height(100.dp))
//        }
//    }
//}

@Composable
fun RecentChatAndCallButtonsRow(
    onChatsClick: () -> Unit,
    onCallsClick: () -> Unit
) {
    val iconSizeChat = 19.dp
    val iconSizeCall = 18.dp

    val borderColor = Color.Black.copy(0.4f)
//    val borderColor = Color(0xFFA24C13)

    val chatIconUrl = "https://cdn-icons-png.flaticon.com/512/2462/2462719.png" // black chat icon
    val callIconUrl = "https://assets.streamlinehq.com/image/private/w_300,h_300,ar_1/f_auto/v1/icons/all-icons/phone-call-kq9z3lfm4welx5rbsdfd.png/phone-call-ygdahfdcompo15s9uregk.png?_a=DAJFJtWIZAAC"   // black call icon

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 10.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        OutlinedButton(
            onClick = onChatsClick,
            border = BorderStroke(1.dp, borderColor),
            shape = RoundedCornerShape(10.dp),
            colors = ButtonDefaults.outlinedButtonColors(
                containerColor = Color.Transparent,
                contentColor = borderColor
            ),
            modifier = Modifier
                .weight(1f)
                .height(38.dp)
        ) {
            AsyncImage(
                model = chatIconUrl,
                contentDescription = "Chats Icon",
                modifier = Modifier.padding(end = 8.dp).size(iconSizeChat)
            )
            Text(
                text = "Recent Chats",
                fontWeight = FontWeight.Medium,
                fontSize = 13.sp,
                color = Color.Black
            )
        }

        OutlinedButton(
            onClick = onCallsClick,
            border = BorderStroke(1.dp, borderColor),
            shape = RoundedCornerShape(10.dp),
            colors = ButtonDefaults.outlinedButtonColors(
                containerColor = Color.Transparent,
                contentColor = borderColor
            ),
            modifier = Modifier
                .weight(1f)
                .height(38.dp)
        ) {
            AsyncImage(
                model = callIconUrl,
                contentDescription = "Calls Icon",
                modifier = Modifier.padding(end = 10.dp).size(iconSizeCall)
            )
            Text(
                text = "Recent Calls",
                fontWeight = FontWeight.Medium,
                fontSize = 13.sp,
                color = Color.Black
            )
        }
    }
}




@Composable
fun SuggestionsCategoriesRow(navController: NavController) {
    val topics = listOf(
        "Love",
        "Career",
        "Health",
        "Finance",
        "Family",
        "Marriage",
        "Education",
        "Business",
        "Foreign Travel",
        "Legal Issues"
    )

    LazyRow(
        modifier = Modifier.padding(start = 10.dp, top = 5.dp)
    ) {
        items(topics) { topic ->
            Button(
                onClick = {
                    navController.navigate(MainActivity.AstrologersBySpecialityScreenRoute(topic))
                },
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.outlinedButtonColors(
                    containerColor = Color.Transparent,
                    contentColor = Color(0xFFC19A6B)
                ),
                border = BorderStroke(0.5.dp, Color(0xFFC19A6B)),
                modifier = Modifier
                    .height(32.dp)
                    .padding(end = 8.dp)
            ) {
                TranslatedText(
                    text = topic,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFA07747)
                )
            }
        }
    }
}
