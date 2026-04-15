package com.emad.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.emad.domain.model.User

@Entity(tableName = "user_table")
data class UserEntity(
    @PrimaryKey val userId: String, // This is the Google ID or Backend ID
    val name: String,
    val email: String,
    val photoUrl: String?,
    val googleId: String?,
    val lastLogin: Long?,
    val createdAt: Long?
) {
    // Helper to convert this Database Entity -> Domain Model
    fun toDomainModel(): User {
        return User(
            userId = userId,
            name = name,
            email = email,
            photoUrl = photoUrl,
            googleId = googleId,
            lastLogin = lastLogin,
            createdAt = createdAt
        )
    }
}

// Helper to convert Domain Model -> Database Entity
fun User.toEntity(): UserEntity {
    return UserEntity(
        userId = this.userId,
        name = this.name,
        email = this.email,
        photoUrl = this.photoUrl,
        googleId = this.googleId,
        lastLogin = this.lastLogin,
        createdAt = this.createdAt
    )
}