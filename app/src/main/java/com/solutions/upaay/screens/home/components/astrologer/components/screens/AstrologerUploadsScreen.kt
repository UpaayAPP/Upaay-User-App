package com.solutions.upaay.screens.home.components.astrologer.components.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.solutions.upaay.MainActivity
import com.solutions.upaay.utils.translate.TranslatedText

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun AstrologerUploadsScreen(navController: NavController) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 5.dp)
    ) {
        item {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(5.dp, 10.dp)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Go back",
                        modifier = Modifier
                            .padding(end = 8.dp)
                            .size(22.dp)
                            .clickable {
                                navController.popBackStack()
                            }
                    )
                    TranslatedText(
                        text = "Pewdiepie's Profile",
                        fontWeight = FontWeight.W400,
                        style = MaterialTheme.typography.titleSmall,
                    )
                }

                AsyncImage(
                    model = "https://www.astrobharati.in/Upload/astrologers/profile_pic/Dr.Vinayak%20Bhat/1.Vinayak%20bhat.jpg",
                    contentDescription = "Profile",
                    modifier = Modifier
                        .padding(top = 10.dp)
                        .size(80.dp)
                        .clip(CircleShape)
                )

                Spacer(modifier = Modifier.height(7.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TranslatedText(
                        text = "Pewdiepie",
                        overflow = TextOverflow.Ellipsis,
                        maxLines = 1,
                        style = MaterialTheme.typography.titleSmall
                    )
                    AsyncImage(
                        model = "https://static.vecteezy.com/system/resources/thumbnails/017/350/123/small_2x/green-check-mark-icon-in-round-shape-design-png.png",
                        contentDescription = "Verified",
                        modifier = Modifier
                            .padding(start = 5.dp)
                            .size(20.dp)
                            .clip(CircleShape)
                    )

                }

                Spacer(modifier = Modifier.height(7.dp))

                TranslatedText(
                    text = "Tarot, Vedic, Life Coach",
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 1,
                    style = MaterialTheme.typography.labelMedium
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(0.dp, 20.dp, 0.dp, 30.dp)
                ) {

                    Button(
                        onClick = {
                            navController.navigate(MainActivity.ChatScreenRoute(astrologerId = "123"))
                        },
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            containerColor = Color.Transparent,
                            contentColor = Color.Red
                        ),
                        border = BorderStroke(1.dp, Color.Red),
                        modifier = Modifier
                            .width(LocalConfiguration.current.screenWidthDp.dp / 3.6f)
                            .height(37.dp)
                    ) {
                        TranslatedText(
                            text = "Message",
                            fontSize = 13.sp,
                            color = Color.Red
                        )
                    }

                    Button(
                        onClick = {
//                            navController.navigate(MainActivity.ChatScreenRoute(astrologerId = "123"))
                        },
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            containerColor = Color(0xFFFFE22B),
                            contentColor = Color.Black
                        ),
//                        border = BorderStroke(1.dp, Color.Red),
                        modifier = Modifier
                            .width(LocalConfiguration.current.screenWidthDp.dp / 2.6f)
                            .height(37.dp)
                            .padding(start = 5.dp)
                    ) {
                        TranslatedText(
                            text = "Follow",
                            fontSize = 14.sp,
                        )
                    }

                    Button(
                        onClick = {
//                            navController.navigate(MainActivity.ChatScreenRoute(astrologerId = "123"))
                        },
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            containerColor = Color(0xFF2B9CFF),
                            contentColor = Color.White
                        ),
//                        border = BorderStroke(1.dp, Color.Red),
                        modifier = Modifier
                            .width(LocalConfiguration.current.screenWidthDp.dp / 3.5f)
                            .height(37.dp)
                            .padding(start = 5.dp)
                    ) {
                        TranslatedText(
                            text = "Share",
                            fontSize = 14.sp,
                        )
                    }
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(40.dp, 0.dp, 40.dp, 30.dp)
                ) {

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {

                        TranslatedText(
                            text = "Posts",
                            overflow = TextOverflow.Ellipsis,
                            maxLines = 1,
                            fontWeight = FontWeight.W400,
                            style = MaterialTheme.typography.bodyMedium
                        )

                        TranslatedText(
                            text = "20",
                            overflow = TextOverflow.Ellipsis,
                            maxLines = 1,
                            style = MaterialTheme.typography.titleMedium,
//                            fontWeight = FontWeight.W300,
                            modifier = Modifier.padding(5.dp)
                        )
                    }

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {

                        TranslatedText(
                            text = "Orders",
                            overflow = TextOverflow.Ellipsis,
                            maxLines = 1,
                            fontWeight = FontWeight.W400,
                            style = MaterialTheme.typography.bodyMedium
                        )

                        TranslatedText(
                            text = "1462",
                            overflow = TextOverflow.Ellipsis,
                            maxLines = 1,
                            style = MaterialTheme.typography.titleMedium,
//                            fontWeight = FontWeight.W300,
                            modifier = Modifier.padding(5.dp)
                        )
                    }

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {

                        TranslatedText(
                            text = "Followers",
                            overflow = TextOverflow.Ellipsis,
                            maxLines = 1,
                            fontWeight = FontWeight.W400,
                            style = MaterialTheme.typography.bodyMedium
                        )

                        TranslatedText(
                            text = "118.5k",
                            overflow = TextOverflow.Ellipsis,
                            maxLines = 1,
                            style = MaterialTheme.typography.titleMedium,
                            modifier = Modifier.padding(5.dp)

                        )
                    }
                }

                Spacer(modifier = Modifier.height(7.dp))

                TranslatedText(
                    text = "No uplods yet",
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 1,
                    style = MaterialTheme.typography.labelMedium
                )

                // Commenting this for play store
//                FlowRow(
//                    modifier = Modifier.fillMaxWidth(),
//                    maxItemsInEachRow = 3,
//                    horizontalArrangement = Arrangement.SpaceEvenly,
//                    verticalArrangement = Arrangement.spacedBy(5.dp)
//                ) {
//                    repeat(35) {
//
//                        Box(
//                            modifier = Modifier
//                                .width(LocalConfiguration.current.screenWidthDp.dp / 3.3f)
//                                .height(180.dp)
//                                .clip(RoundedCornerShape(5.dp))
//                                .background(
//                                    Color(
//                                        0xFFCECECE
//                                    )
//                                )
//                        )
//
//                    }
//                }
            }
        }
    }
}