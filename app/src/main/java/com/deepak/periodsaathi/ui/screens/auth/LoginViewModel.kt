package com.deepak.periodsaathi.ui.screens.auth

import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.deepak.periodsaathi.auth.CredentialManagerHelper
import com.deepak.periodsaathi.auth.GoogleSignInManager
import com.deepak.periodsaathi.data.datastore.UserPreferences
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import dagger.hilt.android.lifecycle.HiltViewModel
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
    private val googleSignInManager: GoogleSignInManager,
    private val credentialManagerHelper: CredentialManagerHelper,
    private val userPreferences: UserPreferences
) : ViewModel() {

    private val _loginState = MutableStateFlow<LoginState>(LoginState.Idle)
    val loginState: StateFlow<LoginState> = _loginState.asStateFlow()

    private val _userProfile = MutableStateFlow<UserProfile?>(null)
    val userProfile: StateFlow<UserProfile?> = _userProfile.asStateFlow()

    init {
        if (googleSignInManager.isSignedIn()) {
            googleSignInManager.getCurrentAccount()?.let {
                _userProfile.value = UserProfile(
                    name = it.displayName ?: "",
                    email = it.email ?: "",
                    photoUrl = it.photoUrl?.toString()
                )
            }
        }
    }

    /** Modern sign-in via Credential Manager — primary path, no main thread freeze */
    fun signInWithCredentialManager(
        activityContext: Context,
        onFallback: () -> Unit,
        onSuccess: () -> Unit
    ) {
        _loginState.value = LoginState.Loading
        viewModelScope.launch {
            try {
                val result = credentialManagerHelper.signInWithGoogle(activityContext)
                if (result.success) {
                    val name = result.displayName ?: ""
                    _userProfile.value = UserProfile(
                        name = name,
                        email = result.email ?: "",
                        photoUrl = result.photoUrl
                    )
                    userPreferences.setLoggedIn(name)
                    _loginState.value = LoginState.Success
                    onSuccess()
                } else {
                    val msg = result.errorMessage ?: ""
                    if (msg.contains("developer_error", ignoreCase = true) ||
                        msg.contains("not configured", ignoreCase = true) ||
                        msg.contains("DEVELOPER_ERROR", ignoreCase = true)
                    ) {
                        _loginState.value = LoginState.Error(
                            "SHA-1 fingerprint not registered. Run:\n" +
                            "cd android && ./gradlew signingReport\n" +
                            "Copy the debug SHA-1, then add it to the OAuth client ID " +
                            "in https://console.cloud.google.com/apis/credentials"
                        )
                        return@launch
                    }
                    Log.e("GoogleSignIn", "Sign in failed: ${result.errorMessage}")
                    onFallback()
                }
            } catch (e: Exception) {
                Log.e("GoogleSignIn", "Sign in failed", e)
                onFallback()
            }
        }
    }

    /** Legacy fallback via old GoogleSignIn intent (kept for compatibility) */
    fun signInWithGoogle(): Intent {
        _loginState.value = LoginState.Loading
        return googleSignInManager.getSignInIntent()
    }

    fun handleGoogleSignInResult(task: Task<GoogleSignInAccount>, onSuccess: () -> Unit = {}) {
        viewModelScope.launch {
            try {
                val result = googleSignInManager.handleSignInResult(task)
                if (result.success) {
                    result.account?.let { account ->
                        val name = account.displayName ?: ""
                        _userProfile.value = UserProfile(
                            name = name,
                            email = account.email ?: "",
                            photoUrl = account.photoUrl?.toString()
                        )
                        userPreferences.setLoggedIn(name)
                        _loginState.value = LoginState.Success
                        onSuccess()
                    } ?: run {
                        _loginState.value =
                            LoginState.Error("Sign-in succeeded but no account data returned.")
                    }
                } else {
                    _loginState.value =
                        LoginState.Error(result.errorMessage ?: "Google Sign-In failed")
                }
            } catch (e: ApiException) {
                _loginState.value = LoginState.Error(
                    if (e.statusCode == 10)
                        "Google Sign-In config error. Register the SHA-1 fingerprint in Google Cloud Console."
                    else "Sign in failed (code: ${e.statusCode})"
                )
            } catch (e: Exception) {
                _loginState.value =
                    LoginState.Error("An unexpected error occurred. Please try again.")
            }
        }
    }

    fun continueAsGuest() {
        viewModelScope.launch {
            userPreferences.setGuestMode()
            _loginState.value = LoginState.Success
        }
    }

    fun resetState() {
        _loginState.value = LoginState.Idle
    }

    fun handleRawException(e: Exception) {
        val msg = if (e is ApiException && e.statusCode == 10)
            "Google Sign-In is not configured for this build. Register the SHA-1 fingerprint."
        else "An unexpected sign-in error occurred. Please try again."
        _loginState.value = LoginState.Error(msg)
    }

    fun signOut() {
        viewModelScope.launch {
            googleSignInManager.signOut()
            _userProfile.value = null
            _loginState.value = LoginState.Idle
        }
    }
}
