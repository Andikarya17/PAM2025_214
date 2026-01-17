package com.example.bengkelku.ui.view.kendaraan

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.bengkelku.data.remote.model.KendaraanResponse
import com.example.bengkelku.ui.view.components.BaseScaffold
import com.example.bengkelku.viewmodel.kendaraan.AksiKendaraanState
import com.example.bengkelku.viewmodel.kendaraan.KendaraanViewModel
import com.example.bengkelku.viewmodel.kendaraan.ListKendaraanState

@Composable
fun KendaraanScreen(
    viewModel: KendaraanViewModel,
    onTambahKendaraan: () -> Unit,
    onBack: () -> Unit
) {
    val listState by viewModel.listState.collectAsState()
    val aksiState by viewModel.aksiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    // Handle aksi state changes
    LaunchedEffect(aksiState) {
        when (val state = aksiState) {
            is AksiKendaraanState.Berhasil -> {
                snackbarHostState.showSnackbar(state.message)
                viewModel.resetState()
            }
            is AksiKendaraanState.Gagal -> {
                snackbarHostState.showSnackbar(state.pesan)
                viewModel.resetState()
            }
            else -> {}
        }
    }

    BaseScaffold(
        title = "Kendaraan Saya",
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
                Button(
                    onClick = onTambahKendaraan,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Tambah Kendaraan")
                }

                Spacer(Modifier.height(16.dp))

                when (val state = listState) {
                    is ListKendaraanState.Loading -> {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator()
                        }
                    }
                    is ListKendaraanState.Success -> {
                        if (state.data.isEmpty()) {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "Belum ada kendaraan",
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        } else {
                            LazyColumn {
                                items(state.data, key = { it.id }) { kendaraan ->
                                    KendaraanItemApi(
                                        kendaraan = kendaraan,
                                        onHapus = {
                                            viewModel.hapusKendaraan(kendaraan.id)
                                        },
                                        isLoading = aksiState is AksiKendaraanState.Loading
                                    )
                                }
                            }
                        }
                    }
                    is ListKendaraanState.Error -> {
                        Column(
                            modifier = Modifier.fillMaxSize(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = state.message,
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.error
                            )
                            Spacer(Modifier.height(16.dp))
                            Button(onClick = { viewModel.loadKendaraan() }) {
                                Text("Coba Lagi")
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun KendaraanItemApi(
    kendaraan: KendaraanResponse,
    onHapus: () -> Unit,
    isLoading: Boolean
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(12.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "${kendaraan.merk} ${kendaraan.model}",
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = "Plat: ${kendaraan.nomorPlat}",
                    style = MaterialTheme.typography.bodyMedium
                )
                kendaraan.tahun?.let {
                    Text(
                        text = "Tahun: $it",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            TextButton(
                onClick = onHapus,
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
        }
    }
}
