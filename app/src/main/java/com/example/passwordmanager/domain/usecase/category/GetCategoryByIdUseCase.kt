package com.example.passwordmanager.domain.usecase.category

import com.example.passwordmanager.domain.model.Category
import com.example.passwordmanager.domain.repository.CategoryRepository
import javax.inject.Inject

class GetCategoryByIdUseCase @Inject constructor(
    private val repository: CategoryRepository
) {
    suspend operator fun invoke(id: Long): Category? = repository.getCategoryById(id)
}