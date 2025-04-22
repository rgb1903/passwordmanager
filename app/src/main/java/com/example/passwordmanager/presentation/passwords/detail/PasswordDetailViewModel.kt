package com.example.passwordmanager.presentation.password.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.passwordmanager.domain.model.Category
import com.example.passwordmanager.domain.model.Password
import com.example.passwordmanager.domain.repository.CategoryRepository
import com.example.passwordmanager.domain.repository.PasswordRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PasswordDetailViewModel @Inject constructor(
    private val passwordRepository: PasswordRepository,
    private val categoryRepository: CategoryRepository
) : ViewModel() {

    private val _password = MutableStateFlow<Password?>(null)
    val password: StateFlow<Password?> = _password

    val categories: StateFlow<List<Category>> = categoryRepository.getAllCategories()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _selectedCategory = MutableStateFlow<Category?>(null)
    val selectedCategory: StateFlow<Category?> = _selectedCategory

    fun setPassword(password: Password) {
        _password.value = password
    }

    fun selectCategory(category: Category) {
        _selectedCategory.value = category
    }

    fun insertPassword(password: Password) {
        viewModelScope.launch {
            passwordRepository.insertPassword(password)
        }
    }

    fun updatePassword(password: Password) {
        viewModelScope.launch {
            passwordRepository.updatePassword(password)
        }
    }

    fun deletePassword(password: Password) {
        viewModelScope.launch {
            passwordRepository.deletePassword(password)
        }
    }
}