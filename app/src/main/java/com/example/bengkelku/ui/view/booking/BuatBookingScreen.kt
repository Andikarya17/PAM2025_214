package com.example.bengkelku.ui.view.booking

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.bengkelku.data.local.entity.Booking
import com.example.bengkelku.data.local.entity.Kendaraan
import com.example.bengkelku.data.local.entity.Servis
import com.example.bengkelku.data.local.entity.SlotServis
import com.example.bengkelku.data.local.entity.StatusBooking
import com.example.bengkelku.ui.view.components.BaseScaffold
import com.example.bengkelku.viewmodel.booking.AksiBookingState
import com.example.bengkelku.viewmodel.booking.BookingViewModel
import com.example.bengkelku.viewmodel.kendaraan.KendaraanViewModel
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BuatBookingScreen(
    bookingViewModel: BookingViewModel,
    kendaraanViewModel: KendaraanViewModel,
    penggunaId: Int,
    onSelesai: () -> Unit,
    onBack: () -> Unit
) {
    // Collect daftar kendaraan milik customer
    val daftarKendaraan by kendaraanViewModel.daftarKendaraan.collectAsState(initial = emptyList())
    // Collect daftar servis aktif
    val daftarServis by bookingViewModel.daftarServis.collectAsState()
    // Collect daftar slot tersedia
    val daftarSlot by bookingViewModel.daftarSlot.collectAsState()
    // Collect aksi state
    val aksiState by bookingViewModel.aksiState.collectAsState()

    // State untuk dropdown dan form
    var selectedKendaraan by remember { mutableStateOf<Kendaraan?>(null) }
    var selectedServis by remember { mutableStateOf<Servis?>(null) }
    var selectedSlot by remember { mutableStateOf<SlotServis?>(null) }
    var expandedKendaraan by remember { mutableStateOf(false) }
    var expandedServis by remember { mutableStateOf(false) }
    var expandedSlot by remember { mutableStateOf(false) }

    val snackbarHostState = remember { SnackbarHostState() }

    // Handle state changes
    LaunchedEffect(aksiState) {
        when (val state = aksiState) {
            is AksiBookingState.Berhasil -> {
                bookingViewModel.resetState()
                onSelesai()
            }
            is AksiBookingState.Gagal -> {
                snackbarHostState.showSnackbar(state.pesan)
                bookingViewModel.resetState()
            }
            else -> {}
        }
    }

    // Format harga
    val currencyFormat = remember { NumberFormat.getCurrencyInstance(Locale("id", "ID")) }

    // Compute totalBiaya from selected service
    val totalBiaya = selectedServis?.harga ?: 0

    // Derive tanggal and jam from selected slot
    val tanggal = selectedSlot?.tanggal ?: ""
    val jam = selectedSlot?.jamMulai ?: ""

    // Validation
    val isFormValid = selectedKendaraan != null &&
            selectedServis != null &&
            selectedSlot != null

    BaseScaffold(
        title = "Buat Booking Servis",
        showBack = true,
        onBack = onBack
    ) { paddingValues ->
        Scaffold(
            snackbarHost = { SnackbarHost(snackbarHostState) }
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(innerPadding)
                    .padding(16.dp)
            ) {
            // === DROPDOWN PILIH KENDARAAN ===
            ExposedDropdownMenuBox(
                expanded = expandedKendaraan,
                onExpandedChange = { expandedKendaraan = !expandedKendaraan }
            ) {
                OutlinedTextField(
                    value = selectedKendaraan?.let { "${it.merk} ${it.model} - ${it.nomorPlat}" } ?: "",
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Pilih Kendaraan") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedKendaraan) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor()
                )

                ExposedDropdownMenu(
                    expanded = expandedKendaraan,
                    onDismissRequest = { expandedKendaraan = false }
                ) {
                    if (daftarKendaraan.isEmpty()) {
                        DropdownMenuItem(
                            text = { Text("Belum ada kendaraan") },
                            onClick = { expandedKendaraan = false },
                            enabled = false
                        )
                    } else {
                        daftarKendaraan.forEach { kendaraan ->
                            DropdownMenuItem(
                                text = { Text("${kendaraan.merk} ${kendaraan.model} - ${kendaraan.nomorPlat}") },
                                onClick = {
                                    selectedKendaraan = kendaraan
                                    expandedKendaraan = false
                                }
                            )
                        }
                    }
                }
            }

            Spacer(Modifier.height(12.dp))

            // === DROPDOWN PILIH SERVIS ===
            ExposedDropdownMenuBox(
                expanded = expandedServis,
                onExpandedChange = { expandedServis = !expandedServis }
            ) {
                OutlinedTextField(
                    value = selectedServis?.let {
                        "${it.namaServis} - ${currencyFormat.format(it.harga)}"
                    } ?: "",
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Pilih Jenis Servis") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedServis) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor()
                )

                ExposedDropdownMenu(
                    expanded = expandedServis,
                    onDismissRequest = { expandedServis = false }
                ) {
                    if (daftarServis.isEmpty()) {
                        DropdownMenuItem(
                            text = { Text("Belum ada servis tersedia") },
                            onClick = { expandedServis = false },
                            enabled = false
                        )
                    } else {
                        daftarServis.forEach { servis ->
                            DropdownMenuItem(
                                text = { Text("${servis.namaServis} - ${currencyFormat.format(servis.harga)}") },
                                onClick = {
                                    selectedServis = servis
                                    expandedServis = false
                                }
                            )
                        }
                    }
                }
            }

            Spacer(Modifier.height(12.dp))

            // === DROPDOWN PILIH SLOT ===
            ExposedDropdownMenuBox(
                expanded = expandedSlot,
                onExpandedChange = { expandedSlot = !expandedSlot }
            ) {
                OutlinedTextField(
                    value = selectedSlot?.let {
                        "${it.tanggal} | ${it.jamMulai} - ${it.jamSelesai}"
                    } ?: "",
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Pilih Slot Waktu") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedSlot) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor()
                )

                ExposedDropdownMenu(
                    expanded = expandedSlot,
                    onDismissRequest = { expandedSlot = false }
                ) {
                    if (daftarSlot.isEmpty()) {
                        DropdownMenuItem(
                            text = { Text("Tidak ada slot tersedia") },
                            onClick = { expandedSlot = false },
                            enabled = false
                        )
                    } else {
                        daftarSlot.forEach { slot ->
                            val sisaSlot = slot.kapasitas - slot.terpakai
                            DropdownMenuItem(
                                text = {
                                    Text("${slot.tanggal} | ${slot.jamMulai} - ${slot.jamSelesai} ($sisaSlot tersisa)")
                                },
                                onClick = {
                                    selectedSlot = slot
                                    expandedSlot = false
                                }
                            )
                        }
                    }
                }
            }

            Spacer(Modifier.height(16.dp))

            // === BOOKING SUMMARY CARD ===
            if (selectedKendaraan != null || selectedServis != null || selectedSlot != null) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.secondaryContainer
                    )
                ) {
                    Column(Modifier.padding(16.dp)) {
                        Text(
                            "Ringkasan Booking",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                        Spacer(Modifier.height(12.dp))

                        // Kendaraan
                        selectedKendaraan?.let { kendaraan ->
                            Text(
                                "Kendaraan:",
                                style = MaterialTheme.typography.labelMedium
                            )
                            Text(
                                "${kendaraan.merk} ${kendaraan.model} - ${kendaraan.nomorPlat}",
                                style = MaterialTheme.typography.bodyMedium
                            )
                            Spacer(Modifier.height(8.dp))
                        }

                        // Servis
                        selectedServis?.let { servis ->
                            Text(
                                "Jenis Servis:",
                                style = MaterialTheme.typography.labelMedium
                            )
                            Text(
                                "${servis.namaServis} - ${currencyFormat.format(servis.harga)}",
                                style = MaterialTheme.typography.bodyMedium
                            )
                            Spacer(Modifier.height(8.dp))
                        }

                        // Slot (Tanggal & Jam)
                        selectedSlot?.let { slot ->
                            Text(
                                "Tanggal & Jam:",
                                style = MaterialTheme.typography.labelMedium
                            )
                            Text(
                                "${slot.tanggal} | ${slot.jamMulai} - ${slot.jamSelesai}",
                                style = MaterialTheme.typography.bodyMedium
                            )
                            Spacer(Modifier.height(8.dp))
                        }

                        // Total Biaya
                        if (selectedServis != null) {
                            HorizontalDivider(Modifier.padding(vertical = 8.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    "Total Biaya:",
                                    style = MaterialTheme.typography.titleSmall
                                )
                                Text(
                                    currencyFormat.format(totalBiaya),
                                    style = MaterialTheme.typography.titleSmall,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                    }
                }
                Spacer(Modifier.height(16.dp))
            }

            Button(
                onClick = {
                    selectedKendaraan?.let { kendaraan ->
                        selectedServis?.let { servis ->
                            selectedSlot?.let { slot ->
                                val nomorAntrian = generateNomorAntrian()

                                val booking = Booking(
                                    penggunaId = penggunaId,
                                    kendaraanId = kendaraan.id,
                                    servisId = servis.id,
                                    slotServisId = slot.id,
                                    tanggalServis = slot.tanggal,
                                    jamServis = slot.jamMulai,
                                    nomorAntrian = nomorAntrian,
                                    status = StatusBooking.MENUNGGU,
                                    totalBiaya = servis.harga
                                )

                                bookingViewModel.buatBooking(booking)
                                // Navigation handled by LaunchedEffect on aksiState
                            }
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = isFormValid
            ) {
                Text("Simpan Booking")
            }
            }
        }
    }
}

private fun generateNomorAntrian(): String {
    val sdf = SimpleDateFormat("yyyyMMddHHmmss", Locale.getDefault())
    return "ANTRI-${sdf.format(Date())}"
}
