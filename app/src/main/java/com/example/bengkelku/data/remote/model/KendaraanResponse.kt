package com.example.bengkelku.data.remote.model

import com.google.gson.annotations.SerializedName

data class KendaraanResponse(
    @SerializedName("id")
    val id: Int,

    @SerializedName("pengguna_id")
    val penggunaId: Int,

    @SerializedName("merk")
    val merk: String,

    @SerializedName("model")
    val model: String,

    @SerializedName("nomor_plat")
    val nomorPlat: String,

    @SerializedName("tahun")
    val tahun: Int?
)
