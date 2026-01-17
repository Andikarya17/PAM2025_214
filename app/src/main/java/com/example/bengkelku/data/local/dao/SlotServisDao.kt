package com.example.bengkelku.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.bengkelku.data.local.entity.SlotServis
import kotlinx.coroutines.flow.Flow

@Dao
interface SlotServisDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(slotServis: SlotServis)
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAll(slotList: List<SlotServis>)

    @Update
    suspend fun update(slotServis: SlotServis)

    @Delete
    suspend fun delete(slotServis: SlotServis)
    
    @Query("DELETE FROM slot_servis WHERE id = :id")
    suspend fun deleteById(id: Int)

    @Query("""
        SELECT * FROM slot_servis
        ORDER BY tanggal ASC, jamMulai ASC
    """)
    fun getAllSlots(): Flow<List<SlotServis>>

    @Query("""
        SELECT * FROM slot_servis
        WHERE tanggal = :tanggal
        ORDER BY jamMulai ASC
    """)
    fun getSlotsByDate(tanggal: String): Flow<List<SlotServis>>

    @Query("SELECT * FROM slot_servis WHERE id = :slotId")
    suspend fun getSlotById(slotId: Int): SlotServis?

    @Query("""
        SELECT * FROM slot_servis
        WHERE tanggal = :tanggal
          AND id != :excludeId
          AND (
            (jamMulai < :jamSelesai AND jamSelesai > :jamMulai)
          )
    """)
    suspend fun getOverlappingSlots(
        tanggal: String,
        jamMulai: String,
        jamSelesai: String,
        excludeId: Int = 0
    ): List<SlotServis>

    @Query("""
        SELECT COUNT(*) FROM booking
        WHERE slotServisId = :slotId
    """)
    suspend fun countBookingsForSlot(slotId: Int): Int

    @Query("""
        SELECT * FROM slot_servis
        WHERE tanggal >= :today
          AND kapasitas > terpakai
          AND status = 'available'
        ORDER BY tanggal ASC, jamMulai ASC
    """)
    fun getAvailableSlots(today: String): Flow<List<SlotServis>>

    @Query("""
        UPDATE slot_servis
        SET terpakai = terpakai + 1
        WHERE id = :slotId
    """)
    suspend fun incrementTerpakai(slotId: Int)

    @Query("""
        UPDATE slot_servis
        SET terpakai = terpakai - 1
        WHERE id = :slotId AND terpakai > 0
    """)
    suspend fun decrementTerpakai(slotId: Int)
}
