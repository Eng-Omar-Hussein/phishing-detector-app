package com.emad.domain.usecase.email

import com.emad.domain.model.Email
import com.emad.domain.model.FolderType
import com.emad.domain.repository.EmailRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetEmailsUseCase @Inject constructor(
    private val repository: EmailRepository
) {
    operator fun invoke(folder: FolderType): Flow<List<Email>> {
        return repository.getEmails(folder)
    }
}