package com.example.passwordmanager.presentation.categories

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.passwordmanager.R
import com.example.passwordmanager.domain.model.Category
import com.example.passwordmanager.domain.repository.CategoryRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CategoriesViewModel @Inject constructor(
    private val repository: CategoryRepository
) : ViewModel() {

    val categories: StateFlow<List<Category>> = repository.getAllCategories()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _addCategoryResult = MutableStateFlow<AddCategoryResult?>(null)
    val addCategoryResult: StateFlow<AddCategoryResult?> = _addCategoryResult.asStateFlow()

    private val _deleteCategoryResult = MutableStateFlow<DeleteCategoryResult?>(null)
    val deleteCategoryResult: StateFlow<DeleteCategoryResult?> = _deleteCategoryResult.asStateFlow()

    fun addCategory(name: String, icon: Int? = null) {
        viewModelScope.launch {
            val exists = categories.value.any { it.name.equals(name, ignoreCase = true) }
            if (exists) {
                _addCategoryResult.value = AddCategoryResult.Error(R.string.error_duplicate_category)
            } else {
                val finalIcon = icon ?: R.drawable.ic_category
                repository.insertCategory(Category(name = name, icon = finalIcon))
                _addCategoryResult.value = AddCategoryResult.Success
            }
        }
    }

    fun deleteCategory(category: Category) {
        viewModelScope.launch {
            repository.deleteCategory(category)
            _deleteCategoryResult.value = DeleteCategoryResult.Success
        }
    }

    fun resetAddCategoryResult() {
        _addCategoryResult.value = null
    }

    fun resetDeleteCategoryResult() {
        _deleteCategoryResult.value = null
    }
}

sealed class AddCategoryResult {
    data object Success : AddCategoryResult()
    data class Error(val errorCode: Int) : AddCategoryResult()
}

sealed class DeleteCategoryResult {
    data object Success : DeleteCategoryResult()
    data class Error(val message: String) : DeleteCategoryResult()
}