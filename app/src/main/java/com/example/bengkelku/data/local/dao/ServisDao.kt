package com.example.bengkelku.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.bengkelku.data.local.entity.Servis
import kotlinx.coroutines.flow.Flow

@Dao
interface ServisDao {

    @Insert
    suspend fun insert(servis: Servis)

    @Update
    suspend fun update(servis: Servis)

    @Delete
    suspend fun delete(servis: Servis)

    // Untuk Admin
    @Query("""
        SELECT * FROM servis
        ORDER BY namaServis ASC
    """)
    fun getAllServis(): Flow<List<Servis>>

    // Untuk Pelanggan
    @Query("""
        SELECT * FROM servis
        WHERE aktif = 1
        ORDER BY namaServis ASC
    """)
    fun getServisAktif(): Flow<List<Servis>>
}
