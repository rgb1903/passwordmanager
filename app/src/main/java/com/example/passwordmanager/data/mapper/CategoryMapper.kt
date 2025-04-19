package com.example.passwordmanager.data.mapper

import com.example.passwordmanager.data.local.entity.CategoryEntity
import com.example.passwordmanager.domain.model.Category
import javax.inject.Inject

class CategoryMapper @Inject constructor() {
    fun mapToEntity(domain: Category): CategoryEntity {
        return CategoryEntity(
            id = domain.id,
            name = domain.name,
            icon = domain.icon
        )
    }

    fun mapToDomain(entity: CategoryEntity): Category {
        return Category(
            id = entity.id,
            name = entity.name,
            icon = entity.icon
        )
    }
}