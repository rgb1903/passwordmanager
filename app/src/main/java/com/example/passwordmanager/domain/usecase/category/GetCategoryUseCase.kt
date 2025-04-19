package com.example.passwordmanager.domain.usecase.category

import com.example.passwordmanager.domain.model.Category
import com.example.passwordmanager.domain.repository.CategoryRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetCategoriesUseCase @Inject constructor(
    private val repository: CategoryRepository
) {
    operator fun invoke(): Flow<List<Category>> = repository.getAllCategories()
}
