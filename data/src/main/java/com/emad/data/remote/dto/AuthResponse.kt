package com.emad.data.remote.dto

import com.google.gson.annotations.SerializedName
import com.emad.domain.model.User

data class AuthResponse(
    @SerializedName("token") val token: String,
    @SerializedName("user") val user: User
)