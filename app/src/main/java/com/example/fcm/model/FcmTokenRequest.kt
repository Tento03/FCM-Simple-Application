package com.example.fcm.model

data class FcmTokenRequest(
    val user_id: String,
    val fcm_token: String
)