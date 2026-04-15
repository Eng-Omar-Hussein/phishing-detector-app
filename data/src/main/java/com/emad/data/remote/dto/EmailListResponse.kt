package com.emad.data.remote.dto

import com.google.gson.annotations.SerializedName
import com.emad.domain.model.Email

data class EmailListResponse(
    @SerializedName("emails")
    val emails: List<Email>,
    @SerializedName("next_page_token")
    val nextPageToken: String?
)