package com.example.passwordmanager.presentation.main

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.activity.viewModels
import androidx.core.view.WindowCompat
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.example.passwordmanager.R
import com.example.passwordmanager.common.utils.ThemeManager
import com.example.passwordmanager.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val viewModel: MainViewModel by viewModels()
    private lateinit var navController: NavController
    private var isBackPressedOnce = false

    override fun onCreate(savedInstanceState: Bundle?) {
        setupTheme()
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupStatusBar()
        setupToolbar()
        setupNavigation()
        observeNavigation()
    }

    private fun setupTheme() {
        val theme = ThemeManager.getCurrentTheme(this)
        Log.d("MainActivity", "Applying theme: $theme")
        setTheme(ThemeManager.getThemeStyle(theme))
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
    }

    private fun setupNavigation() {
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController

        binding.bottomNavigation.setupWithNavController(navController)

        binding.bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_categories -> {
                    viewModel.onNavigationSelected(item.itemId)
                    true
                }
                R.id.nav_passwords -> {
                    viewModel.onNavigationSelected(item.itemId)
                    true
                }
                R.id.nav_settings -> {
                    viewModel.onNavigationSelected(item.itemId)
                    true
                }
                else -> false
            }
        }

        navController.addOnDestinationChangedListener { _, destination, _ ->
            binding.bottomNavigation.visibility = when (destination.id) {
                R.id.loginFragment -> View.GONE
                else -> View.VISIBLE
            }
            when (destination.id) {
                R.id.categoryListFragment -> binding.bottomNavigation.selectedItemId = R.id.nav_categories
                R.id.passwordListFragment -> binding.bottomNavigation.selectedItemId = R.id.nav_passwords
                R.id.settingsFragment -> binding.bottomNavigation.selectedItemId = R.id.nav_settings
                else -> binding.bottomNavigation.selectedItemId = 0
            }
        }
    }

    private fun observeNavigation() {
        lifecycleScope.launch {
            viewModel.selectedNavigation.collect { itemId ->
                if (navController.currentDestination?.id == R.id.loginFragment) {
                    return@collect
                }
                when (itemId) {
                    R.id.nav_categories -> {
                        if (navController.currentDestination?.id != R.id.categoryListFragment) {
                            navController.navigate(R.id.categoryListFragment)
                        }
                    }
                    R.id.nav_passwords -> {
                        if (navController.currentDestination?.id != R.id.passwordListFragment) {
                            val bundle = Bundle().apply { putLong("categoryId", 1L) }
                            navController.navigate(R.id.passwordListFragment, bundle)
                        }
                    }
                    R.id.nav_settings -> {
                        if (navController.currentDestination?.id != R.id.settingsFragment) {
                            navController.navigate(R.id.settingsFragment)
                        }
                    }
                }
            }
        }
    }

    private fun setupStatusBar() {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        val theme = ThemeManager.getCurrentTheme(this)
        val themeColors = ThemeManager.getThemeColors(this, theme)
        Log.d("MainActivity", "Setting status bar color for theme: $theme")

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            window.statusBarColor = themeColors.statusBarColor
            window.decorView.systemUiVisibility = 0
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.statusBarColor = themeColors.statusBarColor
        }
    }

    override fun onBackPressed() {
        val currentDestination = navController.currentDestination?.id
        when (currentDestination) {
            R.id.loginFragment -> finish()
            R.id.categoryListFragment -> {
                if (navController.previousBackStackEntry == null) {
                    if (!isBackPressedOnce) {
                        Toast.makeText(this, "Çıkmak için tekrar basın", Toast.LENGTH_SHORT).show()
                        isBackPressedOnce = true
                        lifecycleScope.launch {
                            delay(2000)
                            isBackPressedOnce = false
                        }
                    } else {
                        finish()
                    }
                } else {
                    navController.popBackStack()
                }
            }
            R.id.passwordListFragment, R.id.settingsFragment -> {
                if (navController.previousBackStackEntry == null) {
                    finish()
                } else {
                    navController.popBackStack()
                }
            }
            else -> super.onBackPressed()
        }
    }
}