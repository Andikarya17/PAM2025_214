package com.example.bengkelku.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * SlotServis entity - stores service slots from backend
 * PrimaryKey is from backend (not autoGenerate)
 */
@Entity(tableName = "slot_servis")
data class SlotServis(
    
    @PrimaryKey
    val id: Int,  // From backend, not autoGenerate

    val tanggal: String,      // Format: yyyy-MM-dd

    val jamMulai: String,     // Format: HH:mm

    val jamSelesai: String,   // Format: HH:mm

    val kapasitas: Int = 1,

    val terpakai: Int = 0,
    
    val status: String = "available"
)
