package com.emad.phishingdetector.presentation.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.emad.domain.model.User
import com.emad.domain.usecase.auth.LoginWithGoogleUseCase
import com.emad.domain.util.NetworkResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val loginWithGoogleUseCase: LoginWithGoogleUseCase
) : ViewModel() {

    private val _loginState = MutableStateFlow<NetworkResult<User>?>(null)
    val loginState: StateFlow<NetworkResult<User>?> = _loginState

    fun loginWithGoogle(idToken: String) {
        // This calls the API, saves the token to SessionManager, and saves the User to Room DB!
        loginWithGoogleUseCase(idToken).onEach { result ->
            _loginState.value = result
        }.launchIn(viewModelScope)
    }

    fun resetLoginState() {
        _loginState.value = null
    }
}