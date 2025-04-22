package com.example.passwordmanager.presentation.main

import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
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
import java.util.Locale

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val viewModel: MainViewModel by viewModels()
    private lateinit var navController: NavController
    private var isBackPressedOnce = false
    private val handler = Handler(Looper.getMainLooper())
    private var lockRunnable: Runnable? = null
    private var lastPausedTime: Long = 0L

    override fun onCreate(savedInstanceState: Bundle?) {
        setupTheme()
        loadLocale()
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupStatusBar()
        setupToolbar()
        setupNavigation()
        observeNavigation()
        setupAutoLock()
        checkLockOnStart()
    }

    private fun setupTheme() {
        val theme = ThemeManager.getCurrentTheme(this)
        Log.d("MainActivity", "Applying theme: $theme")
        setTheme(ThemeManager.getThemeStyle(theme))
    }

    private fun loadLocale() {
        val sharedPreferences = getSharedPreferences("settings_prefs", MODE_PRIVATE)
        val language = sharedPreferences.getString("language", "tr") ?: "tr"
        Log.d("MainActivity", "Loading language: $language")

        val locale = Locale(language)
        Locale.setDefault(locale)

        val config = Configuration(resources.configuration)
        config.setLocale(locale)
        resources.updateConfiguration(config, resources.displayMetrics)
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
                R.id.nav_back -> {
                    handleNav()
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
                R.id.categoryListFragment -> {
                    binding.bottomNavigation.selectedItemId = R.id.nav_categories
                    restartLockTimer()
                }
                R.id.passwordListFragment -> {
                    binding.bottomNavigation.selectedItemId = R.id.nav_passwords
                    restartLockTimer()
                }
                R.id.settingsFragment -> {
                    binding.bottomNavigation.selectedItemId = R.id.nav_settings
                    restartLockTimer()
                }
                else -> binding.bottomNavigation.selectedItemId = 0
            }
            Log.d("MainActivity", "Navigated to destination: ${destination.label}")
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

    private fun setupAutoLock() {
        val sharedPreferences = getSharedPreferences("settings_prefs", MODE_PRIVATE)
        startLockTimer(sharedPreferences)

        // Otomatik kilit süresinde değişiklik olduğunda zamanlayıcıyı güncelle
        sharedPreferences.registerOnSharedPreferenceChangeListener { prefs, key ->
            if (key == "auto_lock_time") {
                Log.d("MainActivity", "Auto lock time changed, restarting timer")
                lockRunnable?.let { handler.removeCallbacks(it) }
                startLockTimer(prefs)
            }
        }
    }

    private fun startLockTimer(sharedPreferences: android.content.SharedPreferences) {
        val lockTime = sharedPreferences.getInt("auto_lock_time", -1)
        Log.d("MainActivity", "Starting lock timer with time: $lockTime seconds")

        if (lockTime > 0) {
            lockRunnable?.let { handler.removeCallbacks(it) } // Eski zamanlayıcıyı iptal et
            lockRunnable = Runnable {
                Log.d("MainActivity", "Lock timer expired, navigating to LoginFragment")
                if (navController.currentDestination?.id != R.id.loginFragment) {
                    try {
                        navController.navigate(R.id.action_global_loginFragment)
                        Log.d("MainActivity", "Navigation to LoginFragment successful")
                        sharedPreferences.edit().putBoolean("should_lock", false).apply()
                    } catch (e: Exception) {
                        Log.e("MainActivity", "Error navigating to LoginFragment: ${e.message}")
                    }
                } else {
                    Log.d("MainActivity", "Already on LoginFragment, skipping navigation")
                }
            }
            Log.d("MainActivity", "Scheduling lock in $lockTime seconds")
            handler.postDelayed(lockRunnable!!, lockTime * 1000L)
        } else {
            Log.d("MainActivity", "Auto lock disabled (time: $lockTime)")
            lockRunnable?.let { handler.removeCallbacks(it) }
            lockRunnable = null
        }
    }

    private fun restartLockTimer() {
        val sharedPreferences = getSharedPreferences("settings_prefs", MODE_PRIVATE)
        Log.d("MainActivity", "Restarting lock timer after navigation")
        startLockTimer(sharedPreferences)
    }

    private fun checkLockOnStart() {
        val sharedPreferences = getSharedPreferences("settings_prefs", MODE_PRIVATE)
        val shouldLock = sharedPreferences.getBoolean("should_lock", false)
        Log.d("MainActivity", "Checking lock on start, shouldLock: $shouldLock")
        if (shouldLock && navController.currentDestination?.id != R.id.loginFragment) {
            Log.d("MainActivity", "App requires lock, navigating to LoginFragment")
            try {
                navController.navigate(R.id.action_global_loginFragment)
                sharedPreferences.edit().putBoolean("should_lock", false).apply()
            } catch (e: Exception) {
                Log.e("MainActivity", "Error navigating to LoginFragment on start: ${e.message}")
            }
        }
    }

    override fun onPause() {
        super.onPause()
        val sharedPreferences = getSharedPreferences("settings_prefs", MODE_PRIVATE)
        val lockTime = sharedPreferences.getInt("auto_lock_time", -1)
        if (lockTime > 0) {
            Log.d("MainActivity", "App paused, marking as requiring lock, recording pause time")
            lastPausedTime = System.currentTimeMillis()
            sharedPreferences.edit().putBoolean("should_lock", true).apply()
        } else {
            Log.d("MainActivity", "App paused, auto lock disabled, no action taken")
        }
    }

    override fun onResume() {
        super.onResume()
        val sharedPreferences = getSharedPreferences("settings_prefs", MODE_PRIVATE)
        val lockTime = sharedPreferences.getInt("auto_lock_time", -1)
        Log.d("MainActivity", "App resumed, checking background lock status")

        if (lockTime > 0 && lastPausedTime > 0) {
            val timeInBackground = System.currentTimeMillis() - lastPausedTime
            Log.d("MainActivity", "Time in background: ${timeInBackground / 1000} seconds")
            if (timeInBackground >= lockTime * 1000L) {
                Log.d("MainActivity", "Lock time exceeded in background, navigating to LoginFragment")
                if (navController.currentDestination?.id != R.id.loginFragment) {
                    try {
                        navController.navigate(R.id.action_global_loginFragment)
                        sharedPreferences.edit().putBoolean("should_lock", false).apply()
                        Log.d("MainActivity", "Navigation to LoginFragment successful")
                    } catch (e: Exception) {
                        Log.e("MainActivity", "Error navigating to LoginFragment: ${e.message}")
                    }
                }
                lastPausedTime = 0L
                return // Zamanlayıcıyı tekrar başlatmaya gerek yok, kilit ekranına gidildi
            }
        }

        Log.d("MainActivity", "App resumed, restarting lock timer")
        startLockTimer(sharedPreferences)
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d("MainActivity", "App destroyed, cleaning up lock runnable")
        lockRunnable?.let {
            handler.removeCallbacks(it)
            lockRunnable = null
        }
    }

    override fun onBackPressed() {
        handleNav()
        super.onBackPressed()
    }

    private fun handleNav(){
        val currentDestination = navController.currentDestination?.id
        when (currentDestination) {
            R.id.loginFragment -> finish()
            R.id.categoryListFragment -> {
                if (navController.previousBackStackEntry == null) {
                    if (!isBackPressedOnce) {
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