package com.emad.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.emad.data.local.entity.UserEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {

    // OnConflictStrategy.REPLACE means:
    // "If we try to save a user and one already exists, overwrite it."
    // This is perfect for updating the profile picture or name.
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: UserEntity)

    // We use Flow so the UI updates automatically if the user changes.
    // LIMIT 1 because we only have one logged-in user.
    @Query("SELECT * FROM user_table LIMIT 1")
    fun getCurrentUser(): Flow<UserEntity?>

    // Called when logging out
    @Query("DELETE FROM user_table")
    suspend fun clearUser()
}