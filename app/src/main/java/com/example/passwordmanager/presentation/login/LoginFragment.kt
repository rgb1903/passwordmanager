package com.example.passwordmanager.presentation.login

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.passwordmanager.R
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class LoginFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_login, container, false)

        checkBiometricAvailability()

        return view
    }

    private fun checkBiometricAvailability() {
        val biometricManager = BiometricManager.from(requireContext())
        when (biometricManager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_STRONG or BiometricManager.Authenticators.BIOMETRIC_WEAK)) {
            BiometricManager.BIOMETRIC_SUCCESS -> {
                showBiometricPrompt()
            }
            BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE,
            BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE,
            BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED -> {
                Toast.makeText(
                    requireContext(),
                    "Bu uygulama, biyometrik doğrulama desteklemeyen cihazlarda çalışmamaktadır. Lütfen cihaz ayarlarından parmak izi veya yüz tanıma ayarlayın.",
                    Toast.LENGTH_LONG
                ).show()
                // 2 saniye gecikme ile uygulamayı kapat
                lifecycleScope.launch {
                    delay(2000)
                    requireActivity().finish()
                }
            }
            else -> {
                Toast.makeText(
                    requireContext(),
                    "Biyometrik doğrulama yapılamıyor. Bu uygulama bu cihazda çalışmamaktadır.",
                    Toast.LENGTH_LONG
                ).show()
                // 2 saniye gecikme ile uygulamayı kapat
                lifecycleScope.launch {
                    delay(2000)
                    requireActivity().finish()
                }
            }
        }
    }

    private fun showBiometricPrompt() {
        val executor = ContextCompat.getMainExecutor(requireContext())
        val biometricPrompt = BiometricPrompt(this, executor, object : BiometricPrompt.AuthenticationCallback() {
            override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                // Başarılı doğrulama, CategoryListFragment'a yönlendir
                findNavController().navigate(R.id.action_loginFragment_to_categoryListFragment)
            }

            override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                Toast.makeText(requireContext(), "Doğrulama hatası: $errString", Toast.LENGTH_SHORT).show()
                // Hata durumunda uygulamayı kapat
                requireActivity().finish()
            }

            override fun onAuthenticationFailed() {
                Toast.makeText(requireContext(), "Biyometrik doğrulama başarısız", Toast.LENGTH_SHORT).show()
            }
        })

        val promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle("Kimlik Doğrulama")
            .setSubtitle("Parola yöneticisine erişmek için parmak izi veya yüz tanıma kullanın")
            .setNegativeButtonText("İptal")
            .build()

        biometricPrompt.authenticate(promptInfo)
    }
}