package com.example.bengkelku.data.remote.model

import com.google.gson.annotations.SerializedName

data class BookingResponse(
    @SerializedName("id")
    val id: Int,

    @SerializedName("pengguna_id")
    val penggunaId: Int,

    @SerializedName("kendaraan_id")
    val kendaraanId: Int,

    @SerializedName("servis_id")
    val servisId: Int,

    @SerializedName("slot_servis_id")
    val slotServisId: Int,

    @SerializedName("tanggal_servis")
    val tanggalServis: String,

    @SerializedName("jam_servis")
    val jamServis: String,

    @SerializedName("nomor_antrian")
    val nomorAntrian: String,

    @SerializedName("status")
    val status: String,

    @SerializedName("total_biaya")
    val totalBiaya: Int,

    // Optional joined data from backend
    @SerializedName("nama_pengguna")
    val namaPengguna: String? = null,

    @SerializedName("nama_kendaraan")
    val namaKendaraan: String? = null,

    @SerializedName("nama_servis")
    val namaServis: String? = null
)
