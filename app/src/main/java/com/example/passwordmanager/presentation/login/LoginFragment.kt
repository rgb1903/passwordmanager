package com.example.passwordmanager.presentation.login

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.passwordmanager.R
import com.example.passwordmanager.common.utils.Extensions.BiometricUtils
import com.example.passwordmanager.common.utils.Extensions.ToastUtils.showCustomToast
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class LoginFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_login, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        checkBiometricAvailability()
    }

    private fun checkBiometricAvailability() {
        BiometricUtils.checkBiometricAvailability(requireContext(), object : BiometricUtils.BiometricCallback {
            override fun onAuthenticationSucceeded() {
                showBiometricPrompt()
            }

            override fun onAuthenticationError(errorMessage: String) {
                showCustomToast(requireContext(), errorMessage)
                lifecycleScope.launch {
                    delay(2000)
                    requireActivity().finish()
                }
            }

            override fun onAuthenticationFailed() {
                showCustomToast(requireContext(), getString(R.string.unsuccess))
            }
        })
    }

    private fun showBiometricPrompt() {
        BiometricUtils.showBiometricPrompt(this, object : BiometricUtils.BiometricCallback {
            override fun onAuthenticationSucceeded() {
                findNavController().navigate(R.id.action_loginFragment_to_categoryListFragment)
            }

            override fun onAuthenticationError(errorMessage: String) {
                showCustomToast(requireContext(), errorMessage)
                requireActivity().finish()
            }

            override fun onAuthenticationFailed() {
                showCustomToast(requireContext(), getString(R.string.unsuccess))
            }
        })
    }
}