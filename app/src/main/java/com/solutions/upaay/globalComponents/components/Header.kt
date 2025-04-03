package com.solutions.upaay.globalComponents.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Sort
import androidx.compose.material.icons.outlined.Call
import androidx.compose.material.icons.outlined.CurrencyRupee
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.MoreVert
import androidx.compose.material.icons.outlined.ShoppingBag
import androidx.compose.material3.DrawerState
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.solutions.upaay.MainActivity
import com.solutions.upaay.utils.profile.ProfileUtils
import com.solutions.upaay.utils.translate.TranslatedText
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

val screenNames = mapOf(
    "Home" to "Explore Upaay",
    "Astrologers" to "Talk With Astrologers",
    "Shop" to "Upaay Store",
    "Me" to "Profile & Settings",
    "Explore" to "Explore Upaay"
)

@Composable
fun Header(
    navController: NavController,
    selectedItem: String,
    scope: CoroutineScope,
    drawerState: DrawerState
) {

//    val navBackStackEntry by navController.currentBackStackEntryAsState()
//    val currentRoute = navBackStackEntry?.destination?.route
//
//    val noHeaderRoutes = listOf(
//        MainActivity.NewOpeningsScreenRoute::class.qualifiedName,
//        MainActivity.CompaniesScreenRoute::class.qualifiedName,
//        MainActivity.SearchScreenRoute::class.qualifiedName,
//        MainActivity.InAppAlertsScreenRoute::class.qualifiedName,
//        MainActivity.AllAssessmentsScreenRoute::class.qualifiedName,
//        MainActivity.OpeningDetailScreenRoute::class.qualifiedName + "/{name}",
//        MainActivity.CompanyDetailScreenRoute::class.qualifiedName + "/{companyName}",
//        MainActivity.AssessmentDetailsScreenRoute::class.qualifiedName + "/{assessmentId}",
//        MainActivity.StartAssessmentScreenRoute::class.qualifiedName + "/{assessmentId}/{isContinue}"
//    )
//
//    val differentHeaderRoutes = listOf(
//        MainActivity.AuthSigninScreenRoute::class.qualifiedName,
//        MainActivity.AuthSignupScreenRoute::class.qualifiedName,
//        MainActivity.AuthSignupScreenRoute::class.qualifiedName,
//    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(if (selectedItem == "Home") Color(0xFFFAF7F3) else Color(0xFFFAF7F3))
//            .background(if(selectedItem == "Lobby") Color(0xFF9575CD) else Color(0xFFFAF7F3))
            .padding(10.dp, 10.dp, 15.dp, 10.dp)
    ) {

        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {

//        if (currentRoute in noHeaderRoutes) {
//            return;
//        }
//
//        if (currentRoute in differentHeaderRoutes) {
//
//            Icon(
//                imageVector = Icons.Filled.ArrowBack,
//                contentDescription = "Menu",
//                modifier = Modifier
//                    .size(37.dp)
//                    .padding(0.dp, 4.5.dp)
//                    .clickable {
//                        navController.popBackStack();
//                    }
//            )
//
//        } else {
            Icon(
                imageVector = Icons.Filled.Sort,
                tint = Color.Gray,
//                tint = if(selectedItem == "Lobby") Color(0XFFFAFAFA) else Color.Gray,
                contentDescription = "Menu",
                modifier = Modifier
                    .size(35.dp)
                    .clickable {
                        scope.launch {
                            drawerState.open()
                        }
                    }
            )

            Spacer(modifier = Modifier.width(10.dp))

            TranslatedText(
                text = screenNames[selectedItem].toString(),
                color = Color.Black,
//                color = if(selectedItem == "Lobby") Color(0XFFFAFAFA) else Color.Black,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(end = 2.dp)
            )

//            if (selectedItem == "Lobby" || selectedItem == "Astrologers") {
//                CurvedSearchBarDiv(modifier = Modifier.weight(1f), selectedItem) {
////                    navController.navigate(MainActivity.SearchScreenRoute)
//                }
//
//                if (selectedItem == "Lobby") {
//                    Icon(
//                        imageVector = Icons.Outlined.Notifications,
//                        tint = Color.Gray,
//                        contentDescription = "Alerts",
//                        modifier = Modifier
//                            .size(34.dp)
//                            .clickable {
////                            navController.navigate(MainActivity.InAppAlertsScreenRoute)
//                            }
//                            .padding(start = 10.dp)
//                    )
//                }
//            } else {
//                Spacer(modifier = Modifier.weight(1f))
//            }

            Spacer(modifier = Modifier.weight(1f))

            if (selectedItem == "Astrologers" || selectedItem == "Home") {
                if (ProfileUtils.userProfileState.value.balance != null) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
//                        .border(1.dp, Color.Black.copy(0.2f), RoundedCornerShape(8.dp))
                            .background(Color(0xFF4CAF50))
//                        .background(Color(0xFFE4E8EC))
                            .padding(5.dp, 4.dp)
                            .clickable {
                                navController.navigate(MainActivity.WalletScreenRoute)
                            }
                    ) {

                        Icon(
                            imageVector = Icons.Outlined.CurrencyRupee,
                            tint = Color.White,
                            contentDescription = "Wallet",
                            modifier = Modifier
                                .size(15.dp)
                        )

                        TranslatedText(
                            text = ProfileUtils.userProfileState.value.balance.toString(),
                            overflow = TextOverflow.Ellipsis,
                            maxLines = 1,
                            color = Color.White,
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier.padding(start = 2.dp, end = 4.dp)
                        )
                    }
                }

//                AsyncImage(
//                    model = "https://cdn.creazilla.com/icons/3235780/chat-alt-flat-icon-lg.png",
//                    contentDescription = "Chat",
//                    modifier = Modifier
//                        .padding(start = 10.dp, end = 5.dp)
//                        .size(31.dp)
//                        .clip(CircleShape)
//                        .clickable {
//                            navController.navigate(MainActivity.MyChatsScreenRoute)
//                        }
//                )
            } else if (selectedItem == "Shop") {

//                Icon(
//                    imageVector = Icons.Outlined.FavoriteBorder,
//                    tint = Color.Black,
//                    contentDescription = "Wishlist",
//                    modifier = Modifier
//                        .padding(start = 10.dp)
//                        .size(24.dp)
//                )

                Icon(
                    imageVector = Icons.Outlined.ShoppingBag,
                    tint = Color.Black,
                    contentDescription = "Cart",
                    modifier = Modifier
                        .padding(end = 5.dp)
                        .size(24.dp)
                        .clickable {
                            navController.navigate(MainActivity.CartScreenRoute)
                        }
                )
            }
//            else if (selectedItem == "Explore") {
//                Icon(
//                    imageVector = Icons.Outlined.MoreVert,
//                    tint = Color.Black,
//                    contentDescription = "More Options",
//                    modifier = Modifier
//                        .padding(start = 10.dp, end = 5.dp)
//                        .size(21.dp)
//                )
//            }
        }
    }
}

