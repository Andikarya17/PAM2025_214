package com.example.bengkelku.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "slot_servis")
data class SlotServis(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    val tanggal: String,      // Format: yyyy-MM-dd

    val jamMulai: String,     // Format: HH:mm

    val jamSelesai: String,   // Format: HH:mm

    val kapasitas: Int = 1,

    val terpakai: Int = 0
)
