package com.deepak.periodsaathi.auth

import android.app.Activity
import android.content.Context
import android.util.Log
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.GetCredentialResponse
import androidx.credentials.exceptions.GetCredentialCancellationException
import androidx.credentials.exceptions.GetCredentialException
import androidx.credentials.exceptions.GetCredentialInterruptedException
import androidx.credentials.exceptions.NoCredentialException
import com.deepak.periodsaathi.BuildConfig
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.security.MessageDigest
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

data class CredentialSignInResult(
    val success: Boolean,
    val idToken: String? = null,
    val displayName: String? = null,
    val email: String? = null,
    val photoUrl: String? = null,
    val errorMessage: String? = null
)

@Singleton
class CredentialManagerHelper @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val credentialManager: CredentialManager by lazy {
        CredentialManager.create(context)
    }

    suspend fun signInWithGoogle(activityContext: Context): CredentialSignInResult {
        return withContext(Dispatchers.Main) {
            try {
                val nonce = generateNonce()
                val googleIdOption = GetGoogleIdOption.Builder()
                    .setFilterByAuthorizedAccounts(false)
                    .setServerClientId(BuildConfig.GOOGLE_WEB_CLIENT_ID)
                    .setAutoSelectEnabled(false)
                    .setNonce(nonce)
                    .build()

                val request = GetCredentialRequest.Builder()
                    .addCredentialOption(googleIdOption)
                    .build()

                val activity = activityContext.findActivity()
                    ?: return@withContext CredentialSignInResult(
                        success = false,
                        errorMessage = "Unable to resolve Activity context for Google Sign-In"
                    )

                val result = credentialManager.getCredential(
                    request = request,
                    context = activity
                )
                handleCredentialResult(result)
            } catch (e: GetCredentialCancellationException) {
                CredentialSignInResult(success = false, errorMessage = "Sign-in cancelled")
            } catch (e: GetCredentialInterruptedException) {
                CredentialSignInResult(
                    success = false,
                    errorMessage = "Sign-in interrupted. Please try again."
                )
            } catch (e: NoCredentialException) {
                CredentialSignInResult(
                    success = false,
                    errorMessage = "No Google account found. Please add a Google account in Settings."
                )
            } catch (e: GetCredentialException) {
                Log.e("CredentialManager", "Credential error type=${e.type} msg=${e.message} cause=${e.cause}")
                val friendlyMsg = when {
                    e.type.contains("TYPE_USER_CANCELED", ignoreCase = true) ->
                        "Sign-in was cancelled."
                    e.type.contains("TYPE_NO_CREDENTIAL", ignoreCase = true) ->
                        "No Google account found on this device. Please add one in Settings."
                    e.message?.contains("10:", ignoreCase = true) == true ->
                        "Google Sign-In is not configured. Ask the developer to register the app SHA-1 fingerprint."
                    e.message?.contains("developer_error", ignoreCase = true) == true ->
                        "Configuration error. Please check the Google Cloud Console OAuth setup."
                    e.message?.contains("NETWORK_ERROR", ignoreCase = true) == true ->
                        "No internet connection. Please check your network and try again."
                    else ->
                        "Google Sign-In failed. Please try again. (${e.type.takeLast(40)})"
                }
                CredentialSignInResult(success = false, errorMessage = friendlyMsg)
            } catch (e: Exception) {
                Log.e("CredentialManager", "Unexpected sign-in error", e)
                CredentialSignInResult(
                    success = false,
                    errorMessage = "An unexpected error occurred. Please try again."
                )
            }
        }
    }

    private fun handleCredentialResult(result: GetCredentialResponse): CredentialSignInResult {
        return when (val credential = result.credential) {
            is CustomCredential -> {
                if (credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                    try {
                        val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)
                        CredentialSignInResult(
                            success = true,
                            idToken = googleIdTokenCredential.idToken,
                            displayName = googleIdTokenCredential.displayName,
                            email = googleIdTokenCredential.id,
                            photoUrl = googleIdTokenCredential.profilePictureUri?.toString()
                        )
                    } catch (e: Exception) {
                        Log.e("CredentialManager", "Failed to parse GoogleIdTokenCredential", e)
                        CredentialSignInResult(
                            success = false,
                            errorMessage = "Failed to parse Google credentials. Please try again."
                        )
                    }
                } else {
                    CredentialSignInResult(
                        success = false,
                        errorMessage = "Unexpected credential type: ${credential.type}"
                    )
                }
            }
            else -> CredentialSignInResult(
                success = false,
                errorMessage = "Unsupported credential type"
            )
        }
    }

    private fun generateNonce(): String {
        val raw = UUID.randomUUID().toString()
        val digest = MessageDigest.getInstance("SHA-256")
        val hashBytes = digest.digest(raw.toByteArray())
        return hashBytes.joinToString("") { "%02x".format(it) }
    }

    private fun Context.findActivity(): Activity? {
        var context = this
        while (context is android.content.ContextWrapper) {
            if (context is Activity) return context
            context = context.baseContext
        }
        return null
    }
}
