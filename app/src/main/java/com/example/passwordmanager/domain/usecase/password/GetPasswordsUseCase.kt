package com.example.passwordmanager.domain.usecase.password

import com.example.passwordmanager.domain.model.Password
import com.example.passwordmanager.domain.repository.PasswordRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetPasswordsUseCase @Inject constructor(
    private val repository: PasswordRepository
) {
    operator fun invoke(): Flow<List<Password>> = repository.getAllPasswords()
}