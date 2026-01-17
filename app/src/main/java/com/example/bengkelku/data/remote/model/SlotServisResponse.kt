package com.example.bengkelku.data.remote.model

import com.google.gson.annotations.SerializedName

data class SlotServisResponse(
    @SerializedName("id")
    val id: Int = 0,

    @SerializedName("tanggal")
    val tanggal: String = "",

    @SerializedName("jam_mulai")
    val jamMulai: String = "",

    @SerializedName("jam_selesai")
    val jamSelesai: String = "",

    @SerializedName("kapasitas")
    val kapasitas: Int = 0,

    @SerializedName("terpakai")
    val terpakai: Int = 0,

    @SerializedName("status")
    val status: String = "available"
)
