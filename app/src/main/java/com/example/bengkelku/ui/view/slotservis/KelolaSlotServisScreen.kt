package com.example.bengkelku.ui.view.slotservis

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.bengkelku.data.local.entity.SlotServis
import com.example.bengkelku.ui.view.components.BaseScaffold
import com.example.bengkelku.viewmodel.slotservis.AksiSlotState
import com.example.bengkelku.viewmodel.slotservis.SlotServisViewModel
import java.util.*

@Composable
fun KelolaSlotServisScreen(
    viewModel: SlotServisViewModel,
    onBack: () -> Unit
) {
    val semuaSlot by viewModel.semuaSlot.collectAsState()
    val aksiState by viewModel.aksiState.collectAsState()
    val slotUntukEdit by viewModel.slotUntukEdit.collectAsState()

    var showTambahDialog by remember { mutableStateOf(false) }
    var showHapusDialog by remember { mutableStateOf<SlotServis?>(null) }

    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(aksiState) {
        when (val state = aksiState) {
            is AksiSlotState.Berhasil -> {
                snackbarHostState.showSnackbar(state.pesan)
                viewModel.resetState()
            }
            is AksiSlotState.Gagal -> {
                snackbarHostState.showSnackbar(state.pesan)
                viewModel.resetState()
            }
            else -> {}
        }
    }

    BaseScaffold(
        title = "Kelola Slot Servis",
        showBack = true,
        onBack = onBack
    ) { paddingValues ->
        Scaffold(
            snackbarHost = { SnackbarHost(snackbarHostState) },
            floatingActionButton = {
                FloatingActionButton(
                    onClick = { showTambahDialog = true }
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Tambah Slot")
                }
            }
        ) { innerPadding ->
            if (semuaSlot.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .padding(innerPadding),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        "Belum ada slot servis.\nTambahkan slot baru dengan tombol +",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .padding(innerPadding)
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(semuaSlot) { slot ->
                        SlotItemCard(
                            slot = slot,
                            onEdit = { viewModel.pilihUntukEdit(slot) },
                            onHapus = { showHapusDialog = slot }
                        )
                    }
                }
            }
        }
    }

    // Dialog Tambah Slot
    if (showTambahDialog) {
        SlotFormDialog(
            title = "Tambah Slot",
            onDismiss = { showTambahDialog = false },
            onSimpan = { tanggal, jamMulai, jamSelesai, kapasitas ->
                viewModel.tambahSlot(tanggal, jamMulai, jamSelesai, kapasitas)
                showTambahDialog = false
            }
        )
    }

    // Dialog Edit Slot
    slotUntukEdit?.let { slot ->
        SlotFormDialog(
            title = "Edit Slot",
            initialTanggal = slot.tanggal,
            initialJamMulai = slot.jamMulai,
            initialJamSelesai = slot.jamSelesai,
            initialKapasitas = slot.kapasitas.toString(),
            onDismiss = { viewModel.batalEdit() },
            onSimpan = { tanggal, jamMulai, jamSelesai, kapasitas ->
                viewModel.updateSlot(
                    slot.copy(
                        tanggal = tanggal,
                        jamMulai = jamMulai,
                        jamSelesai = jamSelesai,
                        kapasitas = kapasitas
                    )
                )
            }
        )
    }

    // Dialog Konfirmasi Hapus
    showHapusDialog?.let { slot ->
        AlertDialog(
            onDismissRequest = { showHapusDialog = null },
            title = { Text("Hapus Slot") },
            text = {
                Text("Yakin ingin menghapus slot ${slot.tanggal} (${slot.jamMulai} - ${slot.jamSelesai})?")
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.hapusSlot(slot)
                        showHapusDialog = null
                    }
                ) {
                    Text("Hapus", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { showHapusDialog = null }) {
                    Text("Batal")
                }
            }
        )
    }
}

@Composable
private fun SlotItemCard(
    slot: SlotServis,
    onEdit: () -> Unit,
    onHapus: () -> Unit
) {
    val sisaKapasitas = slot.kapasitas - slot.terpakai
    val isAvailable = sisaKapasitas > 0

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (isAvailable)
                MaterialTheme.colorScheme.surface
            else
                MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.3f)
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = slot.tanggal,
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        text = "${slot.jamMulai} - ${slot.jamSelesai}",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = "Kapasitas: ${slot.terpakai}/${slot.kapasitas}",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Text(
                        text = if (isAvailable) "Tersedia ($sisaKapasitas slot)" else "Penuh",
                        style = MaterialTheme.typography.labelMedium,
                        color = if (isAvailable)
                            MaterialTheme.colorScheme.primary
                        else
                            MaterialTheme.colorScheme.error
                    )
                }

                Row {
                    IconButton(onClick = onEdit) {
                        Icon(Icons.Default.Edit, contentDescription = "Edit")
                    }
                    IconButton(onClick = onHapus) {
                        Icon(
                            Icons.Default.Delete,
                            contentDescription = "Hapus",
                            tint = MaterialTheme.colorScheme.error
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun SlotFormDialog(
    title: String,
    initialTanggal: String = "",
    initialJamMulai: String = "",
    initialJamSelesai: String = "",
    initialKapasitas: String = "1",
    onDismiss: () -> Unit,
    onSimpan: (tanggal: String, jamMulai: String, jamSelesai: String, kapasitas: Int) -> Unit
) {
    val context = LocalContext.current
    val calendar = Calendar.getInstance()

    var tanggal by remember { mutableStateOf(initialTanggal) }
    var jamMulai by remember { mutableStateOf(initialJamMulai) }
    var jamSelesai by remember { mutableStateOf(initialJamSelesai) }
    var kapasitas by remember { mutableStateOf(initialKapasitas) }

    var tanggalError by remember { mutableStateOf(false) }
    var jamMulaiError by remember { mutableStateOf(false) }
    var jamSelesaiError by remember { mutableStateOf(false) }
    var kapasitasError by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title) },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Date Picker
                OutlinedTextField(
                    value = tanggal,
                    onValueChange = {},
                    label = { Text("Tanggal") },
                    isError = tanggalError,
                    supportingText = if (tanggalError) {
                        { Text("Tanggal wajib dipilih") }
                    } else null,
                    readOnly = true,
                    trailingIcon = {
                        Icon(
                            Icons.Default.DateRange,
                            contentDescription = "Pilih Tanggal"
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            DatePickerDialog(
                                context,
                                { _, year, month, day ->
                                    tanggal = String.format("%04d-%02d-%02d", year, month + 1, day)
                                    tanggalError = false
                                },
                                calendar.get(Calendar.YEAR),
                                calendar.get(Calendar.MONTH),
                                calendar.get(Calendar.DAY_OF_MONTH)
                            ).show()
                        }
                )

                // Jam Mulai
                OutlinedTextField(
                    value = jamMulai,
                    onValueChange = {},
                    label = { Text("Jam Mulai") },
                    isError = jamMulaiError,
                    supportingText = if (jamMulaiError) {
                        { Text("Jam mulai wajib dipilih") }
                    } else null,
                    readOnly = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            TimePickerDialog(
                                context,
                                { _, hour, minute ->
                                    jamMulai = String.format("%02d:%02d", hour, minute)
                                    jamMulaiError = false
                                },
                                9, 0, true
                            ).show()
                        }
                )

                // Jam Selesai
                OutlinedTextField(
                    value = jamSelesai,
                    onValueChange = {},
                    label = { Text("Jam Selesai") },
                    isError = jamSelesaiError,
                    supportingText = if (jamSelesaiError) {
                        { Text("Jam selesai wajib dipilih") }
                    } else null,
                    readOnly = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            TimePickerDialog(
                                context,
                                { _, hour, minute ->
                                    jamSelesai = String.format("%02d:%02d", hour, minute)
                                    jamSelesaiError = false
                                },
                                10, 0, true
                            ).show()
                        }
                )

                // Kapasitas
                OutlinedTextField(
                    value = kapasitas,
                    onValueChange = {
                        kapasitas = it.filter { char -> char.isDigit() }
                        kapasitasError = false
                    },
                    label = { Text("Kapasitas") },
                    isError = kapasitasError,
                    supportingText = if (kapasitasError) {
                        { Text("Kapasitas minimal 1") }
                    } else null,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    tanggalError = tanggal.isBlank()
                    jamMulaiError = jamMulai.isBlank()
                    jamSelesaiError = jamSelesai.isBlank()
                    kapasitasError = kapasitas.isBlank() ||
                            kapasitas.toIntOrNull() == null ||
                            kapasitas.toInt() < 1

                    if (!tanggalError && !jamMulaiError && !jamSelesaiError && !kapasitasError) {
                        onSimpan(
                            tanggal,
                            jamMulai,
                            jamSelesai,
                            kapasitas.toInt()
                        )
                    }
                }
            ) {
                Text("Simpan")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Batal")
            }
        }
    )
}
