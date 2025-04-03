package com.solutions.upaay.screens.auth.newuser.profileinfo

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import com.solutions.upaay.screens.auth.newuser.profileinfo.screens.UserDetails

@Composable
fun BuildProfile(navController: NavHostController) {

    UserDetails(context = LocalContext.current, navController )
}

//data class AstrologerAdditionalDetails (
//    val speciality : String,
//    val experience : String,
//    val price : String,
//    val languages : List<String>,
//)