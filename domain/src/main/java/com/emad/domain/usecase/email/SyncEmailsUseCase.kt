package com.emad.domain.usecase.email

import com.emad.domain.model.FolderType
import com.emad.domain.repository.EmailRepository
import com.emad.domain.util.NetworkResult
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class SyncEmailsUseCase @Inject constructor(
    private val repository: EmailRepository
) {
    operator fun invoke(folder: FolderType): Flow<NetworkResult<Unit>> {
        return repository.syncEmails(folder)
    }
}