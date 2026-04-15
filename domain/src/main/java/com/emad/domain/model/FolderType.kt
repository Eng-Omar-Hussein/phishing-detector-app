package com.emad.domain.model

import com.google.gson.annotations.SerializedName

enum class FolderType {
    @SerializedName("INBOX")
    INBOX,
    @SerializedName("SENT")
    SENT,
    @SerializedName("TRASH")
    TRASH,
    @SerializedName("SPAM")
    SPAM,
    @SerializedName("HOOKED")
    HOOKED,
    @SerializedName("DRAFTS")
    DRAFTS
}