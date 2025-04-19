package com.example.passwordmanager.presentation.categories

import com.example.passwordmanager.domain.model.Category

data class CategoryUiState(
    val categories: List<Category> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)