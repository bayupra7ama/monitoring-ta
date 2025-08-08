package com.tugas.layout.ui.mahasiswa

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

import android.app.DatePickerDialog
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box

import androidx.compose.runtime.*
import androidx.lifecycle.viewmodel.compose.viewModel
import com.tugas.data.repository.UserPreferences
import com.tugas.viewmodel.TaskViewModel
import kotlinx.coroutines.launch
import java.util.Calendar


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTaskScreen(
    projectId: Int,

    onSubmit: (TaskInput) -> Unit = {}
) {
    var title by remember { mutableStateOf("") }
    var startDate by remember { mutableStateOf("") }
    var endDate by remember { mutableStateOf("") }
    var status by remember { mutableStateOf("belum") }
    var statusExpanded by remember { mutableStateOf(false) }
    val viewModel: TaskViewModel = viewModel()
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    val userPrefs = remember { UserPreferences(context) }

    val isLoading by viewModel.isLoading
    val taskResponse by viewModel.taskResponse
    val errorMessage by viewModel.errorMessage

    val statusOptions = listOf("belum", "proses", "selesai")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Text("Tambah Tugas", fontSize = 20.sp, fontWeight = FontWeight.Bold)

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = title,
            onValueChange = { title = it },
            label = { Text("Judul Tugas") },
            modifier = Modifier.fillMaxWidth()
        )

        DatePickerField("Tanggal Mulai", startDate) { startDate = it }


        DatePickerField("Tanggal Selesai", endDate) { endDate = it }


        Spacer(modifier = Modifier.height(8.dp))

        Text("Status", modifier = Modifier.padding(top = 4.dp))

        ExposedDropdownMenuBox(
            expanded = statusExpanded,
            onExpandedChange = { statusExpanded = !statusExpanded }
        ) {
            OutlinedTextField(
                value = status,
                onValueChange = {},
                readOnly = true,
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = statusExpanded) },
                modifier = Modifier
                    .menuAnchor()
                    .fillMaxWidth()
            )
            ExposedDropdownMenu(
                expanded = statusExpanded,
                onDismissRequest = { statusExpanded = false }
            ) {
                statusOptions.forEach {
                    DropdownMenuItem(
                        text = { Text(it.capitalize()) },
                        onClick = {
                            status = it
                            statusExpanded = false
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                coroutineScope.launch {
                    val token = userPrefs.getToken() ?: return@launch
                    viewModel.addTask(token, projectId, com.tugas.data.model.TaskInput(title, startDate, endDate, status))
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Simpan Tugas")
        }

    }
}
data class TaskInput(
    val title: String,
    val start_date: String,
    val end_date: String,
    val status: String
)

@Composable
fun DatePickerField(
    label: String,
    selectedDate: String,
    onDateSelected: (String) -> Unit
) {
    val context = LocalContext.current
    val calendar = Calendar.getInstance()

    val datePicker = remember {
        DatePickerDialog(
            context,
            { _, year, month, dayOfMonth ->
                val date = String.format("%04d-%02d-%02d", year, month + 1, dayOfMonth)
                onDateSelected(date)
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
    }

    // 👉 Bungkus dengan Box + clickable
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { datePicker.show() }
    ) {
        OutlinedTextField(
            value = selectedDate,
            onValueChange = {},
            label = { Text(label) },
            readOnly = true,
            modifier = Modifier.fillMaxWidth(),
            enabled = false // ❗ ini penting agar klik hanya trigger Box luar
        )
    }
}

