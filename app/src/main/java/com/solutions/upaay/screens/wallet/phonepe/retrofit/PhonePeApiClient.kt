package com.solutions.upaay.screens.wallet.phonepe.retrofit

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object PhonePeApiClient {
    private const val BASE_URL = "https://asia-south1-YOUR_PROJECT.cloudfunctions.net/phonepe/"

    val retrofit: PhonePeApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(PhonePeApiService::class.java)
    }
}
