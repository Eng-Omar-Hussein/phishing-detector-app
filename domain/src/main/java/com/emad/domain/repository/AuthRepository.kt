package com.emad.domain.repository

import com.emad.domain.model.User
import com.emad.domain.util.NetworkResult
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    // 1. Login: Takes the ID Token from Google, returns a User (wrapped in Result)
    fun loginWithGoogle(idToken: String): Flow<NetworkResult<User>>

    // 2. Logout: Clears local session and maybe calls backend logout
    fun logout(): Flow<NetworkResult<Unit>>

    // 3. Get User: Observes the current user (e.g., updates profile pic automatically)
    fun getCurrentUser(): Flow<User?>

    // 4. Check if we should go to Login Screen or Home Screen
    suspend fun isUserLoggedIn(): Boolean
}