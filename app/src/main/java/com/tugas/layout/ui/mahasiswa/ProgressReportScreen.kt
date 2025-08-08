package com.tugas.layout.ui.mahasiswa

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
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Card
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.tugas.data.model.SubTaskData
import com.tugas.data.repository.UserPreferences
import com.tugas.viewmodel.ProgressReportViewModel
import kotlinx.coroutines.launch

@Composable
fun ProgressReportScreen() {
    val context = LocalContext.current
    val viewModel: ProgressReportViewModel = viewModel()
    val reports by viewModel.reports.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    val prefs = remember { UserPreferences(context) }
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(true) {
        coroutineScope.launch {
            val token = prefs.getToken()
            token?.let {
                viewModel.fetchMyReports(it)
            }
        }
    }

    if (isLoading) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text("Laporan Progres", fontSize = 20.sp, fontWeight = FontWeight.Bold)

        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn {
            items(reports) { report ->
                ReportCard(
                    reportTitle = report.task?.title ?: "Tanpa Nama Tugas",
                    subtasks = report.task?.subtasks ?: emptyList(),
                    status = report.status_validasi,
                    feedback = report.feedback,
                    fileUrl = report.file_url
                )
                Spacer(modifier = Modifier.height(12.dp))
            }
        }
    }
}


@Composable
fun ReportCard(
    reportTitle: String,
    subtasks: List<SubTaskData>,
    status: String,
    feedback: String?,
    fileUrl: String
) {
    val context = LocalContext.current

    Card(Modifier.fillMaxWidth()) {
        Column(Modifier.padding(16.dp)) {
            Text(reportTitle, fontWeight = FontWeight.Bold)

            Spacer(Modifier.height(4.dp))
            subtasks.forEach { subtask ->
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        tint = when (subtask.status) {
                            "selesai" -> Color.Green
                            "proses" -> Color.Yellow
                            "belum" -> Color.Red
                            else -> Color.Gray
                        },
                        contentDescription = null
                    )
                    Spacer(Modifier.width(4.dp))
                    Text(subtask.title)
                }
            }

            Spacer(Modifier.height(8.dp))
            Text("Status Validasi: $status")

            if (!feedback.isNullOrBlank()) {
                Text("Feedback: $feedback", fontWeight = FontWeight.SemiBold)
            }

            Spacer(Modifier.height(8.dp))
            Row {
                IconButton(onClick = {
                    // preview via browser
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(fileUrl))
                    context.startActivity(intent)
                }) {
                    Icon(Icons.Default.Visibility, contentDescription = "Lihat")
                }
                IconButton(onClick = {
                    // download via browser
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(fileUrl))
                    context.startActivity(intent)
                }) {
                    Icon(Icons.Default.Download, contentDescription = "Download")
                }
            }
        }
    }
}


