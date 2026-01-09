package com.example.bengkelku.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

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
        Index(value = ["nomorPlat"], unique = true)
    ]
)
data class Kendaraan(

    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    val penggunaId: Int,      // Pemilik kendaraan (pelanggan)

    val merk: String,

    val model: String,

    val nomorPlat: String,

    val tahun: Int? = null
)
