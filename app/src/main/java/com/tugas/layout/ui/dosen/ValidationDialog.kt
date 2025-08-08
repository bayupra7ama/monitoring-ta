package com.tugas.layout.ui.dosen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun ValidationDialog(
    onDismiss: () -> Unit,
    onSubmit: (String, String?) -> Unit
) {
    var status by remember { mutableStateOf("disetujui") }
    var feedback by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = {
                onSubmit(status, feedback.ifBlank { null })
            }) {
                Text("Kirim")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Batal")
            }
        },
        title = { Text("Validasi Laporan") },
        text = {
            Column {
                Text("Pilih Status:")
                Row {
                    listOf("disetujui", "ditolak").forEach { s ->
                        Row(
                            Modifier
                                .clickable { status = s }
                                .padding(end = 16.dp)
                        ) {
                            RadioButton(selected = status == s, onClick = { status = s })
                            Text(s.replaceFirstChar { it.uppercase() })
                        }
                    }
                }
                Spacer(Modifier.height(8.dp))
                OutlinedTextField(
                    value = feedback,
                    onValueChange = { feedback = it },
                    label = { Text("Feedback (opsional)") },
                    maxLines = 4
                )
            }
        }
    )
}
