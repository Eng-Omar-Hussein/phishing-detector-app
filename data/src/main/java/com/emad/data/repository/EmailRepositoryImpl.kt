package com.emad.data.repository

import com.emad.data.local.dao.EmailDao
import com.emad.data.local.entity.toEntity
import com.emad.data.remote.PythonApiService
import com.emad.domain.model.Email
import com.emad.domain.model.FolderType
import com.emad.domain.repository.EmailRepository
import com.emad.domain.util.NetworkResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class EmailRepositoryImpl @Inject constructor(
    private val api: PythonApiService,
    private val dao: EmailDao
) : EmailRepository {

    // 1. Observe DB (offline-first)
    override fun getEmails(folder: FolderType): Flow<List<Email>> {
        return dao.getEmailsByFolder(folder).map { entities ->
            entities.map { it.toDomainModel() }
        }
    }

    override fun getEmailById(emailId: String): Flow<Email?> {
        return dao.getEmailById(emailId).map { it?.toDomainModel() }
    }

    // 2. Sync from network → save to DB
    override fun syncEmails(folder: FolderType): Flow<NetworkResult<Unit>> = flow {
        emit(NetworkResult.Loading())
        try {
            val response = api.getEmails(folder.name)
            if (response.isSuccessful && response.body() != null) {
                val emailList = response.body()!!.emails
                if (emailList.isEmpty()) {
                    emit(NetworkResult.Empty())
                } else {
                    dao.insertEmails(emailList.map { it.toEntity() })
                    emit(NetworkResult.Success(Unit))
                }
            } else {
                emit(NetworkResult.Error("Server error: ${response.code()}"))
            }
        } catch (e: Exception) {
            emit(NetworkResult.Error("Network error: ${e.message}"))
        }
    }

    // 3. Toggle phishing flag (optimistic update)
    override fun toggleHooked(emailId: String, isHooked: Boolean): Flow<NetworkResult<Unit>> =
        flow {
            emit(NetworkResult.Loading())
            try {
                dao.updateHookedStatus(emailId, isHooked)
                val response = api.setHookedStatus(emailId, isHooked)
                if (response.isSuccessful) {
                    emit(NetworkResult.Success(Unit))
                } else {
                    dao.updateHookedStatus(emailId, !isHooked) // revert on failure
                    emit(NetworkResult.Error("Failed to update server"))
                }
            } catch (e: Exception) {
                emit(NetworkResult.Error(e.message ?: "Unknown error"))
            }
        }

    // 4. Mark as read
    override fun markAsRead(emailId: String): Flow<NetworkResult<Unit>> = flow {
        emit(NetworkResult.Loading())
        try {
            dao.markAsRead(emailId) // local update immediately
            val response = api.markAsRead(emailId)
            if (response.isSuccessful) {
                emit(NetworkResult.Success(Unit))
            } else {
                emit(NetworkResult.Error("Failed to mark as read: ${response.code()}"))
            }
        } catch (e: Exception) {
            emit(NetworkResult.Error(e.message ?: "Unknown error"))
        }
    }

    // 5. Delete email
    override fun deleteEmail(emailId: String): Flow<NetworkResult<Unit>> = flow {
        emit(NetworkResult.Loading())
        try {
            dao.deleteEmail(emailId) // optimistic local delete
            emit(NetworkResult.Success(Unit))
            // TODO: call api.deleteEmail(emailId) once backend endpoint is ready
        } catch (e: Exception) {
            emit(NetworkResult.Error(e.message ?: "Unknown error"))
        }
    }

    // 6. Star/unstar email (local only for now — add API call when backend supports it)
    override fun starEmail(emailId: String, isStarred: Boolean): Flow<NetworkResult<Unit>> = flow {
        emit(NetworkResult.Loading())
        try {
            dao.updateStarredStatus(emailId, isStarred)
            emit(NetworkResult.Success(Unit))
            // TODO: call api.setStarredStatus(emailId, isStarred) when backend is ready
        } catch (e: Exception) {
            emit(NetworkResult.Error(e.message ?: "Unknown error"))
        }
    }

    // 7. Analyze email for phishing (calls ML backend)
    override fun analyzeEmail(emailId: String): Flow<NetworkResult<Email>> = flow {
        emit(NetworkResult.Loading())
        try {
            val response = api.analyzeEmail(emailId)
            if (response.isSuccessful && response.body() != null) {
                val email = response.body()!!
                dao.insertEmail(email.toEntity()) // update local cache with analysis result
                emit(NetworkResult.Success(email))
            } else {
                emit(NetworkResult.Error("Analysis failed: ${response.code()}"))
            }
        } catch (e: Exception) {
            emit(NetworkResult.Error(e.message ?: "Unknown error"))
        }
    }

    // 8. Clear all emails (used on logout)
    override fun clearAllEmails(): Flow<NetworkResult<Unit>> = flow {
        emit(NetworkResult.Loading())
        try {
            dao.clearAllEmails()
            emit(NetworkResult.Success(Unit))
        } catch (e: Exception) {
            emit(NetworkResult.Error(e.message ?: "Unknown error"))
        }
    }
}