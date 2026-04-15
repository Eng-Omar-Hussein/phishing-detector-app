package com.emad.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.emad.data.local.entity.EmailEntity
import com.emad.domain.model.FolderType
import kotlinx.coroutines.flow.Flow

@Dao
interface EmailDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEmails(emails: List<EmailEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEmail(email: EmailEntity)

    @Query("SELECT * FROM emails_table WHERE folderType = :folder ORDER BY timestamp DESC")
    fun getEmailsByFolder(folder: FolderType): Flow<List<EmailEntity>>

    @Query("SELECT * FROM emails_table WHERE emailId = :id")
    fun getEmailById(id: String): Flow<EmailEntity?>

    // ── Actions ───────────────────────────────────────────────────────────

    @Query("UPDATE emails_table SET isHooked = :isHooked WHERE emailId = :id")
    suspend fun updateHookedStatus(id: String, isHooked: Boolean)

    @Query("UPDATE emails_table SET isRead = 1 WHERE emailId = :id")
    suspend fun markAsRead(id: String)

    // NEW: needed by starEmail()
    @Query("UPDATE emails_table SET isStarred = :isStarred WHERE emailId = :id")
    suspend fun updateStarredStatus(id: String, isStarred: Boolean)

    @Query("DELETE FROM emails_table WHERE emailId = :id")
    suspend fun deleteEmail(id: String)

    @Query("DELETE FROM emails_table")
    suspend fun clearAllEmails()
}