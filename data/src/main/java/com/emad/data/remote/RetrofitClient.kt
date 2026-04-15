package com.emad.data.remote

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {

    // You will replace this with your actual Python backend URL later
    // For Emulator use: "http://10.0.2.2:8000/"
    // For Physical Device: Use your PC's IP address like "http://192.168.1.5:8000/"
    private const val BASE_URL = "http://10.0.2.2:8000/"

    fun create(authInterceptor: AuthInterceptor): PythonApiService {

        // 1. Create the HTTP Client with the Interceptor
        val client = OkHttpClient.Builder()
            .addInterceptor(authInterceptor)
            .build()

        // 2. Build Retrofit
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(PythonApiService::class.java)
    }
}