package com.example.bengkelku.data.remote.model

import com.google.gson.annotations.SerializedName

data class KendaraanResponse(
    @SerializedName("id")
    val id: Int = 0,

    @SerializedName("user_id")
    val userId: Int = 0,

    @SerializedName("merk")
    val merk: String = "",

    @SerializedName("model")
    val model: String = "",

    @SerializedName("nomor_plat")
    val nomorPlat: String = "",

    @SerializedName("tahun")
    val tahun: Int? = null,

    @SerializedName("warna")
    val warna: String? = null
)
