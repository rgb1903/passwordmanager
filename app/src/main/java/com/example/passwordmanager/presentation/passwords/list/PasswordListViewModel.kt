package com.example.passwordmanager.presentation.passwords.list

import androidx.lifecycle.viewModelScope
import com.example.passwordmanager.common.base.BaseViewModel
import com.example.passwordmanager.domain.model.Password
import com.example.passwordmanager.domain.usecase.password.DeletePasswordUseCase
import com.example.passwordmanager.domain.usecase.password.GetPasswordsUseCase
import com.example.passwordmanager.domain.usecase.password.SearchPasswordsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PasswordListViewModel @Inject constructor(
    private val getPasswordsUseCase: GetPasswordsUseCase,
    private val deletePasswordUseCase: DeletePasswordUseCase,
    private val searchPasswordsUseCase: SearchPasswordsUseCase
) : BaseViewModel() {

    private val _state = MutableStateFlow(PasswordListState())
    val state: StateFlow<PasswordListState> = _state.asStateFlow()

    init {
        loadPasswords()
    }

    private fun loadPasswords() {
        viewModelScope.launch {
            try {
                showLoading()
                getPasswordsUseCase().collect { passwords ->
                    _state.update {
                        it.copy(
                            passwords = passwords,
                            isLoading = false
                        )
                    }
                }
            } catch (e: Exception) {
                _state.update {
                    it.copy(
                        error = e.message ?: "Şifreler yüklenirken bir hata oluştu",
                        isLoading = false
                    )
                }
            }
        }
    }

    fun searchPasswords(query: String) {
        viewModelScope.launch {
            _state.update { it.copy(searchQuery = query) }
            if (query.isBlank()) {
                loadPasswords()
                return@launch
            }

            searchPasswordsUseCase(query).collect { passwords ->
                _state.update { it.copy(passwords = passwords) }
            }
        }
    }

    fun deletePassword(password: Password) {
        viewModelScope.launch {
            try {
                deletePasswordUseCase(password)
                _state.update {
                    it.copy(message = "Şifre başarıyla silindi")
                }
            } catch (e: Exception) {
                _state.update {
                    it.copy(error = e.message ?: "Şifre silinirken bir hata oluştu")
                }
            }
        }
    }
}