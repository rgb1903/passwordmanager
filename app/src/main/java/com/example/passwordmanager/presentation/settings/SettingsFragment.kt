package com.example.passwordmanager.presentation.settings

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.passwordmanager.common.utils.Extensions.LocaleUtils
import com.example.passwordmanager.databinding.FragmentSettingsBinding

class SettingsFragment : Fragment() {

    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        sharedPreferences = requireContext().getSharedPreferences("settings_prefs", Context.MODE_PRIVATE)

        setupLanguageOptions()
        setupThemeOptions()
        setupAutoLockOptions()

    }

    private fun setupLanguageOptions() {
        val currentLanguage = LocaleUtils.getCurrentLanguage(requireContext())
        Log.d("SettingsFragment", "Current language: $currentLanguage")

        updateLanguageSelection(currentLanguage)

        binding.optionTurkish.setOnClickListener {
            Log.d("SettingsFragment", "Selected language: tr")
            animateIcon(it)
            changeLanguage("tr")
        }
        binding.optionEnglish.setOnClickListener {
            Log.d("SettingsFragment", "Selected language: en")
            animateIcon(it)
            changeLanguage("en")
        }
        binding.optionGerman.setOnClickListener {
            Log.d("SettingsFragment", "Selected language: de")
            animateIcon(it)
            changeLanguage("de")
        }
    }

    private fun setupThemeOptions() {
        val currentTheme = sharedPreferences.getString("theme", "blue") ?: "blue"
        Log.d("SettingsFragment", "Current theme: $currentTheme")

        updateThemeSelection(currentTheme)

        binding.optionBlueTheme.setOnClickListener {
            Log.d("SettingsFragment", "Selected theme: blue")
            animateIcon(it)
            changeTheme("blue")
        }
        binding.optionGreenTheme.setOnClickListener {
            Log.d("SettingsFragment", "Selected theme: green")
            animateIcon(it)
            changeTheme("green")
        }
        binding.optionOrangeTheme.setOnClickListener {
            Log.d("SettingsFragment", "Selected theme: orange")
            animateIcon(it)
            changeTheme("orange")
        }
        binding.optionPurpleTheme.setOnClickListener {
            Log.d("SettingsFragment", "Selected theme: purple")
            animateIcon(it)
            changeTheme("purple")
        }
        binding.optionRedTheme.setOnClickListener {
            Log.d("SettingsFragment", "Selected theme: red")
            animateIcon(it)
            changeTheme("red")
        }
        binding.optionGrayTheme.setOnClickListener {
            Log.d("SettingsFragment", "Selected theme: gray")
            animateIcon(it)
            changeTheme("gray")
        }
    }

    private fun setupAutoLockOptions() {
        val currentAutoLock = sharedPreferences.getInt("auto_lock_time", -1) // -1: Asla
        Log.d("SettingsFragment", "Current auto lock time: $currentAutoLock")

        updateAutoLockSelection(currentAutoLock)

        binding.option20Seconds.setOnClickListener {
            Log.d("SettingsFragment", "Selected auto lock: 20 seconds")
            animateIcon(it)
            changeAutoLock(20)
        }
        binding.option30Seconds.setOnClickListener {
            Log.d("SettingsFragment", "Selected auto lock: 30 seconds")
            animateIcon(it)
            changeAutoLock(30)
        }
        binding.option45Seconds.setOnClickListener {
            Log.d("SettingsFragment", "Selected auto lock: 45 seconds")
            animateIcon(it)
            changeAutoLock(45)
        }
        binding.option60Seconds.setOnClickListener {
            Log.d("SettingsFragment", "Selected auto lock: 60 seconds")
            animateIcon(it)
            changeAutoLock(60)

        }
        binding.optionNever.setOnClickListener {
            Log.d("SettingsFragment", "Selected auto lock: Never")
            animateIcon(it)
            changeAutoLock(-1)
        }
    }

    private fun updateLanguageSelection(languageCode: String) {
        binding.checkTurkish.visibility = View.GONE
        binding.checkEnglish.visibility = View.GONE
        binding.checkGerman.visibility = View.GONE

        when (languageCode) {
            "tr" -> binding.checkTurkish.visibility = View.VISIBLE
            "en" -> binding.checkEnglish.visibility = View.VISIBLE
            "de" -> binding.checkGerman.visibility = View.VISIBLE
        }
    }

    private fun updateThemeSelection(theme: String) {
        binding.checkBlueTheme.visibility = View.GONE
        binding.checkGreenTheme.visibility = View.GONE
        binding.checkPurpleTheme.visibility = View.GONE
        binding.checkOrangeTheme.visibility = View.GONE
        binding.checkGrayTheme.visibility = View.GONE
        binding.checkRedTheme.visibility = View.GONE

        when (theme) {
            "blue" -> binding.checkBlueTheme.visibility = View.VISIBLE
            "green" -> binding.checkGreenTheme.visibility = View.VISIBLE
            "orange" -> binding.checkOrangeTheme.visibility = View.VISIBLE
            "purple" -> binding.checkPurpleTheme.visibility = View.VISIBLE
            "red" -> binding.checkRedTheme.visibility = View.VISIBLE
            "gray" -> binding.checkGrayTheme.visibility = View.VISIBLE
        }
    }

    private fun updateAutoLockSelection(lockTime: Int) {
        binding.check20Seconds.visibility = View.GONE
        binding.check30Seconds.visibility = View.GONE
        binding.check45Seconds.visibility = View.GONE
        binding.check60Seconds.visibility = View.GONE
        binding.checkNever.visibility = View.GONE

        when (lockTime) {
            20 -> binding.check20Seconds.visibility = View.VISIBLE
            30 -> binding.check30Seconds.visibility = View.VISIBLE
            45 -> binding.check45Seconds.visibility = View.VISIBLE
            60 -> binding.check60Seconds.visibility = View.VISIBLE
            -1 -> binding.checkNever.visibility = View.VISIBLE
        }
    }



    private fun changeLanguage(languageCode: String) {
        Log.d("SettingsFragment", "Changing language to: $languageCode")
        LocaleUtils.setLanguage(requireContext(), languageCode)
        updateLanguageSelection(languageCode)
        requireActivity().recreate()
    }

    private fun changeTheme(theme: String) {
        Log.d("SettingsFragment", "Changing theme to: $theme")
        val saved = sharedPreferences.edit().putString("theme", theme).commit()
        Log.d("SettingsFragment", "Theme saved to SharedPreferences: $theme, Success: $saved")

        updateThemeSelection(theme)
        requireActivity().recreate()
    }

    private fun changeAutoLock(lockTime: Int) {
        Log.d("SettingsFragment", "Changing auto lock to: $lockTime")
        val saved = sharedPreferences.edit().putInt("auto_lock_time", lockTime).commit()
        Log.d("SettingsFragment", "Auto lock saved to SharedPreferences: $lockTime, Success: $saved")

        updateAutoLockSelection(lockTime)
    }
    private fun animateIcon(view: View){
        view.animate().scaleX(1.5f).scaleY(1.5f).setDuration(100).withEndAction{
            view.scaleX = 1f
            view.scaleY = 1f
        }.start()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}