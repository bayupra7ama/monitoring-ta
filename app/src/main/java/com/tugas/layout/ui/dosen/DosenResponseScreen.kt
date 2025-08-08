package com.tugas.layout.ui.dosen

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.tugas.data.repository.UserPreferences
import com.tugas.viewmodel.DosenReportViewModel
import kotlinx.coroutines.launch


@Composable
fun DosenProgressReportScreen() {
    val context = LocalContext.current
    val viewModel: DosenReportViewModel = viewModel()
    val reports by viewModel.reports.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    val prefs = remember { UserPreferences(context) }
    val coroutineScope = rememberCoroutineScope()
    var isLoggedIn by remember { mutableStateOf(false) }

    // 🔄 Ambil token & fetch data
    LaunchedEffect(true) {
        coroutineScope.launch {
            val token = prefs.getToken()
            if (token != null) {
                isLoggedIn = true
                viewModel.fetchReports(token)
            }
        }
    }

    when {
        isLoading -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }

        !isLoggedIn -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Silakan login terlebih dahulu.")
            }
        }

        else -> {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Laporan Mahasiswa", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(16.dp))

                LazyColumn {
                    items(reports.size) { index ->
                        val report = reports[index]
                        ReportCardDosen(
                            studentName = report.user.name,
                            taskTitle = report.task.title,
                            status = report.status_validasi,
                            feedback = report.feedback.toString(),
                            fileUrl = report.file_url
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                    }
                }


            }
        }
    }
}

@Composable
fun ReportCardDosen(
    studentName: String,
    taskTitle: String,
    status: String,
    feedback: String?,
    fileUrl: String
) {
    val context = LocalContext.current

    Card(Modifier.fillMaxWidth()) {
        Column(Modifier.padding(16.dp)) {
            Text("Nama Mahasiswa: $studentName", fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(4.dp))

            Text("Tugas: $taskTitle")
            Spacer(Modifier.height(4.dp))

            Text("Status Validasi: $status")
            if (!feedback.isNullOrBlank()) {
                Text("Feedback: $feedback", fontWeight = FontWeight.SemiBold)
            }

            Spacer(Modifier.height(8.dp))
            Row {
                IconButton(onClick = {
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(fileUrl))
                    context.startActivity(intent)
                }) {
                    Icon(Icons.Default.Visibility, contentDescription = "Lihat")
                }
                IconButton(onClick = {
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(fileUrl))
                    context.startActivity(intent)
                }) {
                    Icon(Icons.Default.Download, contentDescription = "Download")
                }
            }
        }
    }
}


