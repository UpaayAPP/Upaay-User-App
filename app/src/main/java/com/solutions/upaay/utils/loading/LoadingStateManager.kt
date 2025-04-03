package com.solutions.upaay.utils.loading

import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

object LoadingStateManager {
    var isLoading by mutableStateOf(true)
    var isLoadingWithText by mutableStateOf("")

    fun showLoading() {
        isLoading = true
    }

    fun showLoadingWithText(text: String) {
        isLoadingWithText = text
    }

    fun hideLoading() {
        CoroutineScope(Dispatchers.Main).launch {
            delay(200L)
            isLoading = false
        }
    }

    fun hideLoadingWithText() {
        CoroutineScope(Dispatchers.Main).launch {
            delay(100L)
            isLoadingWithText = ""
        }
    }
}
