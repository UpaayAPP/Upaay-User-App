package com.solutions.upaay.utils.translate

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

//var CURRENTLY_SELECTED_LANGUAGE by mutableStateOf("en")
//
//object LanguagePreferences {
//    private const val PREFS_NAME = "UserPreferences"
//    private const val LANGUAGE_KEY = "selected_language"
//
//    fun setSelectedLanguage(context: Context, languageCode: String) {
//        val sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
//        sharedPreferences.edit().putString(LANGUAGE_KEY, languageCode).apply()
//    }
//
//    fun getSelectedLanguage(context: Context): String {
//        val sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
//        return sharedPreferences.getString(LANGUAGE_KEY, "en") ?: "en" // Default to English
//    }
//}
