package com.example.passwordmanager.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "passwords")
data class PasswordEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val username: String,
    val encryptedPassword: String,
    val categoryId: Long,
    val createdAt: Long = System.currentTimeMillis(),
    val isFavorite: Boolean = false
)