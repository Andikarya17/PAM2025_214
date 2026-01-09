package com.example.bengkelku.ui.view.kendaraan

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.bengkelku.data.local.entity.Kendaraan
import com.example.bengkelku.viewmodel.kendaraan.KendaraanViewModel

@Composable
fun KendaraanScreen(
    viewModel: KendaraanViewModel,
    onTambahKendaraan: () -> Unit
) {
    val daftarKendaraan by viewModel.daftarKendaraan.collectAsState(initial = emptyList())

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {

        Text(
            text = "Kendaraan Saya",
            style = MaterialTheme.typography.headlineSmall
        )

        Spacer(Modifier.height(16.dp))

        Button(
            onClick = onTambahKendaraan,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Tambah Kendaraan")
        }

        Spacer(Modifier.height(16.dp))

        LazyColumn {
            items(daftarKendaraan) { kendaraan ->
                KendaraanItem(
                    kendaraan = kendaraan,
                    onHapus = {
                        viewModel.hapusKendaraan(kendaraan)
                    }
                )
            }
        }
    }
}
