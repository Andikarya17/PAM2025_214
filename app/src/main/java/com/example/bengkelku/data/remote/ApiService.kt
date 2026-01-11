package com.example.bengkelku.data.remote

import com.example.bengkelku.data.remote.model.BookingResponse
import com.example.bengkelku.data.remote.model.KendaraanResponse
import com.example.bengkelku.data.remote.model.LoginResponse
import com.example.bengkelku.data.remote.model.PenggunaResponse
import com.example.bengkelku.data.remote.model.ServisResponse
import com.example.bengkelku.data.remote.model.SlotServisResponse
import retrofit2.http.DELETE
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {

    // ===== AUTH =====

    @FormUrlEncoded
    @POST("auth/login.php")
    suspend fun login(
        @Field("username") username: String,
        @Field("password") password: String
    ): ApiResponse<LoginResponse>

    @FormUrlEncoded
    @POST("auth/register.php")
    suspend fun register(
        @Field("nama") nama: String,
        @Field("username") username: String,
        @Field("password") password: String
    ): ApiResponse<PenggunaResponse>

    // ===== KENDARAAN =====

    @GET("kendaraan/list.php")
    suspend fun getKendaraanByPengguna(
        @Query("pengguna_id") penggunaId: Int
    ): ApiResponse<List<KendaraanResponse>>

    @FormUrlEncoded
    @POST("kendaraan/create.php")
    suspend fun createKendaraan(
        @Field("pengguna_id") penggunaId: Int,
        @Field("merk") merk: String,
        @Field("model") model: String,
        @Field("nomor_plat") nomorPlat: String,
        @Field("tahun") tahun: Int?
    ): ApiResponse<KendaraanResponse>

    @FormUrlEncoded
    @PUT("kendaraan/update.php")
    suspend fun updateKendaraan(
        @Field("id") id: Int,
        @Field("merk") merk: String,
        @Field("model") model: String,
        @Field("nomor_plat") nomorPlat: String,
        @Field("tahun") tahun: Int?
    ): ApiResponse<KendaraanResponse>

    @DELETE("kendaraan/delete.php")
    suspend fun deleteKendaraan(
        @Query("id") id: Int
    ): ApiResponse<Unit>

    // ===== SERVIS =====

    @GET("servis/list.php")
    suspend fun getAllServis(): ApiResponse<List<ServisResponse>>

    @GET("servis/aktif.php")
    suspend fun getServisAktif(): ApiResponse<List<ServisResponse>>

    @FormUrlEncoded
    @POST("servis/create.php")
    suspend fun createServis(
        @Field("nama_servis") namaServis: String,
        @Field("harga") harga: Int,
        @Field("deskripsi") deskripsi: String?
    ): ApiResponse<ServisResponse>

    @FormUrlEncoded
    @PUT("servis/update.php")
    suspend fun updateServis(
        @Field("id") id: Int,
        @Field("nama_servis") namaServis: String,
        @Field("harga") harga: Int,
        @Field("deskripsi") deskripsi: String?,
        @Field("aktif") aktif: Boolean
    ): ApiResponse<ServisResponse>

    @DELETE("servis/delete.php")
    suspend fun deleteServis(
        @Query("id") id: Int
    ): ApiResponse<Unit>

    // ===== SLOT SERVIS =====

    @GET("slot/list.php")
    suspend fun getAllSlots(): ApiResponse<List<SlotServisResponse>>

    @GET("slot/available.php")
    suspend fun getAvailableSlots(
        @Query("tanggal") tanggal: String
    ): ApiResponse<List<SlotServisResponse>>

    @FormUrlEncoded
    @POST("slot/create.php")
    suspend fun createSlot(
        @Field("tanggal") tanggal: String,
        @Field("jam_mulai") jamMulai: String,
        @Field("jam_selesai") jamSelesai: String,
        @Field("kapasitas") kapasitas: Int
    ): ApiResponse<SlotServisResponse>

    @FormUrlEncoded
    @PUT("slot/update.php")
    suspend fun updateSlot(
        @Field("id") id: Int,
        @Field("tanggal") tanggal: String,
        @Field("jam_mulai") jamMulai: String,
        @Field("jam_selesai") jamSelesai: String,
        @Field("kapasitas") kapasitas: Int
    ): ApiResponse<SlotServisResponse>

    @DELETE("slot/delete.php")
    suspend fun deleteSlot(
        @Query("id") id: Int
    ): ApiResponse<Unit>

    // ===== BOOKING =====

    @GET("booking/aktif.php")
    suspend fun getBookingAktif(
        @Query("pengguna_id") penggunaId: Int
    ): ApiResponse<List<BookingResponse>>

    @GET("booking/riwayat.php")
    suspend fun getRiwayatBooking(
        @Query("pengguna_id") penggunaId: Int
    ): ApiResponse<List<BookingResponse>>

    @GET("booking/all.php")
    suspend fun getAllBooking(): ApiResponse<List<BookingResponse>>

    @FormUrlEncoded
    @POST("booking/create.php")
    suspend fun createBooking(
        @Field("pengguna_id") penggunaId: Int,
        @Field("kendaraan_id") kendaraanId: Int,
        @Field("servis_id") servisId: Int,
        @Field("slot_servis_id") slotServisId: Int,
        @Field("tanggal_servis") tanggalServis: String,
        @Field("jam_servis") jamServis: String,
        @Field("nomor_antrian") nomorAntrian: String,
        @Field("total_biaya") totalBiaya: Int
    ): ApiResponse<BookingResponse>

    @FormUrlEncoded
    @PUT("booking/update_status.php")
    suspend fun updateBookingStatus(
        @Field("id") id: Int,
        @Field("status") status: String
    ): ApiResponse<BookingResponse>
}
