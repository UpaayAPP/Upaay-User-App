package com.solutions.upaay.screens.wallet.phonepe.model

import androidx.annotation.Keep

@Keep
data class PhonePeRequestModel(
    val merchantTransactionId: String,
    val merchantUserId: String,
    val amount: Int,
    val mobileNumber: String,
    val targetApp: String
)
