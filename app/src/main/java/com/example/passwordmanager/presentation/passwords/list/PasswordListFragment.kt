package com.example.passwordmanager.presentation.passwords.list

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.passwordmanager.common.base.BaseFragment
import com.example.passwordmanager.databinding.FragmentPasswordListBinding
import com.example.passwordmanager.domain.model.Password
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class PasswordListFragment : BaseFragment() {

    private var _binding: FragmentPasswordListBinding? = null
    private val binding get() = _binding!!

    private val viewModel: PasswordListViewModel by viewModels()
    private val passwordAdapter = PasswordAdapter(
        onPasswordClick = { password -> navigateToPasswordDetail(password) },
        onDeleteClick = { password -> viewModel.deletePassword(password) }
    )

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPasswordListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun initViews() {
        setupRecyclerView()
        setupSearchView()
        setupAddButton()
    }

    override fun observeData() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.state.collect { state ->
                handleState(state)
            }
        }
    }

    private fun setupRecyclerView() {
        binding.recyclerViewPasswords.apply {
            adapter = passwordAdapter
            layoutManager = LinearLayoutManager(requireContext())
            addItemDecoration(
                DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL)
            )
        }
    }

    private fun setupSearchView() {
        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                viewModel.searchPasswords(newText.orEmpty())
                return true
            }
        })
    }

    private fun setupAddButton() {
        binding.fabAddPassword.setOnClickListener {
            navigateToPasswordDetail(null)
        }
    }

    private fun handleState(state: PasswordListState) {
        with(binding) {
            progressBar.isVisible = state.isLoading
            passwordAdapter.submitList(state.passwords)

            state.error?.let { error ->
                showError(error)
                viewModel.clearError()
            }

            state.message?.let { message ->
                showMessage(message)
                viewModel.clearMessage()
            }
        }
    }

    private fun navigateToPasswordDetail(password: Password?) {
        val action = PasswordListFragmentDirections.actionPasswordListToDetail(password)
        findNavController().navigate(action)
    }

    private fun showError(message: String) {
        Snackbar.make(binding.root, message, Snackbar.LENGTH_LONG).show()
    }

    private fun showMessage(message: String) {
        Snackbar.make(binding.root, message, Snackbar.LENGTH_SHORT).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}