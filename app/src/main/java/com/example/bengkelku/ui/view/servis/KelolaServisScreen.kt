package com.example.bengkelku.ui.view.servis

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.bengkelku.data.local.entity.Servis
import com.example.bengkelku.ui.view.components.BaseScaffold
import com.example.bengkelku.viewmodel.servis.AksiServisState
import com.example.bengkelku.viewmodel.servis.ServisAdminViewModel
import java.text.NumberFormat
import java.util.*

@Composable
fun KelolaServisScreen(
    viewModel: ServisAdminViewModel,
    onBack: () -> Unit
) {
    val semuaServis by viewModel.semuaServis.collectAsState()
    val aksiState by viewModel.aksiState.collectAsState()
    val servisUntukEdit by viewModel.servisUntukEdit.collectAsState()

    var showTambahDialog by remember { mutableStateOf(false) }
    var showHapusDialog by remember { mutableStateOf<Servis?>(null) }

    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(aksiState) {
        when (val state = aksiState) {
            is AksiServisState.Berhasil -> {
                snackbarHostState.showSnackbar(state.pesan)
                viewModel.resetState()
            }
            is AksiServisState.Gagal -> {
                snackbarHostState.showSnackbar(state.pesan)
                viewModel.resetState()
            }
            else -> {}
        }
    }

    BaseScaffold(
        title = "Kelola Servis",
        showBack = true,
        onBack = onBack
    ) { paddingValues ->
        Scaffold(
            snackbarHost = { SnackbarHost(snackbarHostState) },
            floatingActionButton = {
                FloatingActionButton(
                    onClick = { showTambahDialog = true }
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Tambah Servis")
                }
            }
        ) { innerPadding ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(innerPadding)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(semuaServis) { servis ->
                    ServisItemCard(
                        servis = servis,
                        onEdit = { viewModel.pilihUntukEdit(servis) },
                        onHapus = { showHapusDialog = servis },
                        onToggleAktif = { viewModel.toggleAktif(servis) }
                    )
                }
            }
        }
    }

    // Dialog Tambah Servis
    if (showTambahDialog) {
        ServisFormDialog(
            title = "Tambah Servis",
            onDismiss = { showTambahDialog = false },
            onSimpan = { nama, harga, deskripsi ->
                viewModel.tambahServis(nama, harga, deskripsi)
                showTambahDialog = false
            }
        )
    }

    // Dialog Edit Servis
    servisUntukEdit?.let { servis ->
        ServisFormDialog(
            title = "Edit Servis",
            initialNama = servis.namaServis,
            initialHarga = servis.harga.toString(),
            initialDeskripsi = servis.deskripsi ?: "",
            onDismiss = { viewModel.batalEdit() },
            onSimpan = { nama, harga, deskripsi ->
                viewModel.updateServis(
                    servis.copy(
                        namaServis = nama,
                        harga = harga,
                        deskripsi = deskripsi
                    )
                )
            }
        )
    }

    // Dialog Konfirmasi Hapus
    showHapusDialog?.let { servis ->
        AlertDialog(
            onDismissRequest = { showHapusDialog = null },
            title = { Text("Hapus Servis") },
            text = { Text("Yakin ingin menghapus servis \"${servis.namaServis}\"?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.hapusServis(servis)
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
private fun ServisItemCard(
    servis: Servis,
    onEdit: () -> Unit,
    onHapus: () -> Unit,
    onToggleAktif: () -> Unit
) {
    val currencyFormat = remember { NumberFormat.getCurrencyInstance(Locale("id", "ID")) }

    Card(
        modifier = Modifier.fillMaxWidth()
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
                        text = servis.namaServis,
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        text = currencyFormat.format(servis.harga),
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.primary
                    )
                    servis.deskripsi?.let { deskripsi ->
                        if (deskripsi.isNotBlank()) {
                            Text(
                                text = deskripsi,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
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

            Spacer(Modifier.height(8.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (servis.aktif) "Aktif" else "Nonaktif",
                    style = MaterialTheme.typography.labelMedium,
                    color = if (servis.aktif)
                        MaterialTheme.colorScheme.primary
                    else
                        MaterialTheme.colorScheme.error
                )
                Spacer(Modifier.width(8.dp))
                Switch(
                    checked = servis.aktif,
                    onCheckedChange = { onToggleAktif() }
                )
            }
        }
    }
}

@Composable
private fun ServisFormDialog(
    title: String,
    initialNama: String = "",
    initialHarga: String = "",
    initialDeskripsi: String = "",
    onDismiss: () -> Unit,
    onSimpan: (nama: String, harga: Int, deskripsi: String?) -> Unit
) {
    var nama by remember { mutableStateOf(initialNama) }
    var harga by remember { mutableStateOf(initialHarga) }
    var deskripsi by remember { mutableStateOf(initialDeskripsi) }

    var namaError by remember { mutableStateOf(false) }
    var hargaError by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title) },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = nama,
                    onValueChange = {
                        nama = it
                        namaError = false
                    },
                    label = { Text("Nama Servis") },
                    isError = namaError,
                    supportingText = if (namaError) {
                        { Text("Nama servis wajib diisi") }
                    } else null,
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                OutlinedTextField(
                    value = harga,
                    onValueChange = {
                        harga = it.filter { char -> char.isDigit() }
                        hargaError = false
                    },
                    label = { Text("Harga (Rp)") },
                    isError = hargaError,
                    supportingText = if (hargaError) {
                        { Text("Harga wajib diisi") }
                    } else null,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                OutlinedTextField(
                    value = deskripsi,
                    onValueChange = { deskripsi = it },
                    label = { Text("Deskripsi (opsional)") },
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 3
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    namaError = nama.isBlank()
                    hargaError = harga.isBlank() || harga.toIntOrNull() == null

                    if (!namaError && !hargaError) {
                        onSimpan(
                            nama.trim(),
                            harga.toInt(),
                            deskripsi.trim().ifBlank { null }
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
