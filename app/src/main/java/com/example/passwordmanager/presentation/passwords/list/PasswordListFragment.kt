package com.example.passwordmanager.presentation.passwords.list

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.passwordmanager.R
import com.example.passwordmanager.databinding.FragmentPasswordListBinding
import com.example.passwordmanager.domain.model.Password
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class PasswordListFragment : Fragment() {

    private var _binding: FragmentPasswordListBinding? = null
    private val binding get() = _binding!!
    private val viewModel: PasswordListViewModel by viewModels()
    private lateinit var passwordAdapter: PasswordAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPasswordListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val categoryId = arguments?.getLong("categoryId", -1L) ?: -1L
        if (categoryId == -1L) {
            Toast.makeText(context, "Please select a category first", Toast.LENGTH_SHORT).show()
            findNavController().navigate(R.id.categoryListFragment)
            return
        }

        setupRecyclerView(categoryId)
        setupAddPasswordButton(categoryId)
        observeState()
        viewModel.loadPasswords(categoryId)
    }

    private fun setupRecyclerView(categoryId: Long) {
        passwordAdapter = PasswordAdapter(
            onPasswordClick = { password ->
                navigateToPasswordDetail(password, categoryId)
            },
            onDeleteClick = { password ->
                viewModel.deletePassword(password)
            }
        )

        binding.rvPasswords.apply {
            adapter = passwordAdapter
            layoutManager = LinearLayoutManager(context)
        }
    }

    private fun observeState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.state.collect { state ->
                    passwordAdapter.submitList(state.passwords)

                    binding.rvPasswords.visibility = if (state.isLoading) View.GONE else View.VISIBLE

                    state.error?.let { error ->
                        Toast.makeText(context, error, Toast.LENGTH_SHORT).show()
                        viewModel.state.value.copy(error = null)
                    }

                    state.message?.let { message ->
                        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                        viewModel.state.value.copy(message = null)
                    }
                }
            }
        }
    }
    private fun navigateToPasswordDetail(password: Password?, categoryId: Long) {
        val action = PasswordListFragmentDirections.actionPasswordListToDetail(
            password = password,
            categoryId = categoryId
        )
        findNavController().navigate(action)
    }
    private fun setupAddPasswordButton(categoryId: Long) {
        binding.fabAddPassword.setOnClickListener {
            val action = PasswordListFragmentDirections.actionPasswordListToAddPassword(
                password = null,
                categoryId = categoryId
            )
            findNavController().navigate(action)
        }
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}