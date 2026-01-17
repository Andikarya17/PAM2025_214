package com.example.bengkelku.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Servis entity - stores service types from backend
 * PrimaryKey is from backend (not autoGenerate)
 */
@Entity(tableName = "servis")
data class Servis(

    @PrimaryKey
    val id: Int,  // From backend, not autoGenerate

    val namaServis: String,

    val harga: Int,

    val deskripsi: String? = null,

    val isActive: Boolean = true
)