package com.emad.phishingdetector.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.emad.domain.model.FolderType
import com.emad.domain.usecase.email.GetEmailsUseCase
import com.emad.domain.usecase.email.SyncEmailsUseCase
import com.emad.domain.usecase.email.ToggleHookedUseCase
import com.emad.domain.util.NetworkResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getEmailsUseCase: GetEmailsUseCase,
    private val syncEmailsUseCase: SyncEmailsUseCase,
    private val toggleHookedUseCase: ToggleHookedUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(HomeState())
    val state: StateFlow<HomeState> = _state

    init {
        loadEmails(FolderType.INBOX)
    }

    fun loadEmails(folder: FolderType) {
        _state.value = _state.value.copy(
            currentFolder = folder,
            emails = emptyList()
        )

        getEmailsUseCase(folder).onEach { emails ->
            _state.value = _state.value.copy(emails = emails)
        }.launchIn(viewModelScope)

        syncFromNetwork(folder, isUserTriggered = false)
    }

    fun refreshEmails() {
        syncFromNetwork(
            folder = _state.value.currentFolder,
            isUserTriggered = true
        )
    }

    private fun syncFromNetwork(folder: FolderType, isUserTriggered: Boolean) {
        syncEmailsUseCase(folder).onEach { result ->
            when (result) {
                is NetworkResult.Loading -> _state.value = _state.value.copy(
                    isLoading = true,
                    isRefreshing = isUserTriggered
                )
                is NetworkResult.Success -> _state.value = _state.value.copy(
                    isLoading = false,
                    isRefreshing = false
                )
                is NetworkResult.Error -> _state.value = _state.value.copy(
                    isLoading = false,
                    isRefreshing = false,
                    errorMessage = result.message
                )
                // Empty: stop spinners — the empty state layout handles the visual
                is NetworkResult.Empty -> _state.value = _state.value.copy(
                    isLoading = false,
                    isRefreshing = false
                )
            }
        }.launchIn(viewModelScope)
    }

    fun onErrorShown() {
        _state.value = _state.value.copy(errorMessage = null)
    }

    fun onToggleHooked(emailId: String, isCurrentlyHooked: Boolean) {
        viewModelScope.launch {
            toggleHookedUseCase(emailId, !isCurrentlyHooked).collect { result ->
                when (result) {
                    is NetworkResult.Error -> _state.value = _state.value.copy(
                        errorMessage = "Failed to update phishing status"
                    )
                    is NetworkResult.Empty,
                    is NetworkResult.Loading,
                    is NetworkResult.Success -> Unit // No UI change needed
                }
            }
        }
    }
}