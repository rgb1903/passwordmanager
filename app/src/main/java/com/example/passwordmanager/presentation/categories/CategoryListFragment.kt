package com.example.passwordmanager.presentation.categories

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.passwordmanager.R
import com.example.passwordmanager.common.utils.IconPalette
import com.example.passwordmanager.common.utils.ThemeManager
import com.example.passwordmanager.databinding.DialogAddCategoryBinding
import com.example.passwordmanager.databinding.FragmentCategoryListBinding
import com.example.passwordmanager.domain.model.Category
import com.google.android.material.dialog.MaterialAlertDialogBuilder
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
        categoryAdapter = CategoryAdapter(
            onCategoryClick = { category ->
                val action = CategoryListFragmentDirections.actionCategoryListToPasswordList(category.id)
                findNavController().navigate(action)
            },
            onCategoryDelete = { category ->
                showDeleteConfirmationDialog(category)
            }
        )
        binding.rvCategories.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = categoryAdapter
            setHasFixedSize(true)
        }
    }

    private fun observeCategories() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.categories.collectLatest { categories ->
                    categoryAdapter.submitList(categories)
                }
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
        val binding = DialogAddCategoryBinding.bind(dialogView)

        // İkon paleti RecyclerView'ı kur
        var selectedIcon: Int? = null
        val iconAdapter = IconAdapter(IconPalette.ICONS) { iconResId ->
            selectedIcon = iconResId
            binding.selectedIconPreview.apply {
                setImageResource(iconResId)
                visibility = View.VISIBLE
            }
        }
        binding.rvIconPalette.apply {
            layoutManager = GridLayoutManager(context, 3)
            adapter = iconAdapter
            setHasFixedSize(true)
        }

        val dialog = MaterialAlertDialogBuilder(requireContext())
            .setView(dialogView)
            .create()

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.addCategoryResult.collect { result ->
                    when (result) {
                        is AddCategoryResult.Success -> {
                            dialog.dismiss()
                            viewModel.resetAddCategoryResult()
                        }
                        is AddCategoryResult.Error -> {
                            binding.tilCategoryName.error = getString(result.errorCode)
                        }
                        null -> {} // Boş blok kaldırılabilir, ama korudum
                    }
                }
            }
        }

        binding.btnSave.setOnClickListener {
            val name = binding.etCategoryName.text.toString().trim()
            if (name.isNotEmpty()) {
                binding.tilCategoryName.error = null
                viewModel.addCategory(name, selectedIcon)
            } else {
                binding.tilCategoryName.error = getString(R.string.fill_all)
            }
        }

        binding.btnCancel.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }

    private fun showDeleteConfirmationDialog(category: Category) {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(R.string.delete_category_title)
            .setMessage(getString(R.string.delete_category_message, category.name))
            .setPositiveButton(R.string.delete) { _, _ ->
                viewModel.deleteCategory(category)
            }
            .setNegativeButton(R.string.cancel) { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    private fun setupThemeSwitch() {
        binding.switchButton.isChecked = ThemeManager.getCurrentTheme(requireContext()) == ThemeManager.THEME_DARK

        binding.switchButton.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                val currentTheme = ThemeManager.getCurrentTheme(requireContext())
                if (currentTheme != ThemeManager.THEME_DARK) {
                    ThemeManager.savePreviousTheme(requireContext(), currentTheme)
                }
                ThemeManager.saveTheme(requireContext(), ThemeManager.THEME_DARK)
            } else {
                val previousTheme = ThemeManager.getPreviousTheme(requireContext())
                ThemeManager.saveTheme(requireContext(), previousTheme)
            }
            requireActivity().recreate()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}