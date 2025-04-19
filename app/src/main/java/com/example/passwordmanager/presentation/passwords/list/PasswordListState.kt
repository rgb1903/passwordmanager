package com.example.passwordmanager.presentation.passwords.list

import com.example.passwordmanager.domain.model.Password

data class PasswordListState(
    val passwords: List<Password> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val message: String? = null,
    val searchQuery: String = ""
)