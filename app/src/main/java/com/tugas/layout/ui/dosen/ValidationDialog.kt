package com.tugas.layout.ui.dosen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.OutlinedTextFieldDefaults // Import ini
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.TextStyle // Import ini
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tugas.layout.ui.theme.GreenPrimary
import com.tugas.layout.ui.theme.GraySecondary
import com.tugas.layout.ui.theme.GrayDark

@Composable
fun ValidationDialog(
    onDismiss: () -> Unit,
    onSubmit: (String, String?) -> Unit
) {
    var status by remember { mutableStateOf("revisi") } // default
    var feedback by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = { onSubmit(status, feedback.ifBlank { null }) }) {
                Text("Kirim", color = GreenPrimary, fontWeight = FontWeight.SemiBold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Batal", color = GraySecondary)
            }
        },
        title = { Text("Validasi Laporan", fontWeight = FontWeight.Bold, fontSize = 20.sp) },
        text = {
            Column {
                Text("Pilih Status:", fontWeight = FontWeight.SemiBold)

                Spacer(Modifier.height(8.dp))

                listOf(
                    "belum" to "Belum",
                    "revisi" to "Revisi",
                    "selesai" to "Selesai"
                ).forEach { (value, label) ->
                    Row(
                        modifier = Modifier
                            .selectable(
                                selected = status == value,
                                onClick = { status = value },
                                role = Role.RadioButton
                            )
                            .padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = status == value,
                            onClick = null,
                            colors = RadioButtonDefaults.colors(
                                selectedColor = GreenPrimary,
                                unselectedColor = GraySecondary
                            )
                        )
                        Text(label, color = GrayDark)
                    }
                }

                Spacer(Modifier.height(16.dp))

                OutlinedTextField(
                    value = feedback,
                    onValueChange = { feedback = it },
                    label = { Text("Feedback (opsional)") },
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        }
    )
}
