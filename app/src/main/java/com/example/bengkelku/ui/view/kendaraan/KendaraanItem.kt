package com.example.bengkelku.ui.view.kendaraan

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.bengkelku.data.local.entity.Kendaraan

@Composable
fun KendaraanItem(
    kendaraan: Kendaraan,
    onHapus: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text("${kendaraan.merk} ${kendaraan.model}")
                Text("Plat: ${kendaraan.nomorPlat}")
                kendaraan.tahun?.let {
                    Text("Tahun: $it")
                }
            }

            TextButton(onClick = onHapus) {
                Text("Hapus", color = MaterialTheme.colorScheme.error)
            }
        }
    }
}
