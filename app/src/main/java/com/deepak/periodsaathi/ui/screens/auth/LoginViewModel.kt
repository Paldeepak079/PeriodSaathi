package com.deepak.periodsaathi.ui.screens.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class LoginState {
    data object Idle : LoginState()
    data object Loading : LoginState()
    data object Success : LoginState()
    data class Error(val message: String) : LoginState()
}

@HiltViewModel
class LoginViewModel @Inject constructor() : ViewModel() {

    private val _loginState = MutableStateFlow<LoginState>(LoginState.Idle)
    val loginState: StateFlow<LoginState> = _loginState.asStateFlow()

    private val _showEmailForm = MutableStateFlow(false)
    val showEmailForm: StateFlow<Boolean> = _showEmailForm.asStateFlow()

    fun signInWithGoogle() {
        _loginState.value = LoginState.Loading
        viewModelScope.launch {
            delay(1500)
            _loginState.value = LoginState.Success
        }
    }

    fun signInWithEmail(email: String, password: String) {
        if (email.isBlank() || password.isBlank()) {
            _loginState.value = LoginState.Error("Please fill in all fields")
            return
        }
        _loginState.value = LoginState.Loading
        viewModelScope.launch {
            delay(1500)
            _loginState.value = LoginState.Success
        }
    }

    fun continueAsGuest() {
        _loginState.value = LoginState.Success
    }

    fun toggleEmailForm() {
        _showEmailForm.value = !_showEmailForm.value
    }

    fun resetState() {
        _loginState.value = LoginState.Idle
    }
}
