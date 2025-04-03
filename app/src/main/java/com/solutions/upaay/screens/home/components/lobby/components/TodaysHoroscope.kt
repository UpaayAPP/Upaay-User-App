package com.solutions.upaay.screens.home.components.lobby.components

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.solutions.upaay.utils.translate.TranslatedText
import kotlinx.coroutines.launch
import androidx.compose.foundation.shape.RoundedCornerShape
//import dagger.hilt.android.lifecycle.HiltViewModel
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST
import android.util.Log
import com.google.gson.Gson
import com.google.gson.annotations.SerializedName

// ✅ 1. DATA MODELS
// ✅ 1. FIXED DATA MODEL
data class PanchangRequest(
    val year: Int, val month: Int, val date: Int,
    val hours: Int, val minutes: Int, val seconds: Int,
    val latitude: Double, val longitude: Double, val timezone: Double,
    val observation_point: String // ✅ REQUIRED FIELD: "topocentric" or "geocentric"
)


data class Tithi(
    @SerializedName("tithi_number") val number: Int?,
    @SerializedName("tithi_name") val name: String?,
    @SerializedName("summary") val summary: String?
)

data class Nakshatra(
    @SerializedName("nakshatra_name") val name: String?,
    @SerializedName("summary") val summary: String?
)

data class Yoga(
    @SerializedName("yog_name") val name: String?,
    @SerializedName("meaning") val meaning: String?
)

data class Karana(
    @SerializedName("karan_name") val name: String?,
    @SerializedName("end_time") val endTime: String?
)

data class PanchangResponse(
    val day: String?,
    val sunrise: String?,
    val sunset: String?,
    val vedicSunrise: String?,
    val vedicSunset: String?,
    val tithi: Tithi?,
    val nakshatra: Nakshatra?,
    val yog: Yoga?,
    val karan: Karana?
)

// ✅ 2. API SERVICE
interface PanchangApiService {
    @Headers("Content-Type: application/json")
    @POST("complete-panchang")
    suspend fun getPanchang(@Body request: PanchangRequest): PanchangResponse
}

// ✅ 3. REPOSITORY
class PanchangRepository(private val apiService: PanchangApiService) {
    suspend fun fetchPanchang(request: PanchangRequest): PanchangResponse {
        return try {
            Log.d("PANCHANGLOG", "Sending request: ${Gson().toJson(request)}")  // Log request
            val response = apiService.getPanchang(request)
            Log.d("PANCHANGLOG", "Raw API Response: ${Gson().toJson(response)}")  // Log response
            response
        } catch (e: retrofit2.HttpException) {  // ✅ Catch HTTP errors
            Log.e("PANCHANGLOG", "HTTP Error: ${e.code()} - ${e.response()?.errorBody()?.string()}")
            PanchangResponse(null, null, null, null, null, null, null, null, null)
        } catch (e: Exception) {
            Log.e("PANCHANGLOG", "API call failed", e)
            PanchangResponse(null, null, null, null, null, null, null, null, null)
        }
    }
}

// ✅ 4. VIEWMODEL
class PanchangViewModel(private val repository: PanchangRepository) : ViewModel() {
    private val _panchang = mutableStateOf<PanchangResponse?>(null)
    val panchang: State<PanchangResponse?> = _panchang

    fun getPanchang(request: PanchangRequest) {
        viewModelScope.launch {
            _panchang.value = repository.fetchPanchang(request)
        }
    }
}

@Composable
fun TodaysInsightsScreen(viewModel: PanchangViewModel) {
    val panchang by viewModel.panchang

    val request = PanchangRequest(
        year = 2025, month = 3, date = 15, hours = 6, minutes = 0, seconds = 0,
        latitude = 28.6139, longitude = 77.2090, timezone = 5.5,
        observation_point = "topocentric"  // ✅ REQUIRED FIELD
    )


    LaunchedEffect(Unit) { viewModel.getPanchang(request) }

    Column(
        modifier = Modifier.fillMaxSize().background(Color(0xFFF2F2F2)).padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Today's Insights", style = MaterialTheme.typography.bodyMedium, color = Color(0xFF8B4513))
        if (panchang == null) {
            CircularProgressIndicator(color = Color(0xFF6200EE))
        } else {
            PanchangCard(panchang!!)
        }
    }
}

@Composable
fun PanchangCard(panchang: PanchangResponse) {
    Card(
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        modifier = Modifier.fillMaxWidth().padding(16.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = "📆 Date: ${panchang.day}", fontSize = 18.sp)
            Text(text = "🌞 Sunrise: ${panchang.sunrise}", fontSize = 18.sp)
            Text(text = "🌇 Sunset: ${panchang.sunset}", fontSize = 18.sp)
            Text(text = "📜 Tithi: ${panchang.tithi?.name}", fontSize = 18.sp)
            Text(text = "🔍 Meaning: ${panchang.tithi?.summary}", fontSize = 16.sp, color = Color.Gray)
            Text(text = "✨ Nakshatra: ${panchang.nakshatra?.name}", fontSize = 18.sp)
            Text(text = "🔍 Meaning: ${panchang.nakshatra?.summary}", fontSize = 16.sp, color = Color.Gray)
        }
    }
}


@Composable
fun TodaysHoroscope(panchangViewModel: PanchangViewModel) {

    TodayCard()
//    TodaysInsightsScreen(panchangViewModel)
}
//
@Composable
fun TodayCard() {
    val todayDate = "11 January 2025, Saturday"
    val hinduDate = "Paush Shukla Ekadashi, Vikram Samvat 2081"
    val panchang = """
        Sunrise: 07:12 AM
        Sunset: 05:45 PM
        Moonrise: 02:18 PM
        Moonset: 04:04 AM
        Tithi: Ekadashi (Up to 11:43 PM)
        Nakshatra: Mrigashirsha
        Yoga: Variyan
        Karan: Vanija
    """.trimIndent()

    val goodToDoToday = listOf(
        "Performing Vishnu Puja",
        "Donation and charity work",
        "Fasting for Ekadashi",
        "Reading Bhagavad Gita",
        "Meditation and self-reflection"
    )

    val avoidToday = listOf(
        "Consuming grains and pulses",
        "Starting any new ventures",
        "Engaging in disputes or arguments",
        "Traveling unnecessarily",
        "Performing non-vegetarian cooking or eating"
    )

    val todayDescription =
        "Today is Paush Shukla Ekadashi, a highly auspicious day dedicated to Lord Vishnu. Observing a fast and performing Vishnu Puja on this day can bring spiritual growth and prosperity. Avoid consuming grains and pulses while fasting."

    Card(
        shape = RoundedCornerShape(8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
//        colors = CardDefaults.cardColors(containerColor = Color.White),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF8F1E4)),
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            // Heading
            TranslatedText(
                text = "Today's Insights",
                style = MaterialTheme.typography.headlineLarge,
                color = Color(0xFF8B4513),
            )

            // Description Section
            TranslatedText(
                text = todayDescription,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.W400,
                color = Color.Black,
                modifier = Modifier.padding(top = 5.dp)
            )

            // Date Section
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                TranslatedText(
                    text = "Date: $todayDate",
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.Black
                )
                TranslatedText(
                    text = "Hindu Date: $hinduDate",
                    style = MaterialTheme.typography.titleSmall,
                    color = Color.Gray
                )
            }

            // Panchang Section
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                TranslatedText(
                    text = "Panchang:",
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.Black
                )
                TranslatedText(
                    text = panchang,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.DarkGray
                )
            }

            // Good to Do Section
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                TranslatedText(
                    text = "Good to do today:",
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.Black
                )
                goodToDoToday.forEach { task ->
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = Color(0xFF2E7D32), // Green
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        TranslatedText(
                            text = task,
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.DarkGray
                        )
                    }
                }
            }

            // What Not to Do Section
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                TranslatedText(
                    text = "What to avoid today:",
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.Black
                )
                avoidToday.forEach { task ->
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Cancel,
                            contentDescription = null,
                            tint = Color(0xFFD32F2F), // Red
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        TranslatedText(
                            text = task,
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.DarkGray
                        )
                    }
                }
            }

// Commenting this for play store
//            TopReelsToday("Today's Special", "Discover what to do and what not to do today")
            Spacer(modifier = Modifier.height(5.dp))
        }
    }
}
