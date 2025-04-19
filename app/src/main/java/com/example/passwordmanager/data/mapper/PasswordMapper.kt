package com.example.passwordmanager.data.mapper

import com.example.passwordmanager.common.utils.EncryptionHelper
import com.example.passwordmanager.data.local.entity.PasswordEntity
import com.example.passwordmanager.domain.model.Password
import javax.inject.Inject

class PasswordMapper @Inject constructor(
    private val encryptionHelper: EncryptionHelper
) {
    fun mapToEntity(domain: Password): PasswordEntity {
        return PasswordEntity(
            id = domain.id,
            title = domain.title,
            username = domain.username,
            encryptedPassword = encryptionHelper.encrypt(domain.password),
            categoryId = domain.categoryId,
            createdAt = domain.createdAt,
            isFavorite = domain.isFavorite
        )
    }

    fun mapToDomain(entity: PasswordEntity): Password {
        return Password(
            id = entity.id,
            title = entity.title,
            username = entity.username,
            password = encryptionHelper.decrypt(entity.encryptedPassword),
            categoryId = entity.categoryId,
            createdAt = entity.createdAt,
            isFavorite = entity.isFavorite
        )
    }
}