package com.example.bengkelku.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.bengkelku.data.local.entity.Servis
import kotlinx.coroutines.flow.Flow

@Dao
interface ServisDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(servis: Servis)
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAll(servisList: List<Servis>)

    @Update
    suspend fun update(servis: Servis)

    @Delete
    suspend fun delete(servis: Servis)
    
    @Query("DELETE FROM servis WHERE id = :id")
    suspend fun deleteById(id: Int)

    @Query("SELECT * FROM servis WHERE id = :id")
    suspend fun getById(id: Int): Servis?

    // Untuk Admin
    @Query("""
        SELECT * FROM servis
        ORDER BY namaServis ASC
    """)
    fun getAllServis(): Flow<List<Servis>>

    // Untuk Pelanggan
    @Query("""
        SELECT * FROM servis
        WHERE isActive = 1
        ORDER BY namaServis ASC
    """)
    fun getServisAktif(): Flow<List<Servis>>
}
