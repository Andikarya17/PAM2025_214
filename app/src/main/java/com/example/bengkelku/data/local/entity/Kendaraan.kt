package com.example.bengkelku.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Kendaraan entity - stores vehicles from backend
 * PrimaryKey is from backend (not autoGenerate)
 */
@Entity(
    tableName = "kendaraan",
    foreignKeys = [
        ForeignKey(
            entity = Pengguna::class,
            parentColumns = ["id"],
            childColumns = ["penggunaId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index("penggunaId"),
        // UNIQUE per customer, not global
        Index(value = ["penggunaId", "nomorPlat"], unique = true)
    ]
)
data class Kendaraan(

    @PrimaryKey
    val id: Int,  // From backend, not autoGenerate

    val penggunaId: Int,      // Pemilik kendaraan (pelanggan)

    val merk: String,

    val model: String,

    val nomorPlat: String,

    val tahun: Int? = null,
    
    val warna: String? = null
)
