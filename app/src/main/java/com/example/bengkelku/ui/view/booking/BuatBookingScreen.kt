package com.example.bengkelku.ui.view.booking

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.bengkelku.data.remote.model.KendaraanResponse
import com.example.bengkelku.data.remote.model.ServisResponse
import com.example.bengkelku.data.remote.model.SlotServisResponse
import com.example.bengkelku.ui.view.components.BaseScaffold
import com.example.bengkelku.viewmodel.booking.AksiBookingState
import com.example.bengkelku.viewmodel.booking.BookingViewModel
import com.example.bengkelku.viewmodel.booking.ListServisState
import com.example.bengkelku.viewmodel.booking.ListSlotState
import com.example.bengkelku.viewmodel.kendaraan.KendaraanViewModel
import com.example.bengkelku.viewmodel.kendaraan.ListKendaraanState
import java.text.NumberFormat
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
    // Collect states from ViewModels
    val kendaraanState by kendaraanViewModel.listState.collectAsState()
    val servisState by bookingViewModel.servisState.collectAsState()
    val slotState by bookingViewModel.slotState.collectAsState()
    val aksiState by bookingViewModel.aksiState.collectAsState()

    // State untuk dropdown dan form
    var selectedKendaraan by remember { mutableStateOf<KendaraanResponse?>(null) }
    var selectedServis by remember { mutableStateOf<ServisResponse?>(null) }
    var selectedSlot by remember { mutableStateOf<SlotServisResponse?>(null) }
    var expandedKendaraan by remember { mutableStateOf(false) }
    var expandedServis by remember { mutableStateOf(false) }
    var expandedSlot by remember { mutableStateOf(false) }

    val snackbarHostState = remember { SnackbarHostState() }
    val isLoading = aksiState is AksiBookingState.Loading

    // Handle aksi state changes
    LaunchedEffect(aksiState) {
        when (val state = aksiState) {
            is AksiBookingState.Berhasil -> {
                snackbarHostState.showSnackbar(state.message)
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

    // Validation
    val isFormValid = selectedKendaraan != null &&
            selectedServis != null &&
            selectedSlot != null &&
            !isLoading

    // Extract list data from states
    val daftarKendaraan = when (val state = kendaraanState) {
        is ListKendaraanState.Success -> state.data
        else -> emptyList()
    }
    val daftarServis = when (val state = servisState) {
        is ListServisState.Success -> state.data
        else -> emptyList()
    }
    val daftarSlot = when (val state = slotState) {
        is ListSlotState.Success -> state.data
        else -> emptyList()
    }

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
                    onExpandedChange = { if (!isLoading) expandedKendaraan = !expandedKendaraan }
                ) {
                    OutlinedTextField(
                        value = selectedKendaraan?.let { "${it.merk} ${it.model} - ${it.nomorPlat}" } ?: "",
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Pilih Kendaraan") },
                        trailingIcon = {
                            if (kendaraanState is ListKendaraanState.Loading) {
                                CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                            } else {
                                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedKendaraan)
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor(),
                        enabled = !isLoading
                    )

                    ExposedDropdownMenu(
                        expanded = expandedKendaraan,
                        onDismissRequest = { expandedKendaraan = false }
                    ) {
                        when (kendaraanState) {
                            is ListKendaraanState.Error -> {
                                DropdownMenuItem(
                                    text = { Text("Gagal memuat kendaraan", color = MaterialTheme.colorScheme.error) },
                                    onClick = { 
                                        kendaraanViewModel.loadKendaraan()
                                        expandedKendaraan = false 
                                    }
                                )
                            }
                            else -> {
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
                    }
                }

                Spacer(Modifier.height(12.dp))

                // === DROPDOWN PILIH SERVIS ===
                ExposedDropdownMenuBox(
                    expanded = expandedServis,
                    onExpandedChange = { if (!isLoading) expandedServis = !expandedServis }
                ) {
                    OutlinedTextField(
                        value = selectedServis?.let {
                            "${it.namaServis} - ${currencyFormat.format(it.harga)}"
                        } ?: "",
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Pilih Jenis Servis") },
                        trailingIcon = {
                            if (servisState is ListServisState.Loading) {
                                CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                            } else {
                                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedServis)
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor(),
                        enabled = !isLoading
                    )

                    ExposedDropdownMenu(
                        expanded = expandedServis,
                        onDismissRequest = { expandedServis = false }
                    ) {
                        when (servisState) {
                            is ListServisState.Error -> {
                                DropdownMenuItem(
                                    text = { Text("Gagal memuat servis", color = MaterialTheme.colorScheme.error) },
                                    onClick = { 
                                        bookingViewModel.loadData()
                                        expandedServis = false 
                                    }
                                )
                            }
                            else -> {
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
                    }
                }

                Spacer(Modifier.height(12.dp))

                // === DROPDOWN PILIH SLOT ===
                ExposedDropdownMenuBox(
                    expanded = expandedSlot,
                    onExpandedChange = { if (!isLoading) expandedSlot = !expandedSlot }
                ) {
                    OutlinedTextField(
                        value = selectedSlot?.let {
                            "${it.tanggal} | ${it.jamMulai} - ${it.jamSelesai}"
                        } ?: "",
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Pilih Slot Waktu") },
                        trailingIcon = {
                            if (slotState is ListSlotState.Loading) {
                                CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                            } else {
                                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedSlot)
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor(),
                        enabled = !isLoading
                    )

                    ExposedDropdownMenu(
                        expanded = expandedSlot,
                        onDismissRequest = { expandedSlot = false }
                    ) {
                        when (slotState) {
                            is ListSlotState.Error -> {
                                DropdownMenuItem(
                                    text = { Text("Gagal memuat slot", color = MaterialTheme.colorScheme.error) },
                                    onClick = { 
                                        bookingViewModel.loadData()
                                        expandedSlot = false 
                                    }
                                )
                            }
                            else -> {
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
                                    bookingViewModel.buatBooking(
                                        kendaraanId = kendaraan.id,
                                        jenisServisId = servis.id,
                                        slotServisId = slot.id
                                    )
                                }
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = isFormValid
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            strokeWidth = 2.dp,
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                        Spacer(Modifier.width(8.dp))
                    }
                    Text(if (isLoading) "Menyimpan..." else "Simpan Booking")
                }
            }
        }
    }
}
