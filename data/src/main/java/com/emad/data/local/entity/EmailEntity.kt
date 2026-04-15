package com.emad.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.emad.domain.model.Attachment
import com.emad.domain.model.Email
import com.emad.domain.model.FolderType
import com.emad.domain.model.User

@Entity(tableName = "emails_table")
data class EmailEntity(
    @PrimaryKey
    val emailId: String,

    val subject: String?,
    val bodySnippet: String?,
    val bodyFull: String?,
    val timestamp: Long,
    val isRead: Boolean,
    val isStarred: Boolean,
    val isHooked: Boolean, // The Phishing Status
    val folderType: FolderType, // stored as String via Converter

    // Complex Objects (Handled by Converters)
    val sender: User,
    val recipients: List<User>,
    val cc: List<User>,
    val bcc: List<User>,
    val attachments: List<Attachment>
) {
    fun toDomainModel(): Email {
        return Email(
            emailId = emailId,
            subject = subject,
            bodySnippet = bodySnippet,
            bodyFull = bodyFull,
            timestamp = timestamp,
            isRead = isRead,
            isStarred = isStarred,
            isHooked = isHooked,
            folderType = folderType,
            sender = sender,
            recipients = recipients,
            cc = cc,
            bcc = bcc,
            attachments = attachments
        )
    }
}

fun Email.toEntity(): EmailEntity {
    return EmailEntity(
        emailId = this.emailId,
        subject = this.subject,
        bodySnippet = this.bodySnippet,
        bodyFull = this.bodyFull,
        timestamp = this.timestamp,
        isRead = this.isRead,
        isStarred = this.isStarred,
        isHooked = this.isHooked,
        folderType = this.folderType,
        sender = this.sender,
        recipients = this.recipients,
        cc = this.cc,
        bcc = this.bcc,
        attachments = this.attachments
    )
}