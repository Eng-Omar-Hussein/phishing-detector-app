package com.emad.data.repository

import com.emad.data.local.SessionManager
import com.emad.data.local.dao.UserDao
import com.emad.data.local.entity.toEntity
import com.emad.data.remote.PythonApiService
import com.emad.data.remote.dto.AuthRequest
import com.emad.domain.model.User
import com.emad.domain.repository.AuthRepository
import com.emad.domain.util.NetworkResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val api: PythonApiService,
    private val sessionManager: SessionManager,
    private val userDao: UserDao
) : AuthRepository {

    override fun loginWithGoogle(idToken: String): Flow<NetworkResult<User>> = flow {
        emit(NetworkResult.Loading())
        try {
            val response = api.loginWithGoogle(AuthRequest(idToken))

            if (response.isSuccessful && response.body() != null) {
                val authResponse = response.body()!!

                // 1. Save Token
                sessionManager.saveAuthToken(authResponse.token)

                // 2. Save User to DB
                val user = authResponse.user
                userDao.insertUser(user.toEntity())

                // 3. Save Basic Info to Session (for Header)
                sessionManager.saveUserDetails(
                    name = user.name,
                    email = user.email,
                    googleId = user.googleId ?: ""
                )

                emit(NetworkResult.Success(user))
            } else {
                emit(NetworkResult.Error("Login failed: ${response.code()}"))
            }
        } catch (e: Exception) {
            emit(NetworkResult.Error("Network error: ${e.message}"))
        }
    }

    override fun logout(): Flow<NetworkResult<Unit>> = flow {
        sessionManager.clearSession()
        userDao.clearUser()
        emit(NetworkResult.Success(Unit))
    }

    override fun getCurrentUser(): Flow<User?> {
        return userDao.getCurrentUser().map { it?.toDomainModel() }
    }

    override suspend fun isUserLoggedIn(): Boolean {
        return !sessionManager.getAuthToken().isNullOrBlank()
    }
}