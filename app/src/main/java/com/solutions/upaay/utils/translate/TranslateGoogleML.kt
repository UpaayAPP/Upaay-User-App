package com.solutions.upaay.utils.translate

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.TextUnit
import com.google.mlkit.common.model.DownloadConditions
import com.google.mlkit.nl.translate.TranslateLanguage
import com.google.mlkit.nl.translate.Translation
import com.google.mlkit.nl.translate.Translator
import com.google.mlkit.nl.translate.TranslatorOptions
import com.solutions.upaay.MainActivity
import com.solutions.upaay.utils.loading.LoadingStateManager.hideLoadingWithText
import com.solutions.upaay.utils.loading.LoadingStateManager.showLoadingWithText
import kotlinx.coroutines.tasks.await

object MLKitTranslator {
    private var translator: Translator? = null

    suspend fun translateText(
        text: String,
        targetLang: String,
        onLoading: (Boolean) -> Unit,
        onResult: (String) -> Unit,
        onError: (String) -> Unit
    ) {
        val sourceLang = TranslateLanguage.ENGLISH
        val targetLanguageCode = getMLKitLanguageCode(targetLang)

        if (targetLanguageCode == null) {
            onError("Unsupported language: $targetLang")
            return
        }

        val options = TranslatorOptions.Builder()
            .setSourceLanguage(sourceLang)
            .setTargetLanguage(targetLanguageCode)
            .build()

        translator = Translation.getClient(options)
        val conditions = DownloadConditions.Builder().build()

        onLoading(true) // Show loading overlay
        try {
            translator!!.downloadModelIfNeeded(conditions).await()
            val translatedText = translator!!.translate(text).await()
            onLoading(false) // Hide overlay
            onResult(translatedText)
        } catch (e: Exception) {
            onLoading(false)
            onError("Translation failed: ${e.message}")
        }
    }

    private fun getMLKitLanguageCode(lang: String): String? {
        return when (lang.lowercase()) {
            "hi" -> TranslateLanguage.HINDI
            "te" -> TranslateLanguage.TELUGU
            "ta" -> TranslateLanguage.TAMIL
            "bn" -> TranslateLanguage.BENGALI
            "mr" -> TranslateLanguage.MARATHI
            "gu" -> TranslateLanguage.GUJARATI
            "kn" -> TranslateLanguage.KANNADA
            "en" -> TranslateLanguage.ENGLISH
            else -> null
        }
    }
}

object TranslationCache {
    val cache = mutableStateMapOf<String, String>()
}

@Composable
fun TranslatableText(
    modifier: Modifier = Modifier,
    originalText: String,
    style: TextStyle = MaterialTheme.typography.bodyMedium,
    color: Color = Color.Unspecified,
    fontWeight: FontWeight = FontWeight.Normal,
    fontSize: TextUnit = TextUnit.Unspecified,
    overflow: TextOverflow = TextOverflow.Clip,
    textAlign: TextAlign? = null,
    textDecoration: TextDecoration? = null,
    maxLines: Int = Int.MAX_VALUE,
) {
    val context = LocalContext.current
    val selectedLanguage = (context as MainActivity).selectedLanguage.collectAsState().value

    var translatedText by remember { mutableStateOf(originalText) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(originalText, selectedLanguage) {
        if (selectedLanguage == "en") {
            translatedText = originalText
        } else {
            // ✅ Check Global Cache First
            if (TranslationCache.cache.containsKey(originalText)) {
                translatedText = TranslationCache.cache[originalText]!!
            } else {
                showLoadingWithText("Getting translation ready...")

                MLKitTranslator.translateText(
                    text = originalText,
                    targetLang = selectedLanguage,
                    onLoading = { isLoading ->
                        if (!isLoading) hideLoadingWithText()
                    },
                    onResult = { translatedTextResult ->
                        translatedText = translatedTextResult
                        TranslationCache.cache[originalText] =
                            translatedTextResult // ✅ Save globally
                        hideLoadingWithText()
                    },
                    onError = { error ->
                        errorMessage = error
                        hideLoadingWithText()
                    }
                )
            }
        }
    }


    Text(
        text = translatedText,
        style = style,
        modifier = modifier,
        color = color,
        fontWeight = fontWeight,
        fontSize = fontSize,
        overflow = overflow,
        maxLines = maxLines,
        textAlign = textAlign,
        textDecoration = textDecoration,
    )

//    Box(modifier = modifier) {
//        Column {
//            Text(text = translatedText, style = style, color = color)
//            errorMessage?.let {
//                Text(text = it, style = style, color = color)
//            }
//        }
//    }
}

@Composable
fun TranslatedText(
    text: String,
    modifier: Modifier = Modifier,
    style: TextStyle = TextStyle.Default,
    color: Color = Color.Unspecified,
    fontWeight: FontWeight = FontWeight.Normal,
    fontSize: TextUnit = TextUnit.Unspecified,
    overflow: TextOverflow = TextOverflow.Clip,
    maxLines: Int = Int.MAX_VALUE,
    textAlign: TextAlign? = null,
    textDecoration: TextDecoration? = null,
) {
    TranslatableText(
        modifier = modifier,
        originalText = text,
        style = style,
        color = color,
        fontWeight = fontWeight,
        overflow = overflow,
        fontSize = fontSize,
        textAlign = textAlign,
        maxLines = maxLines,
        textDecoration = textDecoration,
    )
}
