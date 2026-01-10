package com.example.bengkelku.ui.view.kendaraan

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.bengkelku.data.local.entity.Kendaraan
import com.example.bengkelku.ui.view.components.BaseScaffold
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

    BaseScaffold(
        title = "Tambah Kendaraan",
        showBack = true,
        onBack = onBack
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            OutlinedTextField(
                value = merk,
                onValueChange = { merk = it },
                label = { Text("Merk") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = model,
                onValueChange = { model = it },
                label = { Text("Model") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = nomorPlat,
                onValueChange = { nomorPlat = it },
                label = { Text("Nomor Plat") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = tahun,
                onValueChange = { tahun = it },
                label = { Text("Tahun (opsional)") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(16.dp))

            Button(
                onClick = {
                    val kendaraan = Kendaraan(
                        penggunaId = penggunaId,
                        merk = merk,
                        model = model,
                        nomorPlat = nomorPlat,
                        tahun = tahun.toIntOrNull()
                    )
                    viewModel.tambahKendaraan(kendaraan)
                    onSelesai()
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Simpan")
            }
        }
    }
}

