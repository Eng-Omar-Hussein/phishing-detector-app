package com.emad.domain.model

import com.google.gson.annotations.SerializedName

data class Email(
    @SerializedName("email_id") val emailId: String,
    @SerializedName("subject") val subject: String?,
    @SerializedName("body_snippet") val bodySnippet: String?,
    @SerializedName("body_full") val bodyFull: String?,
    @SerializedName("timestamp") val timestamp: Long,
    @SerializedName("is_read") val isRead: Boolean = false,
    @SerializedName("is_starred") val isStarred: Boolean = false,
    @SerializedName("is_hooked") val isHooked: Boolean,
    @SerializedName("folder_type") val folderType: FolderType = FolderType.INBOX,
    @SerializedName("sender") val sender: User,
    @SerializedName("recipients") val recipients: List<User>,
    @SerializedName("cc") val cc: List<User> = emptyList(),
    @SerializedName("bcc") val bcc: List<User> = emptyList(),
    @SerializedName("attachments") val attachments: List<Attachment> = emptyList(),
    @SerializedName("phishing_score") val phishingScore: Float? = null,
    @SerializedName("risk_level") val riskLevel: RiskLevel? = null,
    @SerializedName("phishing_reasons") val phishingReasons: List<String> = emptyList(),
    @SerializedName("analysis_status") val analysisStatus: AnalysisStatus = AnalysisStatus.PENDING
)