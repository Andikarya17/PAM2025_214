package com.example.bengkelku.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "servis")
data class Servis(

    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    val namaServis: String,

    val harga: Int,

    val deskripsi: String? = null,

    val aktif: Boolean = true
)