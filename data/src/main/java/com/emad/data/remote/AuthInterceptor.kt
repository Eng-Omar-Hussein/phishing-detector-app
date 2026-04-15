package com.emad.data.remote

import com.emad.data.local.SessionManager
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject

class AuthInterceptor @Inject constructor(
    private val sessionManager: SessionManager
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        val token = sessionManager.getAuthToken()

        // 1. If we have no token, just proceed (e.g., Login request)
        if (token.isNullOrBlank()) {
            return chain.proceed(originalRequest)
        }

        // 2. If we HAVE a token, attach it to the header
        val newRequest = originalRequest.newBuilder()
            .addHeader("Authorization", "Bearer $token")
            .build()

        return chain.proceed(newRequest)
    }
}