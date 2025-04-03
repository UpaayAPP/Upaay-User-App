package com.solutions.upaay.globalComponents.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.Image
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.foundation.background
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import coil.compose.rememberAsyncImagePainter
import com.google.accompanist.pager.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.yield

@Composable
fun AutoScrollingBanner(
    modifier: Modifier = Modifier,
    images: List<Int>,
    scrollInterval: Long = 4500,
    boxHeight: Float = 160f,
    imageContentScale: ContentScale = ContentScale.FillWidth,
    imageStartPadding: Int = 20
) {
    val pagerState = rememberPagerState()
    val coroutineScope = rememberCoroutineScope()

    // Auto-scrolling logic
    LaunchedEffect(key1 = pagerState) {
        while (true) {
            yield() // To avoid blocking the main thread
            delay(scrollInterval) // Scroll interval
            with(pagerState) {
                val nextPage = (currentPage + 1) % images.size
                animateScrollToPage(nextPage)
            }
        }
    }

    Column(modifier = modifier.fillMaxWidth()) {
        // Image Pager
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(boxHeight.dp)
        ) {
            HorizontalPager(
                count = images.size,
                state = pagerState,
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(
                        RoundedCornerShape(
                            topStart = 15.dp,
                            topEnd = 5.dp,
                            bottomStart = 5.dp,
                            bottomEnd = 15.dp
                        )
                    )
            ) { page ->
                Image(
                    painter = painterResource(id = images[page]),
                    contentDescription = "Banner Image",
                    contentScale = imageContentScale,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(imageStartPadding.dp, 4.dp, 10.dp, 4.dp)
                        .clip(
                            RoundedCornerShape(
                                topStart = 15.dp,
                                topEnd = 5.dp,
                                bottomStart = 5.dp,
                                bottomEnd = 15.dp
                            )
                        )
                )
            }
        }

        HorizontalPagerIndicator(
            pagerState = pagerState,
            activeColor = Color.DarkGray,
            inactiveColor = Color.LightGray,
            indicatorWidth = 5.dp,
            indicatorHeight = 5.dp,
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(top = 15.dp) // Adjust spacing between image and indicator
        )

    }
}
