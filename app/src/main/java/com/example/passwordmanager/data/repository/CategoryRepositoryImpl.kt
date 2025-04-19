package com.example.passwordmanager.data.repository

import com.example.passwordmanager.data.local.dao.CategoryDao
import com.example.passwordmanager.data.mapper.CategoryMapper
import com.example.passwordmanager.domain.model.Category
import com.example.passwordmanager.domain.repository.CategoryRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import kotlinx.coroutines.flow.map


class CategoryRepositoryImpl @Inject constructor(
    private val categoryDao: CategoryDao,
    private val categoryMapper: CategoryMapper
) : CategoryRepository {

    override suspend fun insertCategory(category: Category) {
        categoryDao.insertCategory(categoryMapper.mapToEntity(category))
    }

    override suspend fun updateCategory(category: Category) {
        categoryDao.updateCategory(categoryMapper.mapToEntity(category))
    }

    override suspend fun deleteCategory(category: Category) {
        categoryDao.deleteCategory(categoryMapper.mapToEntity(category))
    }

    override suspend fun getCategoryById(id: Long): Category? {
        return categoryDao.getCategoryById(id)?.let { categoryMapper.mapToDomain(it) }
    }

    override fun getAllCategories(): Flow<List<Category>> {
        return categoryDao.getAllCategories().map { entities ->
            entities.map { categoryMapper.mapToDomain(it) }
        }
    }
}