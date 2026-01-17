package com.example.bengkelku.data.remote

import com.example.bengkelku.data.remote.model.*
import retrofit2.Response
import retrofit2.http.*

interface ApiService {

    // ===== AUTH =====

    @FormUrlEncoded
    @POST("auth/login.php")
    suspend fun login(
        @Field("username") username: String,
        @Field("password") password: String
    ): Response<ApiResponse<LoginResponse>>

    @FormUrlEncoded
    @POST("auth/register.php")
    suspend fun register(
        @Field("nama") nama: String,
        @Field("username") username: String,
        @Field("password") password: String
    ): Response<ApiResponse<PenggunaResponse>>

    // ===== KENDARAAN =====

    @GET("kendaraan/list.php")
    suspend fun getKendaraanByPengguna(
        @Query("user_id") userId: Int
    ): Response<ApiResponse<List<KendaraanResponse>>>

    @FormUrlEncoded
    @POST("kendaraan/create.php")
    suspend fun createKendaraan(
        @Field("user_id") userId: Int,
        @Field("merk") merk: String,
        @Field("model") model: String,
        @Field("nomor_plat") nomorPlat: String,
        @Field("tahun") tahun: Int?
    ): Response<ApiResponse<Any>>

    @FormUrlEncoded
    @POST("kendaraan/update.php")
    suspend fun updateKendaraan(
        @Field("id") id: Int,
        @Field("merk") merk: String,
        @Field("model") model: String,
        @Field("nomor_plat") nomorPlat: String,
        @Field("tahun") tahun: Int?
    ): Response<ApiResponse<Any>>

    @FormUrlEncoded
    @POST("kendaraan/delete.php")
    suspend fun deleteKendaraan(
        @Field("id") id: Int
    ): Response<ApiResponse<Any>>

    // ===== SERVIS =====

    @GET("servis/list.php")
    suspend fun getAllServis(): Response<ApiResponse<List<ServisResponse>>>

    @FormUrlEncoded
    @POST("servis/create.php")
    suspend fun createServis(
        @Field("nama_servis") namaServis: String,
        @Field("harga") harga: Int,
        @Field("deskripsi") deskripsi: String?
    ): Response<ApiResponse<Any>>

    @FormUrlEncoded
    @POST("servis/update.php")
    suspend fun updateServis(
        @Field("id") id: Int,
        @Field("nama_servis") namaServis: String,
        @Field("harga") harga: Int,
        @Field("deskripsi") deskripsi: String?
    ): Response<ApiResponse<Any>>

    @FormUrlEncoded
    @POST("servis/delete.php")
    suspend fun deleteServis(
        @Field("id") id: Int
    ): Response<ApiResponse<Any>>

    // ===== SLOT SERVIS =====

    @GET("slot/list_available.php")
    suspend fun getAvailableSlots(): Response<ApiResponse<List<SlotServisResponse>>>

    @GET("slot/list_all.php")
    suspend fun getAllSlots(): Response<ApiResponse<List<SlotServisResponse>>>

    @FormUrlEncoded
    @POST("slot/create.php")
    suspend fun createSlot(
        @Field("tanggal") tanggal: String,
        @Field("jam_mulai") jamMulai: String,
        @Field("jam_selesai") jamSelesai: String,
        @Field("kapasitas") kapasitas: Int
    ): Response<ApiResponse<Any>>

    @FormUrlEncoded
    @POST("slot/update.php")
    suspend fun updateSlot(
        @Field("id") id: Int,
        @Field("tanggal") tanggal: String,
        @Field("jam_mulai") jamMulai: String,
        @Field("jam_selesai") jamSelesai: String,
        @Field("kapasitas") kapasitas: Int
    ): Response<ApiResponse<Any>>

    @FormUrlEncoded
    @POST("slot/delete.php")
    suspend fun deleteSlot(
        @Field("id") id: Int
    ): Response<ApiResponse<Any>>

    // ===== BOOKING =====

    @FormUrlEncoded
    @POST("booking/create.php")
    suspend fun createBooking(
        @Field("user_id") userId: Int,
        @Field("kendaraan_id") kendaraanId: Int,
        @Field("jenis_servis_id") jenisServisId: Int,
        @Field("slot_servis_id") slotServisId: Int
    ): Response<ApiResponse<BookingResponse>>

    @GET("booking/list_customer.php")
    suspend fun getBookingCustomer(
        @Query("user_id") userId: Int
    ): Response<ApiResponse<List<BookingResponse>>>

    @GET("booking/list_admin.php")
    suspend fun getAllBooking(): Response<ApiResponse<List<BookingResponse>>>

    @FormUrlEncoded
    @POST("booking/update_status.php")
    suspend fun updateBookingStatus(
        @Field("id") id: Int,
        @Field("status") status: String
    ): Response<ApiResponse<Any>>
}
