package com.example.passwordmanager.domain.usecase.password

import com.example.passwordmanager.domain.model.Password
import com.example.passwordmanager.domain.repository.PasswordRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class SearchPasswordsUseCase @Inject constructor(
    private val repository: PasswordRepository
) {
    operator fun invoke(query: String): Flow<List<Password>> = repository.searchPasswords(query)
}