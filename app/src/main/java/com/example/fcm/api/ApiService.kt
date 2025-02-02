package com.example.fcm.api

import com.example.fcm.model.FcmTokenRequest
import com.example.fcm.model.MessageRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface ApiService {
    @POST("send_message")
    suspend fun send_message(@Body messageRequest: MessageRequest):Response<Void>

    @POST("register_token")
    suspend fun register_token(@Body fcmTokenRequest: FcmTokenRequest):Response<Void>
}