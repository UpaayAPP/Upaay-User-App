package com.solutions.upaay.screens.search.search

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.solutions.upaay.MainActivity
import com.solutions.upaay.globalComponents.components.CrackinTextField
import com.solutions.upaay.screens.home.components.lobby.components.TopAstrologers
import com.solutions.upaay.utils.translate.TranslatedText

@Composable
fun SearchScreen(navController: NavHostController) {

    val searchQuery = remember {
        mutableStateOf(TextFieldValue(""))
    }
    val context = LocalContext.current

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFAF7F3))
            .padding(horizontal = 10.dp, vertical = 15.dp)
    ) {
        item {

            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Go back",
                tint = Color(0xFFAFB6D6),
                modifier = Modifier
                    .size(30.dp)
                    .clickable {
                        navController.popBackStack()
                    }
                    .padding(start = 5.dp)
            )

            TranslatedText(
                text = "Search Astrologers, Horoscopes, etc",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(top = 20.dp, start = 10.dp)
            )

            CrackinTextField(
                value = searchQuery,
                label = "Name, Day, Kundali, Posts, etc",
                placeholder = "Type here to search",
                modifier = Modifier.padding(10.dp, 20.dp, 10.dp, 0.dp)
            )

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
            ) {

                Spacer(modifier = Modifier.weight(1f))

                Button(
                    onClick = {
                        if (searchQuery.value.text.isNotEmpty()) {
                            navController.navigate(MainActivity.SearchResultScreenRoute(searchQuery.value.text))
                        }else{
                            Toast.makeText(context, "Enter something to search", Toast.LENGTH_SHORT).show()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFFF9800),
                        contentColor = Color.White
                    ),
                    modifier = Modifier
                        .padding(10.dp, 30.dp, 10.dp, 7.dp)
                ) {

                    TranslatedText(
                        text = "Search",
                        style = MaterialTheme.typography.titleSmall
                    )
                }
            }

            Spacer(
                modifier = Modifier
                    .padding(top = 25.dp)
                    .fillMaxWidth()
                    .height(15.dp)
                    .background(Color.Gray.copy(alpha = 0.1f))
            )

            TopAstrologers(navController = navController, showViewAllButton = false)
        }
    }
}

//@Composable
//fun CurvedSearchBar() {
//
//    var searchQuery by remember { mutableStateOf(TextFieldValue("")) }
//
//    Row(
//        verticalAlignment = Alignment.CenterVertically,
//        modifier = Modifier
//            .border(1.dp, Color.Gray.copy(alpha = 0.3f), RoundedCornerShape(40.dp))
//            .padding(horizontal = 16.dp)
//    ) {
//        // Icon for the search bar
//        Icon(
//            imageVector = Icons.Default.Search,
//            contentDescription = "Search Icon",
//            tint = Color.Gray.copy(alpha = 0.5f),
//            modifier = Modifier.size(24.dp)
//        )
//
//        BasicTextField(
//            value = searchQuery,
//            onValueChange = { searchQuery = it },
//            textStyle = TextStyle(
//                color = LocalContentColor.current,
//                fontSize = 13.sp,
//                fontWeight = FontWeight.Normal,
//                lineHeight = 16.sp,
//                letterSpacing = 0.5.sp
//            ),
//            singleLine = true,
//            cursorBrush = SolidColor(Color.Gray),
//            modifier = Modifier
//                .weight(0.7f)
//                .height(39.dp)
//                .padding(horizontal = 6.dp),
//            decorationBox = { innerTextField ->
//                Row(
//                    modifier = Modifier
////                        .fillMaxWidth()
//                        .background(Color.Transparent)
//                        .padding(horizontal = 4.dp, vertical = 8.dp),
//                    verticalAlignment = Alignment.CenterVertically
//                ) {
//                    if (searchQuery.text.isEmpty()) {
//                        TranslatedText(
//                            text = "Search Jobs here ...",
//                            fontSize = 14.sp,
//                            color = Color.Gray,
//                        )
//                    }
//                    innerTextField() // Display the actual text field
//                }
//            }
//        )
//    }

//}
