package com.example.bikeapp.data.remote


import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    private const val BASE_URL = "https://www.strava.com/api/v3/"

    val instance: StravaApi by lazy {
        val client = OkHttpClient.Builder()
           .build()

        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(client) // Add the OkHttpClient to Retrofit
            .build()

        retrofit.create(StravaApi::class.java)
    }
}