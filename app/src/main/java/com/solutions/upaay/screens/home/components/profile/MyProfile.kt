package com.solutions.upaay.screens.home.components.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavController
import com.solutions.upaay.screens.home.components.profile.components.ProfileCard

@Composable
fun MyProfile(navController: NavController) {
    LazyColumn(
        modifier = Modifier.fillMaxSize().background(Color.White)
    ) {
        item {
            ProfileCard()
        }
    }
}