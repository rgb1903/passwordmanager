package com.example.passwordmanager.presentation.main

import com.example.passwordmanager.R
import com.example.passwordmanager.common.base.BaseViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

class MainViewModel @Inject constructor() : BaseViewModel() {


    private val _selectedNavigation = MutableStateFlow<Int>(R.id.nav_passwords)
    val selectedNavigation: StateFlow<Int> = _selectedNavigation.asStateFlow()

    fun onNavigationSelected(itemId: Int) {
        _selectedNavigation.value = itemId
    }
}