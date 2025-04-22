package com.example.passwordmanager.common.utils

import android.content.Context
import androidx.core.content.ContextCompat
import com.example.passwordmanager.R

object ThemeManager {
    // Tema türleri
    const val THEME_BLUE = "blue"
    const val THEME_GREEN = "green"
    const val THEME_PURPLE = "purple"

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

    // Tema stilini döndüren fonksiyon
    fun getThemeStyle(theme: String): Int {
        return when (theme) {
            THEME_BLUE -> R.style.Theme_PasswordManager_Blue
            THEME_GREEN -> R.style.Theme_PasswordManager_Green
            THEME_PURPLE -> R.style.Theme_PasswordManager_Purple
            else -> R.style.Theme_PasswordManager_Blue
        }
    }

    // Tema renklerini tutan veri sınıfı
    data class ThemeColors(
        val backgroundColor: Int,
        val statusBarColor: Int,
        val cardBackgroundColor: Int,
        val textPrimaryColor: Int,
        val textSecondaryColor: Int,
        val accentColor: Int
    )
}