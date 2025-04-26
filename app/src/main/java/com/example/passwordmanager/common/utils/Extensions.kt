package com.example.passwordmanager.common.utils

import android.content.ClipboardManager
import android.content.Context
import android.content.res.Configuration
import android.view.Gravity
import android.view.LayoutInflater
import android.widget.TextView
import android.widget.Toast
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.passwordmanager.R
import java.util.Locale

class Extensions {
    object ClipboardUtils {
        fun copyToClipboard(context: Context) {
            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            clipboard.primaryClip
        }
    }

    object ToastUtils {
        fun showCustomToast(context: Context, message: String) {
            val inflater = LayoutInflater.from(context)
            val layout = inflater.inflate(R.layout.layout_toast_custom, null, false)

            val textView = layout.findViewById<TextView>(R.id.toast_text)
            textView.text = message

            val toast = Toast(context)
            toast.duration = Toast.LENGTH_SHORT
            toast.setGravity(Gravity.CENTER or Gravity.BOTTOM, 0, 100)
            toast.view = layout
            toast.show()
        }
    }

    object BiometricUtils {
        interface BiometricCallback {
            fun onAuthenticationSucceeded()
            fun onAuthenticationError(errorMessage: String)
            fun onAuthenticationFailed()
        }

        fun showBiometricPrompt(fragment: Fragment, callback: BiometricCallback) {
            val context = fragment.requireContext()
            val executor = ContextCompat.getMainExecutor(context)
            val biometricPrompt = BiometricPrompt(
                fragment,
                executor,
                object : BiometricPrompt.AuthenticationCallback() {
                    override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                        callback.onAuthenticationSucceeded()
                    }

                    override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                        callback.onAuthenticationError(errString.toString())
                    }

                    override fun onAuthenticationFailed() {
                        callback.onAuthenticationFailed()
                    }
                })

            val promptInfo = BiometricPrompt.PromptInfo.Builder()
                .setTitle(context.getString(R.string.verify))
                .setSubtitle("context.getString(R.string.finger_print)")
                .setNegativeButtonText(context.getString(R.string.cancel))
                .build()

            biometricPrompt.authenticate(promptInfo)
        }

        fun checkBiometricAvailability(context: Context, callback: BiometricCallback) {
            val biometricManager = androidx.biometric.BiometricManager.from(context)
            when (biometricManager.canAuthenticate(
                androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_STRONG or
                        androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_WEAK
            )) {
                androidx.biometric.BiometricManager.BIOMETRIC_SUCCESS -> {
                    callback.onAuthenticationSucceeded()
                }

                androidx.biometric.BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE,
                androidx.biometric.BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE,
                androidx.biometric.BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED
                -> {
                    callback.onAuthenticationError(context.getString(R.string.not_work))
                }

                else -> {
                    callback.onAuthenticationError(context.getString(R.string.not_verify))
                }
            }
        }
    }



    object LocaleUtils {
        private const val PREFS_NAME = "settings_prefs"
        private const val KEY_LANGUAGE = "language"
        private val SUPPORTED_LANGUAGES = listOf("tr", "en", "de")

        fun updateLocale(context: Context) {
            val sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            val savedLanguage = sharedPreferences.getString(KEY_LANGUAGE, null)

            val language = if (savedLanguage != null) {
                savedLanguage
            } else {
                val deviceLanguage = Locale.getDefault().language
                if (SUPPORTED_LANGUAGES.contains(deviceLanguage)) deviceLanguage else "en"
            }

            val locale = Locale(language)
            Locale.setDefault(locale)

            val config = Configuration(context.resources.configuration)
            config.setLocale(locale)
            context.resources.updateConfiguration(config, context.resources.displayMetrics)
        }

        fun setLanguage(context: Context, languageCode: String) {
            if (SUPPORTED_LANGUAGES.contains(languageCode)) {
                val sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
                sharedPreferences.edit().putString(KEY_LANGUAGE, languageCode).apply()

                val locale = Locale(languageCode)
                Locale.setDefault(locale)

                val config = Configuration(context.resources.configuration)
                config.setLocale(locale)
                context.resources.updateConfiguration(config, context.resources.displayMetrics)
            }
        }

        fun getCurrentLanguage(context: Context): String {
            val sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            val savedLanguage = sharedPreferences.getString(KEY_LANGUAGE, null)
            return if (savedLanguage != null) {
                savedLanguage
            } else {
                val deviceLanguage = Locale.getDefault().language
                if (SUPPORTED_LANGUAGES.contains(deviceLanguage)) deviceLanguage else "en"
            }
        }
    }
}