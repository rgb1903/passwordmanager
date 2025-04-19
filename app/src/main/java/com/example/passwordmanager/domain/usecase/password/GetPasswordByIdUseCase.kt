package com.example.passwordmanager.domain.usecase.password

import com.example.passwordmanager.domain.model.Password
import com.example.passwordmanager.domain.repository.PasswordRepository
import javax.inject.Inject

class GetPasswordByIdUseCase @Inject constructor(
    private val repository: PasswordRepository
) {
    suspend operator fun invoke(id: Long): Password? = repository.getPasswordById(id)
}