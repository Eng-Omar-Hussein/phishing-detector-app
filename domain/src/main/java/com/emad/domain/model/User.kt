package com.emad.domain.model

import com.google.gson.annotations.SerializedName

data class User(
    @SerializedName("user_id") val userId: String,
    @SerializedName("name") val name: String,
    @SerializedName("email") val email: String,
    @SerializedName("photo_url") val photoUrl: String?,
    @SerializedName("google_id") val googleId: String? = null,
    @SerializedName("last_login") val lastLogin: Long? = null,
    @SerializedName("created_at") val createdAt: Long? = null
)