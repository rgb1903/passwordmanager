package com.example.passwordmanager.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.passwordmanager.data.local.dao.CategoryDao
import com.example.passwordmanager.data.local.dao.PasswordDao
import com.example.passwordmanager.data.local.entity.CategoryEntity
import com.example.passwordmanager.data.local.entity.PasswordEntity

@Database(
    entities = [
        PasswordEntity::class,
        CategoryEntity::class
    ],
    version = 1
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun passwordDao(): PasswordDao
    abstract fun categoryDao(): CategoryDao

    companion object {
        const val DATABASE_NAME = "password_manager.db"
    }
}