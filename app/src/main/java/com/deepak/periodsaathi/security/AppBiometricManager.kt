package com.deepak.periodsaathi.security

import android.content.Context
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_STRONG
import androidx.biometric.BiometricManager.Authenticators.DEVICE_CREDENTIAL
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity

enum class BiometricStatus {
    Available,
    NotEnrolled,
    NotAvailable
}

class AppBiometricManager(private val context: Context) {

    fun isBiometricAvailable(): BiometricStatus {
        val biometricManager = BiometricManager.from(context)
        return when (biometricManager.canAuthenticate(BIOMETRIC_STRONG or DEVICE_CREDENTIAL)) {
            BiometricManager.BIOMETRIC_SUCCESS -> BiometricStatus.Available
            BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE,
            BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE,
            BiometricManager.BIOMETRIC_ERROR_SECURITY_UPDATE_REQUIRED,
            BiometricManager.BIOMETRIC_ERROR_UNSUPPORTED -> BiometricStatus.NotAvailable
            BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED -> BiometricStatus.NotEnrolled
            else -> BiometricStatus.NotAvailable
        }
    }

    /**
     * Launches the native biometric prompt.
     *
     * @param activity        The host FragmentActivity.
     * @param onSuccess       Called when the user is authenticated successfully.
     * @param onFailed        Called when a single biometric attempt fails (wrong finger, etc.).
     *                        The prompt stays open for retries — do NOT dismiss the screen here.
     * @param onError         Called with [errString] and [errorCode] when a terminal error occurs.
     *                        Use [errorCode] to distinguish between hardware errors (route to PIN)
     *                        and user cancellations (show retry message).
     */
    fun authenticate(
        activity: FragmentActivity,
        onSuccess: () -> Unit,
        onFailed: () -> Unit,
        onError: (errString: String, errorCode: Int) -> Unit
    ) {
        val executor = ContextCompat.getMainExecutor(context)

        val promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle("Wake up Saathi 🌸")
            .setSubtitle("Verify to access Period Saathi")
            .setAllowedAuthenticators(BIOMETRIC_STRONG or DEVICE_CREDENTIAL)
            .build()

        val biometricPrompt = BiometricPrompt(
            activity,
            executor,
            object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                    onSuccess()
                }

                override fun onAuthenticationFailed() {
                    onFailed()
                }

                override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                    onError(errString.toString(), errorCode)
                }
            }
        )

        biometricPrompt.authenticate(promptInfo)
    }

    fun canAuthenticate(): Boolean = isBiometricAvailable() == BiometricStatus.Available
}
