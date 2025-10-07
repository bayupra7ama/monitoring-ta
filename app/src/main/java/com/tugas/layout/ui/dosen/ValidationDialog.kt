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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ValidationDialog(
    onDismiss: () -> Unit,
    onSubmit: (String, String?) -> Unit
) {
    var status by remember { mutableStateOf("ditolak") } // <--- PERBAIKAN DI SINI
    var feedback by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = {
                onSubmit(status, feedback.ifBlank { null })
            }) {
                Text(
                    "Kirim",
                    color = GreenPrimary,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 16.sp
                )
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(
                    "Batal",
                    color = GraySecondary,
                    fontSize = 16.sp
                )
            }
        },
        title = {
            Text(
                "Validasi Laporan",
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
                color = GrayDark
            )
        },
        text = {
            Column {
                Text(
                    "Pilih Status:",
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 16.sp,
                    color = GrayDark
                )
                Spacer(Modifier.height(8.dp))
                Row(modifier = Modifier.fillMaxWidth()) {
                    // Ubah ini:
                    // listOf("disetujui", "ditolak", "revisi").forEach { s ->
                    // Menjadi ini:
                    listOf(
                        Pair("disetujui", "disetujui"),
                        Pair("ditolak", "ditolak"),
                    ).forEach { (label, valueToSend) ->
                        Row(
                            modifier = Modifier
                                .weight(1f)
                                .selectable(
                                    selected = status == valueToSend, // Pilih berdasarkan nilai yang dikirim
                                    onClick = { status = valueToSend },
                                    role = Role.RadioButton
                                )
                                .padding(vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = status == valueToSend,
                                onClick = null,
                                colors = RadioButtonDefaults.colors(
                                    selectedColor = GreenPrimary,
                                    unselectedColor = GraySecondary
                                )
                            )
                            Text(
                                label.replaceFirstChar { it.uppercase() }, // Tampilkan label "Revisi"
                                color = GrayDark,
                                fontSize = 15.sp
                            )
                        }
                    }
                }
                Spacer(Modifier.height(16.dp))
                OutlinedTextField(
                    value = feedback,
                    onValueChange = { feedback = it },
                    label = {
                        Text(
                            "Feedback (opsional)",
                            color = GraySecondary
                        )
                    },
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 4,
                    shape = RoundedCornerShape(10.dp),
                    colors = OutlinedTextFieldDefaults.colors( // Perubahan di sini!
                        focusedBorderColor = GreenPrimary,
                        unfocusedBorderColor = GraySecondary,
                        cursorColor = GreenPrimary,
                        // Sekarang gunakan textColor langsung di OutlinedTextField atau di contentColor
                        // contentColor = GrayDark // Ini akan mengubah warna teks input juga
                    ),
                    textStyle = TextStyle(color = GrayDark) // Mengatur warna teks input di sini
                )
            }
        },
        shape = RoundedCornerShape(16.dp),
        containerColor = Color.White
    )
}