package com.emad.domain.usecase.auth

import com.emad.domain.model.User
import com.emad.domain.repository.AuthRepository
import com.emad.domain.util.NetworkResult
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class LoginWithGoogleUseCase @Inject constructor(
    private val repository: AuthRepository
) {
    // The "invoke" operator allows us to call the class like a function:
    // loginUseCase("token") instead of loginUseCase.execute("token")
    operator fun invoke(idToken: String): Flow<NetworkResult<User>> {
        return repository.loginWithGoogle(idToken)
    }
}