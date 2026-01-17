package com.example.bengkelku.ui.view.kendaraan

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.bengkelku.ui.view.components.BaseScaffold
import com.example.bengkelku.viewmodel.kendaraan.AksiKendaraanState
import com.example.bengkelku.viewmodel.kendaraan.KendaraanViewModel

@Composable
fun TambahKendaraanScreen(
    viewModel: KendaraanViewModel,
    penggunaId: Int,
    onSelesai: () -> Unit,
    onBack: () -> Unit
) {
    var merk by remember { mutableStateOf("") }
    var model by remember { mutableStateOf("") }
    var nomorPlat by remember { mutableStateOf("") }
    var tahun by remember { mutableStateOf("") }

    val aksiState by viewModel.aksiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val isLoading = aksiState is AksiKendaraanState.Loading

    // Handle state changes
    LaunchedEffect(aksiState) {
        when (val state = aksiState) {
            is AksiKendaraanState.Berhasil -> {
                viewModel.resetState()
                onSelesai()
            }
            is AksiKendaraanState.Gagal -> {
                snackbarHostState.showSnackbar(state.pesan)
                viewModel.resetState()
            }
            else -> {}
        }
    }

    BaseScaffold(
        title = "Tambah Kendaraan",
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
                OutlinedTextField(
                    value = merk,
                    onValueChange = { merk = it },
                    label = { Text("Merk") },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !isLoading
                )

                Spacer(Modifier.height(8.dp))

                OutlinedTextField(
                    value = model,
                    onValueChange = { model = it },
                    label = { Text("Model") },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !isLoading
                )

                Spacer(Modifier.height(8.dp))

                OutlinedTextField(
                    value = nomorPlat,
                    onValueChange = { nomorPlat = it.uppercase() },
                    label = { Text("Nomor Plat") },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !isLoading
                )

                Spacer(Modifier.height(8.dp))

                OutlinedTextField(
                    value = tahun,
                    onValueChange = { tahun = it.filter { c -> c.isDigit() } },
                    label = { Text("Tahun (opsional)") },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !isLoading
                )

                Spacer(Modifier.height(16.dp))

                Button(
                    onClick = {
                        viewModel.tambahKendaraan(
                            merk = merk,
                            model = model,
                            nomorPlat = nomorPlat,
                            tahun = tahun.toIntOrNull()
                        )
                    },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = merk.isNotBlank() && model.isNotBlank() && nomorPlat.isNotBlank() && !isLoading
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            strokeWidth = 2.dp,
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                        Spacer(Modifier.width(8.dp))
                    }
                    Text(if (isLoading) "Menyimpan..." else "Simpan")
                }
            }
        }
    }
}
