package com.emad.domain.usecase.email

import com.emad.domain.repository.EmailRepository
import com.emad.domain.util.NetworkResult
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ToggleHookedUseCase @Inject constructor(
    private val repository: EmailRepository
) {
    /**
     * @param emailId The ID of the email to update
     * @param isHooked True if this is a Phishing/Dangerous email, False if Safe
     */
    operator fun invoke(emailId: String, isHooked: Boolean): Flow<NetworkResult<Unit>> {
        return repository.toggleHooked(emailId, isHooked)
    }
}