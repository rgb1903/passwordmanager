package com.example.passwordmanager.common.base

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

abstract class BaseViewModel : ViewModel() {
    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    private val _message = MutableStateFlow<String?>(null)
    val message: StateFlow<String?> = _message.asStateFlow()

    protected fun showLoading() {
        _loading.value = true
    }

    protected fun hideLoading() {
        _loading.value = false
    }

    protected fun showError(message: String) {
        _error.value = message
    }

    fun clearError() {
        _error.value = null
    }

    protected fun showMessage(message: String) {
        _message.value = message
    }

    fun clearMessage() {
        _message.value = null
    }
}