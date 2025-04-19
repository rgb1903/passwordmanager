package com.example.passwordmanager.common.di

import android.content.Context
import androidx.room.Room
import com.example.passwordmanager.common.utils.EncryptionHelper
import com.example.passwordmanager.data.local.dao.CategoryDao
import com.example.passwordmanager.data.local.dao.PasswordDao
import com.example.passwordmanager.data.local.database.AppDatabase
import com.example.passwordmanager.data.mapper.CategoryMapper
import com.example.passwordmanager.data.mapper.PasswordMapper
import com.example.passwordmanager.data.repository.CategoryRepositoryImpl
import com.example.passwordmanager.data.repository.PasswordRepositoryImpl
import com.example.passwordmanager.domain.repository.CategoryRepository
import com.example.passwordmanager.domain.repository.PasswordRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideDatabase(
        @ApplicationContext context: Context
    ): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            AppDatabase.DATABASE_NAME
        ).build()
    }

    @Provides
    @Singleton
    fun providePasswordDao(database: AppDatabase): PasswordDao {
        return database.passwordDao()
    }

    @Provides
    @Singleton
    fun provideCategoryDao(database: AppDatabase): CategoryDao {
        return database.categoryDao()
    }

    @Provides
    @Singleton
    fun providePasswordRepository(
        passwordDao: PasswordDao,
        passwordMapper: PasswordMapper
    ): PasswordRepository {
        return PasswordRepositoryImpl(passwordDao, passwordMapper)
    }

    @Provides
    @Singleton
    fun provideCategoryRepository(
        categoryDao: CategoryDao,
        categoryMapper: CategoryMapper
    ): CategoryRepository {
        return CategoryRepositoryImpl(categoryDao, categoryMapper)
    }

    @Provides
    @Singleton
    fun provideEncryptionHelper(
        @ApplicationContext context: Context
    ): EncryptionHelper {
        return EncryptionHelper(context)
    }
}