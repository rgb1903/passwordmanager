package com.example.passwordmanager.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Password(
    val id: Long = 0,
    val title: String,
    val username: String,
    val password: String,
    val categoryId: Long,
    val createdAt: Long = System.currentTimeMillis(),
    val isFavorite: Boolean = false
) : Parcelable