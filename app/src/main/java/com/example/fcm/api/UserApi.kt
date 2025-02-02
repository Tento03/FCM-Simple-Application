package com.example.fcm.api

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create

object UserApi {
    val retrofit=Retrofit.Builder()
        .baseUrl("http://192.168.1.6:3000/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val apiService= retrofit.create(ApiService::class.java)

}