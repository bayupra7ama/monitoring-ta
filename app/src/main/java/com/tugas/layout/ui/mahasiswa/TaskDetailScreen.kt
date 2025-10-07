package com.tugas.layout.ui.mahasiswa

import android.content.res.Resources.Theme
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults

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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.tugas.data.repository.UserPreferences
import com.tugas.layout.R
import com.tugas.layout.ui.theme.ChartPlanned
import com.tugas.layout.ui.theme.GreenPrimary
import com.tugas.layout.ui.theme.PrimaryGreen
import com.tugas.viewmodel.TaskViewModel
import kotlinx.coroutines.launch
import android.provider.OpenableColumns // Import ini untuk mendapatkan nama file dan ukuran
import android.widget.Toast // Import Toast untuk pesan singkat

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskDetailScreen(
    taskId: Int,
    navController: NavController,
    onBackClick: () -> Unit
) {
    val context = LocalContext.current
    val viewModel: TaskViewModel = viewModel()
    val userPrefs = remember { UserPreferences(context) }
    val taskDetail by viewModel.taskDetail.collectAsState()
    val isUpdating by viewModel.isUpdatingSubtask
    val isUploading by viewModel.isUploading.collectAsState()

    val coroutineScope = rememberCoroutineScope()

    var showDialog by remember { mutableStateOf(false) }
    var subtaskTitle by remember { mutableStateOf("") }

    var showUploadDialog by remember { mutableStateOf(false) }
    var selectedFileName by remember { mutableStateOf("") }
    var selectedFileUri by remember { mutableStateOf<Uri?>(null) }
    var showFileSizeErrorDialog by remember { mutableStateOf(false) } // State baru untuk dialog error ukuran file

    val MAX_FILE_SIZE_BYTES = 5 * 1024 * 1024 // 5 MB

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        if (uri != null) {
            // Dapatkan informasi file termasuk ukuran
            context.contentResolver.query(uri, null, null, null, null)?.use { cursor ->
                if (cursor.moveToFirst()) {
                    val sizeIndex = cursor.getColumnIndex(OpenableColumns.SIZE)
                    val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)

                    val fileSize = if (sizeIndex != -1) cursor.getLong(sizeIndex) else -1L
                    val fileName = if (nameIndex != -1) cursor.getString(nameIndex) else "file"

                    if (fileSize != -1L && fileSize > MAX_FILE_SIZE_BYTES) {
                        showFileSizeErrorDialog = true // Tampilkan dialog error jika terlalu besar
                    } else {
                        selectedFileName = fileName
                        selectedFileUri = uri
                        showUploadDialog = true
                    }
                }
            }
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

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Detail Tugas",
                        color = Color.Black,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_arrow_back),
                            contentDescription = "Kembali",
                            tint = Color.Unspecified
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        },
        content = { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xfff5f2f2))
                    .padding(innerPadding)
            ) {
                // 🌀 Background melengkung atas dan bawah
                Image(
                    painter = painterResource(id = R.drawable.top_curve),
                    contentDescription = null,
                    contentScale = ContentScale.Fit,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .matchParentSize()
                        .offset(x = 40.dp, y = (-20).dp)
                )

                Image(
                    painter = painterResource(id = R.drawable.buttom_curve),
                    contentDescription = null,
                    contentScale = ContentScale.Fit,
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .size(300.dp)
                        .offset(x = (-100).dp)
                )

                // ✨ Konten utama
                if (taskDetail == null) {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                } else {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                    ) {
                        // 🔹 Judul dan deskripsi gaya modern
                        Text(
                            text = taskDetail!!.title,
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black
                        )
                        Text(
                            text = ("Status : " + taskDetail!!.status) ?: "Tidak ada deskripsi",
                            fontSize = 14.sp,
                            color = Color.Gray
                        )
                        Text(
                            "Tanggal: ${taskDetail!!.start_date} s/d ${taskDetail!!.end_date}",
                            color = Color.Gray,
                            fontSize = 12.sp
                        )




                        Spacer(Modifier.height(12.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                "Daftar Subtugas",
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 18.sp,
                                color = Color.Black
                            )
                            TextButton(onClick = { showDialog = true }){
                                Text("Tambah +",     color = Color(0xFF1E88E5),
                                    fontWeight = FontWeight.Medium)
                            }

                            // 🧩 Dialog Tambah Subtugas
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
                                            Text("Tambah", color = GreenPrimary, fontWeight = FontWeight.SemiBold)
                                        }
                                    },
                                    dismissButton = {
                                        TextButton(onClick = { showDialog = false }) {
                                            Text("Batal", color = Color.Gray)
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


                        }

                        if (isUpdating) {
                            LinearProgressIndicator(modifier = Modifier.fillMaxWidth().background(
                                PrimaryGreen))
                            Spacer(Modifier.height(8.dp))
                        }

                        // 🔸 Daftar Subtugas
                        if (taskDetail!!.subtasks.isNotEmpty()) {
                            taskDetail!!.subtasks.forEach { subtask ->
                                var showStatusDialog by remember { mutableStateOf(false) }

                                val statusColor = when (subtask.status) {
                                    "belum" -> Color.Red
                                    "proses" -> Color(0xFFFFC107)
                                    "selesai" -> Color(0xFF4CAF50)
                                    else -> Color.Gray
                                }

                                if (showStatusDialog) {
                                    AlertDialog(
                                        onDismissRequest = { showStatusDialog = false },
                                        title = { Text("Ubah Status Subtugas") },
                                        text = {
                                            val statusOptions = listOf("belum", "proses", "selesai")
                                            Column {
                                                statusOptions.forEach { option ->
                                                    TextButton(onClick = {
                                                        coroutineScope.launch {
                                                            val token = userPrefs.getToken()
                                                            if (token != null) {
                                                                viewModel.updateSubtaskStatus(
                                                                    token,
                                                                    subtask.id,
                                                                    option
                                                                )
                                                                showStatusDialog = false
                                                            }
                                                        }
                                                    }) {
                                                        Text(option.replaceFirstChar { it.uppercase() })
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

                                // 🧱 Card Subtask versi baru
                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 4.dp),
                                    colors = CardDefaults.cardColors(containerColor = Color.White),
                                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                                    shape = RoundedCornerShape(10.dp)
                                ) {
                                    Column(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(horizontal = 10.dp, vertical = 8.dp)
                                    ) {
                                        Text(
                                            subtask.title,
                                            fontWeight = FontWeight.SemiBold,
                                            color = Color.Black,
                                            fontSize = 14.sp,
                                            lineHeight = 18.sp
                                        )

                                        Spacer(modifier = Modifier.height(4.dp))

                                        Row(
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically,
                                            modifier = Modifier.fillMaxWidth()
                                        ) {
                                            Text(
                                                subtask.status.uppercase(),
                                                color = statusColor,
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Medium
                                            )
                                            IconButton(
                                                onClick = { showStatusDialog = true },
                                                modifier = Modifier.size(22.dp)
                                            ) {
                                                Icon(
                                                    Icons.Default.Edit,
                                                    contentDescription = "Edit",
                                                    tint = Color.Gray,
                                                    modifier = Modifier.size(18.dp)
                                                )
                                            }
                                        }
                                    }
                                }

                            }
                        } else {
                            Text("Belum ada subtugas", color = Color.Gray)
                        }

                        Spacer(Modifier.height(16.dp))

                        // 🔹 Tombol Upload Laporan
                        Button(
                            onClick = { launcher.launch("application/pdf") },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = GreenPrimary, // 💚 Warna tombol
                                contentColor = Color.White      // 🏷 Warna teks di atas tombol
                            )

                        ) {
                            Text("Upload Laporan")
                        }

                    }
                }
            }

            // 🧩 Dialog Tambah Subtugas (pastikan hanya ada satu instance dialog ini)
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
                            Text("Tambah", color = GreenPrimary, fontWeight = FontWeight.SemiBold)
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { showDialog = false }) {
                            Text("Batal", color = Color.Gray)
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

// 🧩 Dialog Upload File
            if (showUploadDialog && selectedFileUri != null) {
                AlertDialog(
                    onDismissRequest = { if (!isUploading) showUploadDialog = false },
                    confirmButton = {
                        TextButton(
                            onClick = {
                                coroutineScope.launch {
                                    val token = userPrefs.getToken()
                                    if (token != null && selectedFileUri != null && !isUploading) {
                                        viewModel.uploadProgressReport(token, taskId, selectedFileUri!!, context)
                                        showUploadDialog = false
                                    }
                                }
                            },
                            enabled = !isUploading
                        ) {
                            if (isUploading) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(20.dp),
                                    strokeWidth = 2.dp
                                )
                            } else {
                                Text("Upload", color = GreenPrimary, fontWeight = FontWeight.SemiBold)
                            }
                        }
                    },
                    dismissButton = {
                        TextButton(
                            onClick = { if (!isUploading) showUploadDialog = false },
                            enabled = !isUploading
                        ) {
                            Text("Batal", color = Color.Gray)
                        }
                    },
                    title = { Text("Konfirmasi Upload") },
                    text = { Text("Upload file: $selectedFileName ?") }
                )
            }

            // 🚫 Dialog Error Ukuran File
            if (showFileSizeErrorDialog) {
                AlertDialog(
                    onDismissRequest = { showFileSizeErrorDialog = false },
                    confirmButton = {
                        TextButton(onClick = { showFileSizeErrorDialog = false }) {
                            Text("OK", color = GreenPrimary, fontWeight = FontWeight.SemiBold)
                        }
                    },
                    title = { Text("Ukuran File Terlalu Besar") },
                    text = { Text("Ukuran file maksimal adalah 5MB. Silakan pilih file lain.") }
                )
            }

        }


    )
}
