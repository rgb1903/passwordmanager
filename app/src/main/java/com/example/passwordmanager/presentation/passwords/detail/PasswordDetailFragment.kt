package com.example.passwordmanager.presentation.password.detail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.passwordmanager.common.utils.EncryptionHelper
import com.example.passwordmanager.databinding.FragmentPasswordDetailBinding
import com.example.passwordmanager.domain.model.Category
import com.example.passwordmanager.domain.model.Password
import com.google.android.material.textfield.MaterialAutoCompleteTextView
import dagger.hilt.android.AndroidEntryPoint
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

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPasswordDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupCategoryDropdown()
        setupPassword()
        setupButtons()
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

            if (title.isEmpty() || username.isEmpty() || passwordText.isEmpty() || category == null) {
                Toast.makeText(context, "Please fill all fields and select a category", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val encryptedPassword = encryptionHelper.encrypt(passwordText)
            if (args.password != null) {
                // Mevcut parolayı güncelle
                val updatedPassword = args.password!!.copy(
                    title = title,
                    username = username,
                    password = encryptedPassword,
                    categoryId = category.id
                )
                viewModel.updatePassword(updatedPassword)
            } else {
                // Yeni parola ekle
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}