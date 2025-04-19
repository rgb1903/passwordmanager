package com.example.passwordmanager.presentation.categories

import com.example.passwordmanager.domain.model.Category

sealed class CategoryEvent {
    data class AddCategory(
        val name: String,
        val icon: Int? = null
    ) : CategoryEvent()

    data class UpdateCategory(val category: Category) : CategoryEvent()
    data class DeleteCategory(val category: Category) : CategoryEvent()
}