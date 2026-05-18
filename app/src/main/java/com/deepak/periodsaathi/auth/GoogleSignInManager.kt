package com.deepak.periodsaathi.auth

import android.content.Context
import android.util.Log
import com.deepak.periodsaathi.BuildConfig
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

data class GoogleSignInResult(
    val success: Boolean,
    val account: GoogleSignInAccount? = null,
    val idToken: String? = null,
    val errorMessage: String? = null
)

@Singleton
class GoogleSignInManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val googleSignInClient: GoogleSignInClient by lazy {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(BuildConfig.GOOGLE_WEB_CLIENT_ID)
            .requestEmail()
            .requestProfile()
            .build()
        GoogleSignIn.getClient(context, gso)
    }

    fun getSignInIntent() = googleSignInClient.signInIntent

    suspend fun signOutViaIntent(launchIntent: (android.content.Intent) -> Unit) {
        googleSignInClient.signOut().addOnCompleteListener {
            Log.d("GoogleSignInManager", "Signed out successfully")
        }
    }

    suspend fun handleSignInResult(task: Task<GoogleSignInAccount>): GoogleSignInResult {
        return try {
            val account = task.getResult(ApiException::class.java)
            GoogleSignInResult(
                success = true,
                account = account,
                idToken = account.idToken
            )
        } catch (e: ApiException) {
            Log.e("GoogleSignInManager", "Google sign in failed: ${e.statusCode}", e)
            GoogleSignInResult(
                success = false,
                errorMessage = getErrorMessage(e.statusCode)
            )
        }
    }

    suspend fun signOut() {
        googleSignInClient.signOut().addOnCompleteListener {
            Log.d("GoogleSignInManager", "Signed out successfully")
        }
    }

    fun isSignedIn(): Boolean {
        val account = GoogleSignIn.getLastSignedInAccount(context)
        return account != null
    }

    fun getCurrentAccount(): GoogleSignInAccount? {
        return GoogleSignIn.getLastSignedInAccount(context)
    }

    private fun getErrorMessage(statusCode: Int): String {
        return when (statusCode) {
            12500 -> "Sign in cancelled"
            12501 -> "Sign in interrupted"
            12502 -> "No account found. Please add a Google account to your device."
            else -> "Sign in failed (code: $statusCode)"
        }
    }
}
