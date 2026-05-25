package com.deepak.periodsaathi.ui.screens.auth

import android.content.Intent
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.deepak.periodsaathi.auth.GoogleSignInManager
import com.deepak.periodsaathi.auth.GoogleSignInResult
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
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

data class UserProfile(
    val name: String,
    val email: String,
    val photoUrl: String? = null
)

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val googleSignInManager: GoogleSignInManager
) : ViewModel() {

    private val _loginState = MutableStateFlow<LoginState>(LoginState.Idle)
    val loginState: StateFlow<LoginState> = _loginState.asStateFlow()

    private val _showEmailForm = MutableStateFlow(false)
    val showEmailForm: StateFlow<Boolean> = _showEmailForm.asStateFlow()

    private val _userProfile = MutableStateFlow<UserProfile?>(null)
    val userProfile: StateFlow<UserProfile?> = _userProfile.asStateFlow()

    init {
        if (googleSignInManager.isSignedIn()) {
            val account = googleSignInManager.getCurrentAccount()
            account?.let {
                _userProfile.value = UserProfile(
                    name = it.displayName ?: "",
                    email = it.email ?: "",
                    photoUrl = it.photoUrl?.toString()
                )
            }
        }
    }

    fun handleGoogleSignInResult(task: Task<GoogleSignInAccount>, onSuccess: () -> Unit = {}) {
        _loginState.value = LoginState.Loading
        viewModelScope.launch {
            val result = googleSignInManager.handleSignInResult(task)
            if (result.success) {
                result.account?.let { account ->
                    _userProfile.value = UserProfile(
                        name = account.displayName ?: "",
                        email = account.email ?: "",
                        photoUrl = account.photoUrl?.toString()
                    )
                    _loginState.value = LoginState.Success
                    onSuccess()
                }
            } else {
                _loginState.value = LoginState.Error(result.errorMessage ?: "Google Sign-In failed")
            }
        }
    }

    fun signInWithGoogle(): Intent {
        _loginState.value = LoginState.Loading
        return googleSignInManager.getSignInIntent()
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

    fun signOut() {
        viewModelScope.launch {
            googleSignInManager.signOut()
            _userProfile.value = null
            _loginState.value = LoginState.Idle
        }
    }
}
