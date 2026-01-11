package com.example.bengkelku.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.bengkelku.data.local.entity.Kendaraan
import kotlinx.coroutines.flow.Flow

@Dao
interface KendaraanDao {

    @Insert
    suspend fun insert(kendaraan: Kendaraan)

    @Update
    suspend fun update(kendaraan: Kendaraan)

    @Delete
    suspend fun delete(kendaraan: Kendaraan)

    @Query("""
        SELECT * FROM kendaraan
        WHERE penggunaId = :penggunaId
        ORDER BY merk ASC
    """)
    fun getKendaraanByPengguna(penggunaId: Int): Flow<List<Kendaraan>>

    @Query("""
        SELECT * FROM kendaraan
        WHERE id = :id
        LIMIT 1
    """)
    suspend fun getById(id: Int): Kendaraan?

    // Check duplicate per customer (not global)
    @Query("""
        SELECT COUNT(*) FROM kendaraan
        WHERE penggunaId = :penggunaId AND nomorPlat = :nomorPlat
    """)
    suspend fun countByPenggunaAndNomorPlat(penggunaId: Int, nomorPlat: String): Int
}
