package com.example.bengkelku.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "booking",
    foreignKeys = [
        ForeignKey(
            entity = Pengguna::class,
            parentColumns = ["id"],
            childColumns = ["penggunaId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = Kendaraan::class,
            parentColumns = ["id"],
            childColumns = ["kendaraanId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = Servis::class,
            parentColumns = ["id"],
            childColumns = ["servisId"],
            onDelete = ForeignKey.RESTRICT
        )
    ],
    indices = [
        Index("penggunaId"),
        Index("kendaraanId"),
        Index("servisId")
    ]
)
data class Booking(

    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    val penggunaId: Int,      // Pelanggan yang booking

    val kendaraanId: Int,     // Kendaraan yang diservis

    val servisId: Int,        // Jenis servis

    val tanggalServis: String, // Format: yyyy-MM-dd

    val jamServis: String,     // Format: HH:mm

    val nomorAntrian: String,

    val status: StatusBooking = StatusBooking.MENUNGGU,

    val totalBiaya: Int
)

/**
 * Status pengerjaan booking servis
 */
enum class StatusBooking {
    MENUNGGU,
    DIPROSES,
    SELESAI,
    DIAMBIL
}
