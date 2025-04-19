package com.example.passwordmanager.data.repository

import com.example.passwordmanager.data.local.dao.PasswordDao
import com.example.passwordmanager.data.mapper.PasswordMapper
import com.example.passwordmanager.domain.model.Password
import com.example.passwordmanager.domain.repository.PasswordRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class PasswordRepositoryImpl @Inject constructor(
    private val passwordDao: PasswordDao,
    private val passwordMapper: PasswordMapper
) : PasswordRepository {

    override suspend fun insertPassword(password: Password) {
        passwordDao.insertPassword(passwordMapper.mapToEntity(password))
    }

    override suspend fun updatePassword(password: Password) {
        passwordDao.updatePassword(passwordMapper.mapToEntity(password))
    }

    override suspend fun deletePassword(password: Password) {
        passwordDao.deletePassword(passwordMapper.mapToEntity(password))
    }

    override suspend fun getPasswordById(id: Long): Password? {
        return passwordDao.getPasswordById(id)?.let { passwordMapper.mapToDomain(it) }
    }

    override fun getAllPasswords(): Flow<List<Password>> {
        return passwordDao.getAllPasswords().map { entities ->
            entities.map { passwordMapper.mapToDomain(it) }
        }
    }

    override fun searchPasswords(query: String): Flow<List<Password>> {
        return passwordDao.searchPasswords(query).map { entities ->
            entities.map { passwordMapper.mapToDomain(it) }
        }
    }

    override fun getPasswordsByCategory(categoryId: Long): Flow<List<Password>> {
        return passwordDao.getPasswordsByCategory(categoryId).map { entities ->
            entities.map { passwordMapper.mapToDomain(it) }
        }
    }
}