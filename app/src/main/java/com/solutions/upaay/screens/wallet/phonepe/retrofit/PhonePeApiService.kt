package com.solutions.upaay.screens.wallet.phonepe.retrofit

import com.solutions.upaay.screens.wallet.phonepe.model.PhonePeRequestModel
import com.solutions.upaay.screens.wallet.phonepe.model.PhonePeResponseModel
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface PhonePeApiService {
    @POST("paymentinfo")
    suspend fun initiatePayment(@Body request: PhonePeRequestModel): Response<PhonePeResponseModel>
}
