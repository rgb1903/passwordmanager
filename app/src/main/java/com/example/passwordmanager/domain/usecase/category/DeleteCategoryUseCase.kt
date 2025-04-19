package com.example.passwordmanager.domain.usecase.category

import com.example.passwordmanager.domain.model.Category
import com.example.passwordmanager.domain.repository.CategoryRepository
import javax.inject.Inject

class DeleteCategoryUseCase @Inject constructor(
    private val repository: CategoryRepository
) {
    suspend operator fun invoke(category: Category) = repository.deleteCategory(category)
}