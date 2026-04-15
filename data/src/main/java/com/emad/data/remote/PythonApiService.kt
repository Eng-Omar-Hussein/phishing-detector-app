package com.emad.data.remote

import com.emad.data.remote.dto.AuthRequest
import com.emad.data.remote.dto.AuthResponse
import com.emad.data.remote.dto.EmailListResponse
import com.emad.domain.model.Email
import retrofit2.Response
import retrofit2.http.*

interface PythonApiService {

    // ── Auth ──────────────────────────────────────────────────────────────
    @POST("auth/google")
    suspend fun loginWithGoogle(@Body request: AuthRequest): Response<AuthResponse>

    // ── Emails ────────────────────────────────────────────────────────────
    @GET("emails")
    suspend fun getEmails(@Query("folder") folder: String): Response<EmailListResponse>

    // ── Actions ───────────────────────────────────────────────────────────
    @POST("emails/{id}/hook")
    suspend fun setHookedStatus(
        @Path("id") emailId: String,
        @Query("status") isHooked: Boolean
    ): Response<Unit>

    @POST("emails/{id}/read")
    suspend fun markAsRead(@Path("id") emailId: String): Response<Unit>

    // NEW: Triggers ML + cybersecurity analysis on the backend
    // Returns the updated Email object with isHooked populated
    @POST("emails/{id}/analyze")
    suspend fun analyzeEmail(@Path("id") emailId: String): Response<Email>
}