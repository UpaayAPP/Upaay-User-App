package com.solutions.upaay.screens.wallet.phonepe.model

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
data class PhonePeResponseModel(
    @SerializedName("Payment_Info")
    val paymentInfo: PaymentInfo
) {
    @Keep
    data class PaymentInfo(
        val s1: String,
        val merchantId: String,
        val payloadMain: String,
        val checksum: String
    )
}
