package com.example.passwordmanager.domain.repository

import com.example.passwordmanager.domain.model.Password
import kotlinx.coroutines.flow.Flow

interface PasswordRepository {
    suspend fun insertPassword(password: Password)
    suspend fun updatePassword(password: Password)
    suspend fun deletePassword(password: Password)
    suspend fun getPasswordById(id: Long): Password?
    fun getAllPasswords(): Flow<List<Password>>
    fun searchPasswords(query: String): Flow<List<Password>>
    fun getPasswordsByCategory(categoryId: Long): Flow<List<Password>>
}