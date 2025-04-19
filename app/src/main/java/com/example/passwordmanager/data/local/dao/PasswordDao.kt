package com.example.passwordmanager.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.passwordmanager.data.local.entity.PasswordEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PasswordDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPassword(password: PasswordEntity)

    @Update
    suspend fun updatePassword(password: PasswordEntity)

    @Delete
    suspend fun deletePassword(password: PasswordEntity)

    @Query("SELECT * FROM passwords WHERE id = :id")
    suspend fun getPasswordById(id: Long): PasswordEntity?

    @Query("SELECT * FROM passwords ORDER BY createdAt DESC")
    fun getAllPasswords(): Flow<List<PasswordEntity>>

    @Query("SELECT * FROM passwords WHERE title LIKE '%' || :query || '%' OR username LIKE '%' || :query || '%'")
    fun searchPasswords(query: String): Flow<List<PasswordEntity>>

    @Query("SELECT * FROM passwords WHERE categoryId = :categoryId")
    fun getPasswordsByCategory(categoryId: Long): Flow<List<PasswordEntity>>
}