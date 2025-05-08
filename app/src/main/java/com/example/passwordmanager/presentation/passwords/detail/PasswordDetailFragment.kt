package com.example.passwordmanager.presentation.passwords.detail

import android.os.Bundle
import android.text.method.PasswordTransformationMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.passwordmanager.R
import com.example.passwordmanager.common.utils.EncryptionHelper
import com.example.passwordmanager.common.utils.Extensions.ToastUtils.showCustomToast
import com.example.passwordmanager.common.utils.Extensions.ClipboardUtils.copyToClipboard
import com.example.passwordmanager.databinding.FragmentPasswordDetailBinding
import com.example.passwordmanager.domain.model.Password
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class PasswordDetailFragment : Fragment() {

    private var _binding: FragmentPasswordDetailBinding? = null
    private val binding get() = _binding!!
    private val viewModel: PasswordDetailViewModel by viewModels()
    private val args: PasswordDetailFragmentArgs by navArgs()

    @Inject
    lateinit var encryptionHelper: EncryptionHelper

    private lateinit var biometricPrompt: BiometricPrompt
    private lateinit var promptInfo: BiometricPrompt.PromptInfo

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPasswordDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupBiometricPrompt()
        setupCategoryDropdown()
        setupPassword()
        setupButtons()
        setupCopyAndVisibility()
    }

    private fun setupBiometricPrompt() {
        val executor = ContextCompat.getMainExecutor(requireContext())
        biometricPrompt = BiometricPrompt(this, executor, object : BiometricPrompt.AuthenticationCallback() {
            override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                super.onAuthenticationError(errorCode, errString)

                showCustomToast(requireContext(),errString.toString())
            }

            override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                super.onAuthenticationSucceeded(result)
                when(result.authenticationType) {
                    BiometricPrompt.AUTHENTICATION_RESULT_TYPE_BIOMETRIC -> {
                        handleBiometricSuccess()
                    }
                }
            }

            override fun onAuthenticationFailed() {
                super.onAuthenticationFailed()
                val fail = getString(R.string.auth_fail)
                showCustomToast(requireContext(), fail)
            }
        })

        val cancel = getString(R.string.cancel)
        val verify = getString(R.string.verify)
        promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle(verify)
            .setSubtitle("")
            .setNegativeButtonText(cancel)
            .build()
    }

    private var biometricAction: (() -> Unit)? = null

    private fun handleBiometricSuccess() {
        biometricAction?.invoke()
        biometricAction = null
    }

    private fun setupCategoryDropdown() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.categories.collectLatest { categories ->
                val adapter = ArrayAdapter(
                    requireContext(),
                    android.R.layout.simple_dropdown_item_1line,
                    categories.map { it.name }
                )
                binding.etCategory.setAdapter(adapter)

                val selectedCategory = categories.find { it.id == args.categoryId }
                selectedCategory?.let {
                    binding.etCategory.setText(it.name, false)
                    viewModel.selectCategory(it)
                }
            }
        }

        binding.etCategory.setOnItemClickListener { _, _, position, _ ->
            viewModel.categories.value.getOrNull(position)?.let { category ->
                viewModel.selectCategory(category)
            }
        }
    }

    private fun setupPassword() {
        args.password?.let { password ->
            viewModel.setPassword(password)
            binding.etTitle.setText(password.title)
            binding.etUsername.setText(password.username)
            binding.etPassword.setText(encryptionHelper.decrypt(password.password))
            binding.btnDelete.visibility = View.VISIBLE
        } ?: run {
            binding.btnDelete.visibility = View.GONE
        }
    }

    private fun setupButtons() {
        binding.btnSave.setOnClickListener {
            val title = binding.etTitle.text.toString().trim()
            val username = binding.etUsername.text.toString().trim()
            val passwordText = binding.etPassword.text.toString().trim()
            val category = viewModel.selectedCategory.value
            val fill = getString(R.string.fill_all)

            if (title.isEmpty() || username.isEmpty() || passwordText.isEmpty() || category == null) {
                showCustomToast(requireContext(),fill)
                return@setOnClickListener
            }

            val encryptedPassword = encryptionHelper.encrypt(passwordText)
            if (args.password != null) {
                val updatedPassword = args.password!!.copy(
                    title = title,
                    username = username,
                    password = encryptedPassword,
                    categoryId = category.id
                )
                viewModel.updatePassword(updatedPassword)
            } else {
                val newPassword = Password(
                    title = title,
                    username = username,
                    password = encryptedPassword,
                    categoryId = category.id
                )
                viewModel.insertPassword(newPassword)
            }
            findNavController().navigateUp()
        }

        binding.btnDelete.setOnClickListener {
            args.password?.let {
                viewModel.deletePassword(it)
                findNavController().navigateUp()
            }
        }
    }

    private fun setupCopyAndVisibility() {

        binding.tilUsername.setEndIconOnClickListener {
            val textName= getString(R.string.name_copy)
            val textEmpty = getString(R.string.name_empty)
            val username = binding.etUsername.text.toString().trim()
            if (username.isNotEmpty()) {
                copyToClipboard(requireContext())
                showCustomToast(requireContext(),"$textName: $username")

            } else {
                showCustomToast(requireContext(),textEmpty)
            }
        }

        binding.tilPassword.setEndIconOnClickListener {
            val textEmpty = getString(R.string.pass_empty)
            val password = binding.etPassword.text.toString().trim()

            biometricAction = {
                if (password.isNotEmpty()) {
                    lifecycleScope.launch {
                        binding.etPassword.transformationMethod = null
                        delay(20000)
                        binding.etPassword.transformationMethod = PasswordTransformationMethod.getInstance()
                        binding.etPassword.setSelection(binding.etPassword.text?.length ?: 0)
                    }
                } else {
                    showCustomToast(requireContext(), textEmpty)
                }
            }
            biometricPrompt.authenticate(promptInfo)
        }


    }



    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}