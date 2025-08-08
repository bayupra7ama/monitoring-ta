package com.tugas.layout.ui.mahasiswa

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.tugas.data.repository.UserPreferences
import com.tugas.viewmodel.TaskViewModel
import kotlinx.coroutines.launch


@Composable
fun TaskDetailScreen(taskId: Int) {
    val context = LocalContext.current
    val viewModel: TaskViewModel = viewModel()
    val userPrefs = remember { UserPreferences(context) }
    val taskDetail by viewModel.taskDetail.collectAsState()
    val isUpdating by viewModel.isUpdatingSubtask
    val coroutineScope = rememberCoroutineScope()

    var showDialog by remember { mutableStateOf(false) }
    var subtaskTitle by remember { mutableStateOf("") }

    // 👇 untuk upload laporan
    var showUploadDialog by remember { mutableStateOf(false) }
    var selectedFileName by remember { mutableStateOf("") }
    var selectedFileUri by remember { mutableStateOf<Uri?>(null) }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        if (uri != null) {
            selectedFileName = uri.lastPathSegment ?: "file"
            selectedFileUri = uri
            showUploadDialog = true
        }
    }

    LaunchedEffect(taskId) {
        coroutineScope.launch {
            val token = userPrefs.getToken()
            if (token != null) {
                viewModel.fetchTaskDetail(token, taskId)
            }
        }
    }

    if (taskDetail == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }

    Column(Modifier.padding(16.dp)) {
        Text("Detail Tugas", fontSize = 22.sp, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(12.dp))

        Text("Judul: ${taskDetail!!.title}")
        Text("Status: ${taskDetail!!.status}")
        Text("Tanggal: ${taskDetail!!.start_date} s/d ${taskDetail!!.end_date}")

        Spacer(Modifier.height(16.dp))
        Text("Subtugas:", fontWeight = FontWeight.SemiBold)

        if (isUpdating) {
            LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
            Spacer(Modifier.height(8.dp))
        }

        if (taskDetail!!.subtasks.isNotEmpty()) {
            taskDetail!!.subtasks.forEach { subtask ->
                var showStatusDialog by remember { mutableStateOf(false) }

                val statusColor = when (subtask.status) {
                    "belum" -> Color.Red
                    "proses" -> Color.Yellow
                    "selesai" -> Color.Green
                    else -> Color.Gray
                }

                if (showStatusDialog) {
                    val statusOptions = listOf("belum", "proses", "selesai")
                    AlertDialog(
                        onDismissRequest = { showStatusDialog = false },
                        title = { Text("Ubah Status Subtugas") },
                        text = {
                            Column {
                                statusOptions.forEach { option ->
                                    TextButton(onClick = {
                                        coroutineScope.launch {
                                            val token = userPrefs.getToken()
                                            if (token != null) {
                                                viewModel.updateSubtaskStatus(token, subtask.id, option)
                                                showStatusDialog = false
                                            }
                                        }
                                    }) {
                                        Text(option.capitalize())
                                    }
                                }
                            }
                        },
                        dismissButton = {
                            TextButton(onClick = { showStatusDialog = false }) {
                                Text("Batal")
                            }
                        },
                        confirmButton = {}
                    )
                }

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    elevation = CardDefaults.cardElevation(2.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(subtask.title, fontWeight = FontWeight.SemiBold)
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                subtask.status,
                                color = statusColor,
                                fontSize = 12.sp
                            )
                            IconButton(onClick = { showStatusDialog = true }) {
                                Icon(Icons.Default.Edit, contentDescription = "Edit Status")
                            }
                        }
                    }
                }
            }
        } else {
            Text("Belum ada subtugas", color = Color.Gray)
        }

        Spacer(Modifier.height(16.dp))

        // tombol tambah subtugas
        Button(onClick = { showDialog = true }, modifier = Modifier.fillMaxWidth()) {
            Text("Tambah Subtugas")
        }

        Spacer(Modifier.height(8.dp))

        // tombol upload laporan
        Button(onClick = { launcher.launch("application/pdf") }, modifier = Modifier.fillMaxWidth()) {
            Text("Upload Laporan")
        }
    }

    // dialog tambah subtugas
    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            confirmButton = {
                TextButton(onClick = {
                    coroutineScope.launch {
                        val token = userPrefs.getToken()
                        if (!subtaskTitle.isBlank() && token != null) {
                            viewModel.addSubtask(token, taskId, subtaskTitle)
                            subtaskTitle = ""
                            showDialog = false
                        }
                    }
                }) {
                    Text("Tambah")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDialog = false }) {
                    Text("Batal")
                }
            },
            title = { Text("Tambah Subtugas") },
            text = {
                OutlinedTextField(
                    value = subtaskTitle,
                    onValueChange = { subtaskTitle = it },
                    label = { Text("Judul Subtugas") }
                )
            }
        )
    }

    // dialog konfirmasi upload
    if (showUploadDialog && selectedFileUri != null) {
        AlertDialog(
            onDismissRequest = { showUploadDialog = false },
            confirmButton = {
                TextButton(onClick = {
                    coroutineScope.launch {
                        val token = userPrefs.getToken()
                        if (token != null && selectedFileUri != null) {
                            viewModel.uploadProgressReport(token, taskId, selectedFileUri!!, context)
                            showUploadDialog = false
                        }
                    }
                }) {
                    Text("Upload")
                }
            },
            dismissButton = {
                TextButton(onClick = { showUploadDialog = false }) {
                    Text("Batal")
                }
            },
            title = { Text("Konfirmasi Upload") },
            text = {
                Text("Upload file: $selectedFileName ?")
            }
        )
    }
}



