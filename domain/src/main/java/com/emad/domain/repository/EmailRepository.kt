package com.emad.domain.repository

import com.emad.domain.model.Email
import com.emad.domain.model.FolderType
import com.emad.domain.util.NetworkResult
import kotlinx.coroutines.flow.Flow

interface EmailRepository {

    // ── Data streams (Room, offline-first) ───────────────────────────────
    fun getEmails(folder: FolderType): Flow<List<Email>>
    fun getEmailById(emailId: String): Flow<Email?>

    // ── Network sync ──────────────────────────────────────────────────────
    fun syncEmails(folder: FolderType): Flow<NetworkResult<Unit>>

    // ── Email actions ─────────────────────────────────────────────────────
    fun markAsRead(emailId: String): Flow<NetworkResult<Unit>>
    fun deleteEmail(emailId: String): Flow<NetworkResult<Unit>>
    fun toggleHooked(emailId: String, isHooked: Boolean): Flow<NetworkResult<Unit>>
    fun starEmail(emailId: String, isStarred: Boolean): Flow<NetworkResult<Unit>>

    // ── ML / Cybersecurity analysis ───────────────────────────────────────
    fun analyzeEmail(emailId: String): Flow<NetworkResult<Email>>

    // ── Session cleanup ───────────────────────────────────────────────────
    fun clearAllEmails(): Flow<NetworkResult<Unit>>
}