package com.example.passwordmanager.common.utils

import android.content.Context
import androidx.core.content.ContextCompat
import com.example.passwordmanager.R

object ThemeManager {

    const val THEME_BLUE = "blue"
    const val THEME_GREEN = "green"
    const val THEME_PURPLE = "purple"
    const val THEME_ORANGE = "orange"
    const val THEME_GRAY = "gray"
    const val THEME_RED = "red"
    const val THEME_DARK = "dark"

    private const val PREFS_NAME = "settings_prefs"
    private const val PREF_THEME = "theme"
    private const val PREF_DARK_MODE = "dark_mode"

    fun getThemeColors(context: Context, theme: String): ThemeColors {
        return when (theme) {
            THEME_BLUE -> ThemeColors(
                backgroundColor = ContextCompat.getColor(context, R.color.background_light_blue),
                statusBarColor = ContextCompat.getColor(context, R.color.accent_blue),
                cardBackgroundColor = ContextCompat.getColor(context, R.color.card_background_blue),
                textPrimaryColor = ContextCompat.getColor(context, R.color.text_primary_blue),
                textSecondaryColor = ContextCompat.getColor(context, R.color.text_secondary_blue),
                accentColor = ContextCompat.getColor(context, R.color.accent_blue)
            )
            THEME_GREEN -> ThemeColors(
                backgroundColor = ContextCompat.getColor(context, R.color.background_light_green),
                statusBarColor = ContextCompat.getColor(context, R.color.accent_green),
                cardBackgroundColor = ContextCompat.getColor(context, R.color.card_background_green),
                textPrimaryColor = ContextCompat.getColor(context, R.color.text_primary_green),
                textSecondaryColor = ContextCompat.getColor(context, R.color.text_secondary_green),
                accentColor = ContextCompat.getColor(context, R.color.accent_green)
            )
            THEME_PURPLE -> ThemeColors(
                backgroundColor = ContextCompat.getColor(context, R.color.background_light_purple),
                statusBarColor = ContextCompat.getColor(context, R.color.accent_purple),
                cardBackgroundColor = ContextCompat.getColor(context, R.color.card_background_purple),
                textPrimaryColor = ContextCompat.getColor(context, R.color.text_primary_purple),
                textSecondaryColor = ContextCompat.getColor(context, R.color.text_secondary_purple),
                accentColor = ContextCompat.getColor(context, R.color.accent_purple)
            )
            THEME_ORANGE -> ThemeColors(
                backgroundColor = ContextCompat.getColor(context, R.color.background_light_orange),
                statusBarColor = ContextCompat.getColor(context, R.color.accent_orange),
                cardBackgroundColor = ContextCompat.getColor(context, R.color.card_background_orange),
                textPrimaryColor = ContextCompat.getColor(context, R.color.text_primary_orange),
                textSecondaryColor = ContextCompat.getColor(context, R.color.text_secondary_orange),
                accentColor = ContextCompat.getColor(context, R.color.accent_orange)
            )
            THEME_RED -> ThemeColors(
                backgroundColor = ContextCompat.getColor(context, R.color.background_light_red),
                statusBarColor = ContextCompat.getColor(context, R.color.accent_red),
                cardBackgroundColor = ContextCompat.getColor(context, R.color.card_background_red),
                textPrimaryColor = ContextCompat.getColor(context, R.color.text_primary_red),
                textSecondaryColor = ContextCompat.getColor(context, R.color.text_secondary_red),
                accentColor = ContextCompat.getColor(context, R.color.accent_red)
            )
            THEME_GRAY -> ThemeColors(
                backgroundColor = ContextCompat.getColor(context, R.color.background_light_gray),
                statusBarColor = ContextCompat.getColor(context, R.color.accent_gray),
                cardBackgroundColor = ContextCompat.getColor(context, R.color.card_background_gray),
                textPrimaryColor = ContextCompat.getColor(context, R.color.text_primary_gray),
                textSecondaryColor = ContextCompat.getColor(context, R.color.text_secondary_gray),
                accentColor = ContextCompat.getColor(context, R.color.accent_gray)
            )
            THEME_DARK -> ThemeColors(
                backgroundColor = ContextCompat.getColor(context, R.color.background_dark),
                statusBarColor = ContextCompat.getColor(context, R.color.accent_dark),
                cardBackgroundColor = ContextCompat.getColor(context, R.color.card_background_dark),
                textPrimaryColor = ContextCompat.getColor(context, R.color.text_primary_dark),
                textSecondaryColor = ContextCompat.getColor(context, R.color.text_secondary_dark),
                accentColor = ContextCompat.getColor(context, R.color.accent_dark)
            )
            else -> ThemeColors(
                backgroundColor = ContextCompat.getColor(context, R.color.background_light_blue),
                statusBarColor = ContextCompat.getColor(context, R.color.accent_blue),
                cardBackgroundColor = ContextCompat.getColor(context, R.color.card_background_blue),
                textPrimaryColor = ContextCompat.getColor(context, R.color.text_primary_blue),
                textSecondaryColor = ContextCompat.getColor(context, R.color.text_secondary_blue),
                accentColor = ContextCompat.getColor(context, R.color.accent_blue)
            )
        }
    }

    fun getThemeStyle(theme: String): Int {
        return when (theme) {
            THEME_BLUE -> R.style.Theme_PasswordManager_Blue
            THEME_GREEN -> R.style.Theme_PasswordManager_Green
            THEME_PURPLE -> R.style.Theme_PasswordManager_Purple
            THEME_ORANGE -> R.style.Theme_PasswordManager_Orange
            THEME_RED -> R.style.Theme_PasswordManager_Red
            THEME_GRAY -> R.style.Theme_PasswordManager_Gray
            THEME_DARK -> R.style.Theme_PasswordManager_Dark // themes.xml'deki karanlık tema
            else -> R.style.Theme_PasswordManager_Blue
        }
    }

    fun saveTheme(context: Context, theme: String) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().putString(PREF_THEME, theme).apply()
    }

    fun getCurrentTheme(context: Context): String {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return prefs.getString(PREF_THEME, THEME_BLUE) ?: THEME_BLUE
    }

    fun saveDarkMode(context: Context, isDarkMode: Boolean) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().putBoolean(PREF_DARK_MODE, isDarkMode).apply()
    }

    fun isDarkModeEnabled(context: Context): Boolean {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return prefs.getBoolean(PREF_DARK_MODE, false)
    }

    data class ThemeColors(
        val backgroundColor: Int,
        val statusBarColor: Int,
        val cardBackgroundColor: Int,
        val textPrimaryColor: Int,
        val textSecondaryColor: Int,
        val accentColor: Int
    )
}