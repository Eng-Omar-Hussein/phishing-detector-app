package com.emad.phishingdetector.presentation.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.emad.domain.model.Email
import com.emad.domain.repository.EmailRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltViewModel
class EmailDetailViewModel @Inject constructor(
    private val emailRepository: EmailRepository
) : ViewModel() {

    private val _email = MutableStateFlow<Email?>(null)
    val email: StateFlow<Email?> = _email

    fun loadEmail(emailId: String) {
        emailRepository.getEmailById(emailId)
            .onEach { _email.value = it }
            .launchIn(viewModelScope)
    }
}