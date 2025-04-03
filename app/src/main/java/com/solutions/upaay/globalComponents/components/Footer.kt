package com.solutions.upaay.globalComponents.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddIcCall
import androidx.compose.material.icons.filled.Assistant
import androidx.compose.material.icons.filled.CallMissedOutgoing
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.ChatBubble
import androidx.compose.material.icons.filled.ChatBubbleOutline
import androidx.compose.material.icons.filled.ContactPhone
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocalLibrary
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.StoreMallDirectory
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material.icons.filled.Work
import androidx.compose.material.icons.outlined.AddIcCall
import androidx.compose.material.icons.outlined.Assistant
import androidx.compose.material.icons.outlined.Chat
import androidx.compose.material.icons.outlined.ChatBubbleOutline
import androidx.compose.material.icons.outlined.ContactPhone
import androidx.compose.material.icons.outlined.Explore
import androidx.compose.material.icons.outlined.Groups
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.LocalLibrary
import androidx.compose.material.icons.outlined.MissedVideoCall
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material.icons.outlined.Storefront
import androidx.compose.material.icons.outlined.WorkOutline
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.solutions.upaay.utils.translate.TranslatedText
@Composable
fun Footer(
    modifier: Modifier = Modifier,
    selectedItem: String,
    onItemSelected: (String) -> Unit
) {
    val items = listOf(
        "Home" to Icons.Outlined.Home,
        "Explore" to Icons.Outlined.Explore,
        "Astrologers" to Icons.Outlined.Groups,
        "Shop" to Icons.Outlined.Storefront,
        "Me" to Icons.Outlined.Person
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(64.dp) // 👈 Thinner footer
            .zIndex(10f)
    ) {
        // Footer background row
        Row(
            modifier = Modifier
                .fillMaxSize()
                .clip(RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp))
                .background(
                    Brush.linearGradient(
                        colors = listOf(Color(0xFFF8F1E4), Color(0xFFE7CFBE))
                    )
                )
                .padding(horizontal = 5.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            items.forEach { (title, icon) ->
                val isSelected = selectedItem == title

                val iconColor by animateColorAsState(
                    targetValue = if (isSelected) Color.Transparent else Color.Black,
                    animationSpec = tween(300), label = ""
                )

                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clickable { onItemSelected(title) },
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = icon,
                            contentDescription = "$title icon",
                            tint = iconColor,
                            modifier = Modifier.size(22.dp)
                        )
                        Text(
                            text = title,
                            fontSize = 10.sp,
                            color = iconColor
                        )
                    }
                }
            }
        }

        // Floating icon layer (animated up from its own position)
        items.forEachIndexed { index, (title, icon) ->
            val isSelected = selectedItem == title

            // Animation properties
            val offsetY by animateDpAsState(
                targetValue = if (isSelected) (-20).dp else 0.dp,
                animationSpec = tween(durationMillis = 300), label = ""
            )
            val scale by animateFloatAsState(
                targetValue = if (isSelected) 1.0f else 0.6f,
                animationSpec = tween(300), label = ""
            )
            val bgColor by animateColorAsState(
                targetValue = if (isSelected) Color(0xFFD38440) else Color.Transparent,
                animationSpec = tween(300), label = ""
            )
            val iconAlpha by animateFloatAsState(
                targetValue = if (isSelected) 1f else 0f,
                animationSpec = tween(300), label = ""
            )

            Box(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .offset(
                        x = ((index - 2) * 65).dp,
                        y = offsetY
                    )
                    .size(46.dp)
                    .graphicsLayer {
                        scaleX = scale
                        scaleY = scale
                        alpha = iconAlpha
                    }
                    .clip(CircleShape)
                    .background(bgColor)
                    .clickable { onItemSelected(title) }
                    .zIndex(if (isSelected) 2f else 0f),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = "$title Selected",
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}




//@Composable
//fun Footer(
//    modifier: Modifier = Modifier,
//    selectedItem: String,
//    onItemSelected: (String) -> Unit
//) {
//    Box(modifier = modifier.fillMaxWidth()) {
//        // Footer Background with Rounded Corners
//        Row(
//            modifier = Modifier
//                .fillMaxWidth()
//                .clip(RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp))
//                .background(
//                    brush = Brush.linearGradient(
//                        colors = listOf(
//                            Color(0xFFF8F1E4),
//                            Color(0xFFE7CFBE),
//                        )
//                    )
//                )
//                .padding(horizontal = 5.dp, vertical = 2.dp),
//            horizontalArrangement = Arrangement.SpaceBetween,
//            verticalAlignment = Alignment.CenterVertically
//        ) {
//            FooterItems(
//                icon = if (selectedItem == "Lobby") Icons.Filled.Home else Icons.Outlined.Home,
//                title = "Home",
//                isSelected = selectedItem == "Lobby",
//                modifier = Modifier.weight(1f)
//            ) {
//                onItemSelected("Lobby")
//            }
//            FooterItems(
//                icon = if (selectedItem == "Chat") Icons.Filled.Assistant else Icons.Outlined.Assistant,
//                title = "Chat",
//                isSelected = selectedItem == "Chat",
//                modifier = Modifier.weight(1f)
//            ) {
//                onItemSelected("Chat")
//            }
//
//            Spacer(modifier = Modifier.weight(1f))
//
//            FooterItems(
//                icon = if (selectedItem == "Call") Icons.Filled.LocalLibrary else Icons.Outlined.LocalLibrary,
//                title = "Call",
//                isSelected = selectedItem == "Call",
//                modifier = Modifier.weight(1f)
//            ) {
//                onItemSelected("Call")
//            }
//            FooterItems(
//                icon = if (selectedItem == "Shop") Icons.Filled.StoreMallDirectory else Icons.Outlined.Storefront,
//                title = "Shop",
////                title = "Upaays",
//                isSelected = selectedItem == "Shop",
//                modifier = Modifier.weight(1f)
//            ) {
//                onItemSelected("Shop")
//            }
//        }
//
//        val exploreOffset by animateDpAsState(
//            targetValue = if (selectedItem == "Explore") 14.dp else (-20).dp,
//            animationSpec = tween(durationMillis = 300)
//        )
//        val exploreSize by animateDpAsState(
//            targetValue = if (selectedItem == "Explore") 42.dp else 54.dp,
//            animationSpec = tween(durationMillis = 300)
//        )
//
//        Box(
//            modifier = Modifier
//                .align(Alignment.TopCenter)
//                .offset(y = exploreOffset)
//                .size(exploreSize)
//                .clip(CircleShape)
//                .background(
//                    if (selectedItem == "Explore") Color.Black
//                    else Color(0xFFB99177)
////                    else Color(0xFFDDBEA9)
//                )
//                .clickable {
//                    onItemSelected("Explore")
//                },
//            contentAlignment = Alignment.Center
//        ) {
//            Icon(
//                imageVector = Icons.Default.Explore,
//                contentDescription = "Explore",
//                tint = Color.White,
//                modifier = Modifier.size(exploreSize - 20.dp) // Adjust icon size dynamically
//            )
//        }
//    }
//}

@Composable
fun FooterItems(
    icon: ImageVector,
    title: String,
    isSelected: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
//    val backgroundColor = if (isSelected) MaterialTheme.colorScheme.primary.copy(alpha = 0.1f) else Color.Transparent
//    val textStyle = if (isSelected) MaterialTheme.typography.labelMedium else MaterialTheme.typography.labelSmall
    val textWeight = if (isSelected) FontWeight.Normal else FontWeight.Light
    val iconSize = if (isSelected) 25.dp else 23.dp
    val fontSize = if (isSelected) 12.sp else 11.5.sp

    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .padding(start = 3.dp, end = 3.dp, top = 5.dp, bottom = 3.dp)
            .clip(RoundedCornerShape(8.dp))
            .clickable(onClick = onClick)
            .padding(5.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = "$title icon",
            tint = if (isSelected) Color(0xFF855E45) else LocalContentColor.current,
            modifier = Modifier.size(iconSize)
        )
        TranslatedText(
            text = title,
            fontWeight = textWeight,
            fontSize = fontSize,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.padding(top = 3.dp)
        )
    }
}



