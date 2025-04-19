package com.example.passwordmanager.domain.repository

import com.example.passwordmanager.domain.model.Category
import kotlinx.coroutines.flow.Flow

interface CategoryRepository {
    suspend fun insertCategory(category: Category)
    suspend fun updateCategory(category: Category)
    suspend fun deleteCategory(category: Category)
    suspend fun getCategoryById(id: Long): Category?
    fun getAllCategories(): Flow<List<Category>>
}