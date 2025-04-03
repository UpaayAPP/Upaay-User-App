package com.solutions.upaay.screens.home

import android.content.SharedPreferences
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import com.solutions.upaay.MainActivity
import com.solutions.upaay.globalComponents.components.Footer
import com.solutions.upaay.globalComponents.components.Header
import com.solutions.upaay.globalComponents.components.HeaderDrawer
import com.solutions.upaay.screens.auth.newuser.basicinfo.auth
import com.solutions.upaay.screens.home.components.astrologer.AllAstrologers
import com.solutions.upaay.screens.home.components.astrologer.components.cards.Astrologer
import com.solutions.upaay.screens.home.components.explore.ExploreUpaay
import com.solutions.upaay.screens.home.components.lobby.Lobby
import com.solutions.upaay.screens.home.components.profile.MyProfile
import com.solutions.upaay.screens.home.components.shop.Shop
import com.solutions.upaay.utils.loading.LoadingStateManager
import com.solutions.upaay.utils.profile.ProfileState
import com.solutions.upaay.utils.profile.ProfileUtils
import kotlinx.coroutines.launch

var handleChangeSelectedItem: (newValue: String) -> Unit = {}

@Composable
fun HomeScreen(
    navController: NavController,
    sharedPreferences: SharedPreferences,
    forceCheckProfileState: Boolean,
    selectedLanguage: String,
    updateLanguage: (String) -> Unit,
    selectAstrologerForCall: (Astrologer) -> Unit
) {

    LaunchedEffect(Unit) {

        LoadingStateManager.showLoading()
        val profileState = ProfileUtils.getUserProfileState(forceCheckProfileState)

        when (profileState) {
            ProfileState.USER_NOT_FOUND -> {
                LoadingStateManager.hideLoading()
                navController.navigate(MainActivity.AuthCreateUserRoute) {
                    popUpTo(0) { inclusive = true }
                }
            }

            ProfileState.PROFILE_INCOMPLETE -> {
                LoadingStateManager.hideLoading()
                navController.navigate(MainActivity.AuthStartBuildingProfileRoute) {
                    popUpTo(0) { inclusive = true }
                }
            }

            ProfileState.VALID_PROFILE -> {
                LoadingStateManager.hideLoading()
                // everything is fine, show the home screen
            }
        }
    }

    var selectedItem by rememberSaveable { mutableStateOf("Astrologers") }
    handleChangeSelectedItem = {
        selectedItem = it
    }
    val scope = rememberCoroutineScope()

    val drawerState = rememberDrawerState(DrawerValue.Closed)

    BackHandler(enabled = selectedItem !== "Astrologers") {
        selectedItem = "Astrologers"
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            HeaderDrawer(
                auth, false, {}, selectedLanguage = selectedLanguage,
                updateLanguage = updateLanguage, navController
            ) {
                scope.launch {
                    drawerState.close()
                }
            }
        }
    ) {
        Column {
            Header(
                navController, selectedItem, scope, drawerState
            )
            Box(Modifier.fillMaxSize()) {
                when (selectedItem) {
                    "Home" -> {
                        Lobby(
                            navController,
                            sharedPreferences,
                            changeSelectedItem = { selectedItem = it })
                    }

                    "Explore" -> {
                        ExploreUpaay(navController)
                    }

                    "Shop" -> {
                        Shop(navController, sharedPreferences)
                    }

                    "Astrologers" -> {
                        AllAstrologers(navController, false, selectAstrologerForCall)
                    }

                    "Me" -> {
                        MyProfile(navController)
                    }

                    else -> {
                        AllAstrologers(navController, false, selectAstrologerForCall)
                    }
                }

                Footer(
                    selectedItem = selectedItem,
                    onItemSelected = {
                        selectedItem = it
                    },
                    modifier = Modifier.align(Alignment.BottomCenter)
                )
            }
        }
    }
}