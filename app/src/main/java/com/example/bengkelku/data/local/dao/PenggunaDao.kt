package com.example.bengkelku.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.bengkelku.data.local.entity.Pengguna
import kotlinx.coroutines.flow.Flow

@Dao
interface PenggunaDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(pengguna: Pengguna)
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAll(penggunaList: List<Pengguna>)

    @Query("SELECT * FROM pengguna WHERE id = :id")
    suspend fun getById(id: Int): Pengguna?

    @Query("SELECT * FROM pengguna WHERE username = :username LIMIT 1")
    suspend fun getByUsername(username: String): Pengguna?
    
    @Query("SELECT * FROM pengguna")
    fun getAll(): Flow<List<Pengguna>>

    @Query("DELETE FROM pengguna WHERE id = :id")
    suspend fun deleteById(id: Int)
}
