package com.example.bengkelku.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.bengkelku.data.local.entity.Pengguna

@Dao
interface PenggunaDao {

    @Insert
    suspend fun insert(pengguna: Pengguna)

    @Query("""
        SELECT * FROM pengguna
        WHERE username = :username AND password = :password
        LIMIT 1
    """)
    suspend fun login(username: String, password: String): Pengguna?

    @Query("""
        SELECT COUNT(*) FROM pengguna
        WHERE role = 'ADMIN'
    """)
    suspend fun jumlahAdmin(): Int
}
