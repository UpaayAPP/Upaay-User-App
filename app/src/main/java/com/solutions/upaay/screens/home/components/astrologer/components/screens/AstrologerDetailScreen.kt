package com.solutions.upaay.screens.home.components.astrologer.components.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.solutions.upaay.screens.home.components.astrologer.components.cards.Astrologer
import com.solutions.upaay.screens.home.components.astrologer.components.cards.AstrologerCard
import com.solutions.upaay.screens.home.components.astrologer.components.cards.AstrologerReview
import com.solutions.upaay.screens.home.components.astrologer.components.cards.AstrologerReviewCard
import com.solutions.upaay.utils.database.retrieve.fetchAstrologerById
import com.solutions.upaay.utils.translate.TranslatedText

//val astrologerReview = AstrologerReview(
//    name = "Pewdiepie",
//    rating = 4.5f,
//    review = "This is a portion for the basic details about the Astrologer so people can basically know somthing about them before contacting them.",
//    profileImageUrl = "https://img.freepik.com/free-vector/businessman-character-avatar-isolated_24877-60111.jpg"
//)

val astrologerReviews = listOf(
    AstrologerReview(
        name = "Amit Sharma",
        rating = 4.8f,
        review = "Had an amazing consultation. Highly recommended!",
        profileImageUrl = "https://img.freepik.com/free-vector/businessman-character-avatar-isolated_24877-60111.jpg"
    ),
    AstrologerReview(
        name = "Priya Verma",
        rating = 4.7f,
        review = "The predictions were accurate, and the remedies really helped.",
        profileImageUrl = "https://img.freepik.com/free-vector/businessman-character-avatar-isolated_24877-60111.jpg"
    ),
    AstrologerReview(
        name = "Rahul Mehta",
        rating = 4.9f,
        review = "Very professional and provides detailed analysis.",
        profileImageUrl = "https://img.freepik.com/free-vector/businessman-character-avatar-isolated_24877-60111.jpg"
    ),
    AstrologerReview(
        name = "Sneha Nair",
        rating = 4.6f,
        review = "Accurate readings and practical solutions!",
        profileImageUrl = "https://img.freepik.com/free-vector/businessman-character-avatar-isolated_24877-60111.jpg"
    ),
    AstrologerReview(
        name = "Vikas Gupta",
        rating = 4.9f,
        review = "Highly knowledgeable and experienced astrologer.",
        profileImageUrl = "https://img.freepik.com/free-vector/businessman-character-avatar-isolated_24877-60111.jpg"
    ),
    AstrologerReview(
        name = "Neha Patel",
        rating = 4.7f,
        review = "Great experience! Got clarity on many aspects of my life.",
        profileImageUrl = "https://img.freepik.com/free-vector/businessman-character-avatar-isolated_24877-60111.jpg"
    ),
    AstrologerReview(
        name = "Arjun Rao",
        rating = 4.8f,
        review = "Excellent astrologer! Very insightful and helpful.",
        profileImageUrl = "https://img.freepik.com/free-vector/businessman-character-avatar-isolated_24877-60111.jpg"
    ),
    AstrologerReview(
        name = "Pooja Choudhary",
        rating = 4.9f,
        review = "Really helped me understand my horoscope better.",
        profileImageUrl = "https://img.freepik.com/free-vector/businessman-character-avatar-isolated_24877-60111.jpg"
    ),
    AstrologerReview(
        name = "Ramesh Iyer",
        rating = 4.5f,
        review = "The remedies provided were very useful and effective.",
        profileImageUrl = "https://img.freepik.com/free-vector/businessman-character-avatar-isolated_24877-60111.jpg"
    ),
    AstrologerReview(
        name = "Divya Menon",
        rating = 4.9f,
        review = "Very polite and gives honest advice.",
        profileImageUrl = "https://img.freepik.com/free-vector/businessman-character-avatar-isolated_24877-60111.jpg"
    )
)

val astrologerInfoText = """
With over 15 years of experience in Vedic astrology, palmistry, and numerology, I have guided thousands of clients through life's uncertainties. Specializing in career, relationships, marriage compatibility, and financial growth, my insights are rooted in ancient wisdom and practical solutions. I provide detailed horoscope analysis based on planetary positions and offer remedies like gemstone recommendations, mantra chanting, and vastu corrections to enhance positivity in life. My approach is not just predictive but also focused on helping individuals make better decisions.Having studied under renowned astrologers and earned certifications in Jyotish Shastra, I blend traditional astrological methods with modern life guidance. Consultations are conducted in Hindi, English, and regional languages, ensuring clarity and personal connection. Connect with me today for accurate insights and life-changing guidance."""

@Composable
fun AstrologerDetailsScreen(
    astrologerId: String,
    navController: NavController,
    selectAstrologerForCall: (Astrologer) -> Unit
) {

    var isDescriptionExpanded by remember { mutableStateOf(false) }

    val astrologerState = produceState<Astrologer?>(initialValue = null) {
        fetchAstrologerById(astrologerId) { value = it }
    }

    val astrologer = astrologerState.value

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 5.dp)
    ) {
        item {
            Column {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(5.dp, 10.dp)
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
                        text = "About Guruji",
                        fontWeight = FontWeight.W400,
                        style = MaterialTheme.typography.titleSmall,
                    )
                }

                astrologer?.let {
                    AstrologerCard(
                        navController = navController,
                        isForChatScreen = false,
                        isForDetailScreen = true,
                        astrologer = astrologer
                    ) {
                        selectAstrologerForCall(it)
                    }
                }

                // Commenting this for play store
//                LazyRow(
//                    modifier = Modifier.padding(start = 5.dp, top = 15.dp)
//                ) {
//                    items(8) {
//                        Box(
//                            modifier = Modifier
//                                .padding(end = 10.dp,)
//                                .width(120.dp)
//                                .height(150.dp)
//                                .clip(RoundedCornerShape(5.dp))
//                                .background(
//                                    Color(
//                                        0xFFCECECE
//                                    )
//                                )
//                        )
//                    }
//                }

                TranslatedText(
                    text = "About",
                    style = MaterialTheme.typography.titleSmall,
                    modifier = Modifier.padding(top = 25.dp, start = 10.dp)
                )
                Text(
                    text = buildAnnotatedString {
                        if (isDescriptionExpanded) {
                            append(astrologerInfoText) // Show full text
                            withStyle(style = SpanStyle(color = Color.Blue)) {
                                append(" Show Less")
                            }
                        } else {
                            append(astrologerInfoText.take(100)) // Adjust the character limit based on your UI
                            if (astrologerInfoText.length > 100) {
                                append("... ")
                                withStyle(style = SpanStyle(color = Color.Blue)) {
                                    append("Show More")
                                }
                            }
                        }
                    },
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.W300,
                    modifier = Modifier
                        .padding(top = 10.dp, start = 10.dp, end = 10.dp)
                        .clickable {
                            isDescriptionExpanded = !isDescriptionExpanded
                        }
                )

                // Commenting this for play store
//                Row(
//                    horizontalArrangement = Arrangement.SpaceBetween,
//                    verticalAlignment = Alignment.CenterVertically,
//                    modifier = Modifier
//                        .fillMaxWidth()
//                        .padding(top = 27.dp, start = 10.dp, end = 10.dp)
//                ) {
//                    TranslatedText(
//                        text = "Uploads",
//                        style = MaterialTheme.typography.titleSmall
//                    )
//
//                    TranslatedText(
//                        text = "View All",
//                        style = MaterialTheme.typography.labelMedium,
//                        color = Color.Gray,
//                        modifier = Modifier.clickable {
//                            navController.navigate(MainActivity.AstrologerUploadsScreenRoute)
//                        }
//                    )
//                }
//
//                LazyRow(
//                    modifier = Modifier.padding(start = 5.dp, top = 20.dp)
//                ) {
//                    items(3) {
//                        Box(
//                            modifier = Modifier
//                                .padding(end = 10.dp,)
//                                .width(120.dp)
//                                .height(180.dp)
//                                .clip(RoundedCornerShape(5.dp))
//                                .background(
//                                    Color(
//                                        0xFFCECECE
//                                    )
//                                )
//                        )
//                    }
//                }

                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 27.dp, bottom = 10.dp, start = 10.dp, end = 10.dp)
                ) {
                    TranslatedText(
                        text = "User Reviews",
                        style = MaterialTheme.typography.titleSmall
                    )

                    TranslatedText(
                        text = "",
                        style = MaterialTheme.typography.labelMedium,
                        color = Color.Gray
                    )
                }

                astrologerReviews.forEach {
                    AstrologerReviewCard(navController, it)
                }
            }
        }
    }
}






