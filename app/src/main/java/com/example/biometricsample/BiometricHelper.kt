package com.example.biometricsample

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_STRONG
import androidx.biometric.BiometricManager.Authenticators.DEVICE_CREDENTIAL
import androidx.biometric.BiometricPrompt
import androidx.core.app.ActivityCompat.startActivityForResult
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import java.util.concurrent.Executor

object BiometricHelper {

    private var executor: Executor? = null
    private var biometricPrompt: BiometricPrompt? = null

    private fun initBio(context: Context) {
        executor = ContextCompat.getMainExecutor(context)
        biometricPrompt = BiometricPrompt(context as FragmentActivity, executor!!,
            object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationError(
                    errorCode: Int,
                    errString: CharSequence
                ) {
                    super.onAuthenticationError(errorCode, errString)
                    Toast.makeText(
                        context.applicationContext,
                        "Authentication error: $errString", Toast.LENGTH_SHORT
                    )
                        .show()
                    Log.e("BioHelper: ", errString.toString())
                }

                override fun onAuthenticationSucceeded(
                    result: BiometricPrompt.AuthenticationResult
                ) {
                    super.onAuthenticationSucceeded(result)
                    Toast.makeText(
                        context.applicationContext,
                        "Authentication succeeded!", Toast.LENGTH_SHORT
                    )
                        .show()
                    Log.e("BioHelper: ", result.authenticationType.toString())
                }

                override fun onAuthenticationFailed() {
                    super.onAuthenticationFailed()
                    Toast.makeText(
                        context.applicationContext, "Authentication failed",
                        Toast.LENGTH_SHORT
                    )
                        .show()
                }
            })
    }

    fun showBiometricPrompt(context: Context) {
        initBio(context)
        val promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle("Biometric login for my app")
            .setSubtitle("Log in using your biometric credential")
            .setAllowedAuthenticators(BIOMETRIC_STRONG or DEVICE_CREDENTIAL)
            .setConfirmationRequired(false)
            .build()

        biometricPrompt?.authenticate(promptInfo)
    }

    fun checkBiometricSupport(context: Context, isSupported: (Boolean) -> Unit) {
        val biometricManager = BiometricManager.from(context)
        when (biometricManager.canAuthenticate(BIOMETRIC_STRONG or DEVICE_CREDENTIAL)) {
            BiometricManager.BIOMETRIC_SUCCESS -> {
                Log.e("MY_APP_TAG", "App can authenticate using biometrics.")
                isSupported(true)
            }
            BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE -> {
                Log.e("MY_APP_TAG", "No biometric features available on this device.")
                isSupported(false)
            }
            BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE -> {
                Log.e("MY_APP_TAG", "Biometric features are currently unavailable.")
                isSupported(false)
            }
            BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED -> {
                // Prompts the user to create credentials that your app accepts.
                val enrollIntent = Intent(Settings.ACTION_BIOMETRIC_ENROLL).apply {
                    putExtra(Settings.EXTRA_BIOMETRIC_AUTHENTICATORS_ALLOWED,
                        BIOMETRIC_STRONG or DEVICE_CREDENTIAL)
                }
                startActivityForResult(context as Activity, enrollIntent, 100, null)
            }
            BiometricManager.BIOMETRIC_ERROR_SECURITY_UPDATE_REQUIRED -> {
                TODO()
            }
            BiometricManager.BIOMETRIC_ERROR_UNSUPPORTED -> {
                TODO()
            }
            BiometricManager.BIOMETRIC_STATUS_UNKNOWN -> {
                TODO()
            }
        }
    }

}