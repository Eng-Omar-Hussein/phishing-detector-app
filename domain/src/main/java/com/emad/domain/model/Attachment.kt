package com.emad.domain.model

import com.google.gson.annotations.SerializedName

data class Attachment(
    @SerializedName("attachment_id") val attachmentId: String,
    @SerializedName("file_name") val fileName: String,
    @SerializedName("mime_type") val mimeType: String,
    @SerializedName("file_size") val fileSize: Long,
    @SerializedName("download_url") val downloadUrl: String
)
