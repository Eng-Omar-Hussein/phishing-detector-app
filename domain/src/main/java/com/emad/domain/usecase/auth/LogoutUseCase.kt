package com.emad.domain.usecase.auth

import com.emad.domain.repository.AuthRepository
import com.emad.domain.util.NetworkResult
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class LogoutUseCase @Inject constructor(
    private val repository: AuthRepository
) {
    operator fun invoke(): Flow<NetworkResult<Unit>> {
        return repository.logout()
    }
}