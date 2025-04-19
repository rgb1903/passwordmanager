package com.example.passwordmanager.presentation.main

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.passwordmanager.R
import com.example.passwordmanager.databinding.ActivityMainBinding
import com.example.passwordmanager.presentation.categories.CategoryListFragment
import com.example.passwordmanager.presentation.passwords.list.PasswordListFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import androidx.fragment.app.commit

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupToolbar()
        setupNavigation()
        observeNavigation()
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
    }

    private fun setupNavigation() {
        binding.bottomNavigation.setOnItemSelectedListener { item ->
            viewModel.onNavigationSelected(item.itemId)
            true
        }
    }

    private fun observeNavigation() {
        lifecycleScope.launch {
            viewModel.selectedNavigation.collect { itemId ->
                val fragment = when (itemId) {
                    R.id.nav_passwords -> PasswordListFragment()
                    R.id.nav_categories -> CategoryListFragment()
                    else -> PasswordListFragment()
                }

                supportFragmentManager.commit {
                    replace(R.id.nav_host_fragment, fragment)
                }
            }
        }
    }
}