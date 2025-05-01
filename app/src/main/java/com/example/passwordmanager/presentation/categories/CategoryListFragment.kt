package com.example.passwordmanager.presentation.categories

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.passwordmanager.R
import com.example.passwordmanager.common.utils.ThemeManager
import com.example.passwordmanager.databinding.FragmentCategoryListBinding
import com.google.android.material.button.MaterialButton
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputEditText
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class CategoryListFragment : Fragment() {

    private var _binding: FragmentCategoryListBinding? = null
    private val binding get() = _binding!!
    private val viewModel: CategoriesViewModel by viewModels()
    private lateinit var categoryAdapter: CategoryAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCategoryListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        observeCategories()
        setupFab()
        setupThemeSwitch()
    }

    private fun setupRecyclerView() {
        categoryAdapter = CategoryAdapter { category ->
            val action = CategoryListFragmentDirections.actionCategoryListToPasswordList(category.id)
            findNavController().navigate(action)
        }
        binding.rvCategories.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = categoryAdapter
        }
    }


    private fun observeCategories() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.categories.collectLatest { categories ->
                categoryAdapter.submitList(categories)
            }
        }
    }

    private fun setupFab() {
        binding.fabAddCategory.setOnClickListener {
            showAddCategoryDialog()
        }
    }

    private fun showAddCategoryDialog() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_add_category, null)
        val etCategoryName = dialogView.findViewById<TextInputEditText>(R.id.etCategoryName)
        val btnSave = dialogView.findViewById<MaterialButton>(R.id.btnSave)
        val btnCancel = dialogView.findViewById<MaterialButton>(R.id.btnCancel)

        val dialog = MaterialAlertDialogBuilder(requireContext())
            .setView(dialogView)
            .create()

        btnSave.setOnClickListener {
            val name = etCategoryName.text.toString().trim()
            if (name.isNotEmpty()) {
                viewModel.addCategory(name)
                dialog.dismiss()
            }
        }

        btnCancel.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
    private fun setupThemeSwitch() {
        binding.switchButton.isChecked = ThemeManager.isDarkModeEnabled(requireContext())


        binding.switchButton.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                ThemeManager.saveTheme(requireContext(), ThemeManager.THEME_DARK)
                ThemeManager.saveDarkMode(requireContext(), true)
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            } else {
                val savedTheme = ThemeManager.getCurrentTheme(requireContext()).takeIf { it != ThemeManager.THEME_DARK }
                    ?: ThemeManager.THEME_BLUE
                ThemeManager.saveTheme(requireContext(), savedTheme)
                ThemeManager.saveDarkMode(requireContext(), false)
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                requireActivity().setTheme(ThemeManager.getThemeStyle(savedTheme))
            }
            requireActivity().recreate()
        }
    }
}