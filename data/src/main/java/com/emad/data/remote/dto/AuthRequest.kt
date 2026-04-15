package com.emad.data.remote.dto

import com.google.gson.annotations.SerializedName

data class AuthRequest(
    @SerializedName("id_token") val idToken: String
)