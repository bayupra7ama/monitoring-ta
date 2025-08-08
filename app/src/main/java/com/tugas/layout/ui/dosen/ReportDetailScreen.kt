package com.tugas.layout.ui.dosen

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
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
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.tugas.data.repository.UserPreferences
import com.tugas.viewmodel.ReportDetailViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportDetailScreen(reportId: Int) {
    val context = LocalContext.current
    val prefs = remember { UserPreferences(context) }
    val viewModel: ReportDetailViewModel = viewModel()

    val report by viewModel.reportDetail.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    var showDialog by remember { mutableStateOf(false) }

    val coroutineScope = rememberCoroutineScope()

    // ✅ Fetch data on first render
    LaunchedEffect(Unit) {
        prefs.getToken()?.let { token ->
            viewModel.fetchReportDetail(token, reportId)
        }
    }

    // ✅ Dialog validasi

    if (showDialog) {
        ValidationDialog(
            onDismiss = { showDialog = false },
            onSubmit = { status, feedback ->
                coroutineScope.launch {
                    val token = prefs.getToken() // ✅ aman di coroutine
                    if (!token.isNullOrBlank()) {
                        viewModel.submitValidation(token, reportId, status, feedback)
                    }
                    showDialog = false
                }
            }
        )
    }

    // ✅ Scaffold for layout
    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Detail Laporan") })
        }
    ) { padding ->
        if (isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            report?.let { rpt ->
                Column(
                    modifier = Modifier
                        .padding(padding)
                        .padding(16.dp)
                ) {
                    Text("Mahasiswa: ${rpt.user.name}")
                    Text("Tugas: ${rpt.task.title}")
                    Text("Status: ${rpt.statusValidasi}")
                    rpt.feedback?.let {
                        Text(text = "Feedback: $it")
                    }
                    Spacer(Modifier.height(8.dp))
                    Button(onClick = {
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(rpt.fileUrl))
                        context.startActivity(intent)
                    }) {
                        Text("Lihat File")
                    }

                    Spacer(Modifier.height(12.dp))

                    // ✅ Ini dipindah ke dalam Scaffold
                    Button(onClick = { showDialog = true }) {
                        Text("Validasi Laporan")
                    }
                }
            } ?: Text("Data tidak ditemukan", modifier = Modifier.padding(16.dp))
        }
    }
}
