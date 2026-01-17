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
import com.example.bengkelku.data.remote.model.ServisResponse
import com.example.bengkelku.ui.view.components.BaseScaffold
import com.example.bengkelku.viewmodel.servis.AksiServisState
import com.example.bengkelku.viewmodel.servis.ListServisState
import com.example.bengkelku.viewmodel.servis.ServisAdminViewModel
import java.text.NumberFormat
import java.util.*

@Composable
fun KelolaServisScreen(
    viewModel: ServisAdminViewModel,
    onBack: () -> Unit
) {
    val listState by viewModel.listState.collectAsState()
    val aksiState by viewModel.aksiState.collectAsState()
    val servisUntukEdit by viewModel.servisUntukEdit.collectAsState()

    var showTambahDialog by remember { mutableStateOf(false) }
    var showHapusDialog by remember { mutableStateOf<ServisResponse?>(null) }

    val snackbarHostState = remember { SnackbarHostState() }
    val isLoading = aksiState is AksiServisState.Loading

    // ✅ FIX: Close dialogs AFTER success/failure state
    LaunchedEffect(aksiState) {
        when (val state = aksiState) {
            is AksiServisState.Berhasil -> {
                // Close dialogs on success
                showTambahDialog = false
                showHapusDialog = null
                snackbarHostState.showSnackbar(state.pesan)
                viewModel.resetState()
            }
            is AksiServisState.Gagal -> {
                // Keep dialog open on failure so user can retry
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
                    onClick = { showTambahDialog = true },
                    containerColor = MaterialTheme.colorScheme.primary
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Tambah Servis")
                }
            }
        ) { innerPadding ->
            when (val state = listState) {
                is ListServisState.Loading -> {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(paddingValues)
                            .padding(innerPadding),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }
                is ListServisState.Success -> {
                    if (state.data.isEmpty()) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(paddingValues)
                                .padding(innerPadding),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Belum ada servis",
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
                            items(state.data, key = { it.id }) { servis ->
                                ServisItemCard(
                                    servis = servis,
                                    onEdit = { viewModel.pilihUntukEdit(servis) },
                                    onHapus = { showHapusDialog = servis },
                                    isLoading = isLoading
                                )
                            }
                        }
                    }
                }
                is ListServisState.Error -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(paddingValues)
                            .padding(innerPadding),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = state.message,
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.error
                        )
                        Spacer(Modifier.height(16.dp))
                        Button(onClick = { viewModel.loadServis() }) {
                            Text("Coba Lagi")
                        }
                    }
                }
            }
        }
    }

    // Dialog Tambah Servis
    if (showTambahDialog) {
        ServisFormDialog(
            title = "Tambah Servis",
            onDismiss = { 
                if (!isLoading) showTambahDialog = false 
            },
            onSimpan = { nama, harga, deskripsi ->
                // ✅ FIX: Don't close dialog here - wait for success state
                viewModel.tambahServis(nama, harga, deskripsi)
            },
            isLoading = isLoading
        )
    }

    // Dialog Edit Servis
    servisUntukEdit?.let { servis ->
        ServisFormDialog(
            title = "Edit Servis",
            initialNama = servis.namaServis,
            initialHarga = servis.harga.toString(),
            initialDeskripsi = servis.deskripsi ?: "",
            onDismiss = { 
                if (!isLoading) viewModel.batalEdit() 
            },
            onSimpan = { nama, harga, deskripsi ->
                // ✅ FIX: Don't close dialog here - wait for success state
                viewModel.updateServis(
                    id = servis.id,
                    namaServis = nama,
                    harga = harga,
                    deskripsi = deskripsi
                )
            },
            isLoading = isLoading
        )
    }

    // Dialog Konfirmasi Hapus
    showHapusDialog?.let { servis ->
        AlertDialog(
            onDismissRequest = { 
                if (!isLoading) showHapusDialog = null 
            },
            title = { Text("Hapus Servis") },
            text = { Text("Yakin ingin menghapus servis \"${servis.namaServis}\"?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        // ✅ FIX: Don't close dialog here - wait for success state
                        viewModel.hapusServis(servis.id)
                    },
                    enabled = !isLoading
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(16.dp),
                            strokeWidth = 2.dp
                        )
                    } else {
                        Text("Hapus", color = MaterialTheme.colorScheme.error)
                    }
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showHapusDialog = null },
                    enabled = !isLoading
                ) {
                    Text("Batal")
                }
            }
        )
    }
}

@Composable
private fun ServisItemCard(
    servis: ServisResponse,
    onEdit: () -> Unit,
    onHapus: () -> Unit,
    isLoading: Boolean
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
                    IconButton(onClick = onEdit, enabled = !isLoading) {
                        Icon(Icons.Default.Edit, contentDescription = "Edit")
                    }
                    IconButton(onClick = onHapus, enabled = !isLoading) {
                        Icon(
                            Icons.Default.Delete,
                            contentDescription = "Hapus",
                            tint = MaterialTheme.colorScheme.error
                        )
                    }
                }
            }

            Spacer(Modifier.height(8.dp))

            // Status indicator
            Surface(
                color = if (servis.isActive)
                    MaterialTheme.colorScheme.primaryContainer
                else
                    MaterialTheme.colorScheme.errorContainer,
                shape = MaterialTheme.shapes.small
            ) {
                Text(
                    text = if (servis.isActive) "Aktif" else "Nonaktif",
                    style = MaterialTheme.typography.labelMedium,
                    color = if (servis.isActive)
                        MaterialTheme.colorScheme.onPrimaryContainer
                    else
                        MaterialTheme.colorScheme.onErrorContainer,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
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
    onSimpan: (nama: String, harga: Int, deskripsi: String?) -> Unit,
    isLoading: Boolean = false
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
                    singleLine = true,
                    enabled = !isLoading
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
                    singleLine = true,
                    enabled = !isLoading
                )

                OutlinedTextField(
                    value = deskripsi,
                    onValueChange = { deskripsi = it },
                    label = { Text("Deskripsi (opsional)") },
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 3,
                    enabled = !isLoading
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
                },
                enabled = !isLoading
            ) {
                if (isLoading) {
                    CircularProgressIndicator(modifier = Modifier.size(16.dp), strokeWidth = 2.dp)
                } else {
                    Text("Simpan")
                }
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss, enabled = !isLoading) {
                Text("Batal")
            }
        }
    )
}
