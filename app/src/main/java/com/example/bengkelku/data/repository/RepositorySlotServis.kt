package com.example.bengkelku.data.repository

import com.example.bengkelku.data.local.dao.SlotServisDao
import com.example.bengkelku.data.local.entity.SlotServis
import kotlinx.coroutines.flow.Flow

class RepositorySlotServis(
    private val slotServisDao: SlotServisDao
) {

    fun getAllSlots(): Flow<List<SlotServis>> {
        return slotServisDao.getAllSlots()
    }

    fun getSlotsByDate(tanggal: String): Flow<List<SlotServis>> {
        return slotServisDao.getSlotsByDate(tanggal)
    }

    fun getAvailableSlots(today: String): Flow<List<SlotServis>> {
        return slotServisDao.getAvailableSlots(today)
    }

    suspend fun getSlotById(slotId: Int): SlotServis? {
        return slotServisDao.getSlotById(slotId)
    }

    suspend fun tambahSlot(slot: SlotServis) {
        slotServisDao.insert(slot)
    }

    suspend fun updateSlot(slot: SlotServis) {
        slotServisDao.update(slot)
    }

    suspend fun hapusSlot(slot: SlotServis) {
        slotServisDao.delete(slot)
    }

    suspend fun getOverlappingSlots(
        tanggal: String,
        jamMulai: String,
        jamSelesai: String,
        excludeId: Int = 0
    ): List<SlotServis> {
        return slotServisDao.getOverlappingSlots(tanggal, jamMulai, jamSelesai, excludeId)
    }

    suspend fun countBookingsForSlot(slotId: Int): Int {
        return slotServisDao.countBookingsForSlot(slotId)
    }

    suspend fun incrementTerpakai(slotId: Int) {
        slotServisDao.incrementTerpakai(slotId)
    }

    suspend fun decrementTerpakai(slotId: Int) {
        slotServisDao.decrementTerpakai(slotId)
    }
}
