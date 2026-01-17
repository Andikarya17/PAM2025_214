package com.example.bengkelku.data.remote.model

import com.google.gson.annotations.SerializedName

data class BookingResponse(
    @SerializedName("id")
    val id: Int = 0,

    @SerializedName("user_id")
    val userId: Int = 0,

    @SerializedName("kendaraan_id")
    val kendaraanId: Int = 0,

    @SerializedName("jenis_servis_id")
    val jenisServisId: Int = 0,

    @SerializedName("slot_servis_id")
    val slotServisId: Int = 0,

    @SerializedName("tanggal_servis")
    val tanggalServis: String = "",

    @SerializedName("jam_servis")
    val jamServis: String = "",

    @SerializedName("nomor_antrian")
    val nomorAntrian: Int = 0,

    @SerializedName("status")
    val status: String = "",

    @SerializedName("total_biaya")
    val totalBiaya: Int = 0,

    // Optional joined data from backend
    @SerializedName("nama_pengguna")
    val namaPengguna: String? = null,

    @SerializedName("nama_kendaraan")
    val namaKendaraan: String? = null,

    @SerializedName("nama_servis")
    val namaServis: String? = null
)
