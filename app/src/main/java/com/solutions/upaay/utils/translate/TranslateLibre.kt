package com.solutions.upaay.utils.translate

//import android.util.Log
//import androidx.compose.material3.MaterialTheme
//import androidx.compose.material3.Text
//import androidx.compose.runtime.*
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.graphics.Color
//import androidx.compose.ui.text.TextStyle
//import kotlinx.coroutines.launch
//import retrofit2.Retrofit
//import retrofit2.converter.gson.GsonConverterFactory
//import retrofit2.http.GET
//import retrofit2.http.Path
//
//// 🔹 Lingva Translate API Interface
//interface LingvaTranslateApi {
//    @GET("api/v1/{source}/{target}/{text}")
//    suspend fun translate(
//        @Path("source") source: String,
//        @Path("target") target: String,
//        @Path("text") text: String
//    ): TranslateResponse
//}
//
//// 🔹 Response Data Model
//data class TranslateResponse(val translation: String?)
//
//// 🔹 Singleton for Retrofit
//object RetrofitInstance {
//    private val retrofit = Retrofit.Builder()
//        .baseUrl("https://lingva.ml/") // 🔥 Free & No API Key Needed
//        .addConverterFactory(GsonConverterFactory.create())
//        .build()
//
//    val api: LingvaTranslateApi = retrofit.create(LingvaTranslateApi::class.java)
//}
//
//// 🔹 Translation Function
//suspend fun translateTextLingva(text: String, targetLang: String): String {
//    return try {
//        val response = RetrofitInstance.api.translate("auto", targetLang, text)
//        Log.d("TRANSLATION_DEBUG", "API Response: $response")
//        response.translation ?: text // 🔥 Handle null response safely
//    } catch (e: retrofit2.HttpException) {
//        Log.e("TRANSLATION_ERROR", "HTTP Error: ${e.response()?.errorBody()?.string()}")
//        text
//    } catch (e: Exception) {
//        Log.e("TRANSLATION_ERROR", "Unexpected error: ${e.message}")
//        text
//    }
//}
//
//// 🔹 Translatable Text Composable
//@Composable
//fun TranslatableText(
//    modifier: Modifier = Modifier,
//    originalText: String,
//    targetLang: String,
//    style: TextStyle = MaterialTheme.typography.bodyMedium,
//    color: Color = Color.Unspecified
//) {
//    var translatedText by remember { mutableStateOf(originalText) }
//    val coroutineScope = rememberCoroutineScope()
//
//    LaunchedEffect(targetLang) {
//        coroutineScope.launch {
//            translatedText = translateTextLingva(originalText, targetLang)
//        }
//    }
//
//    Text(
//        text = translatedText,
//        style = style,
//        color = color,
//        modifier = modifier
//    )
//}
