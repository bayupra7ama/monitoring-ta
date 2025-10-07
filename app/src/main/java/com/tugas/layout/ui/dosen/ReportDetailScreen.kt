package com.tugas.layout.ui.dosen

import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Article
import androidx.compose.material.icons.automirrored.filled.FactCheck
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.tugas.data.model.SubTask
import com.tugas.data.repository.UserPreferences
import com.tugas.layout.R
import com.tugas.layout.ui.theme.GrayDark
import com.tugas.layout.ui.theme.GraySecondary
import com.tugas.layout.ui.theme.GreenPrimary
import com.tugas.viewmodel.ReportDetailViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportDetailScreen(reportId: Int, navController: NavController, onBackClick: () -> Unit) {
    val context = LocalContext.current
    val prefs = remember { UserPreferences(context) }
    val userRole by prefs.roleFlow.collectAsState(initial = null)
    val viewModel: ReportDetailViewModel = viewModel()

    val report by viewModel.reportDetail.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    var showDialog by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        prefs.getToken()?.let { token ->
            viewModel.fetchReportDetail(token, reportId)
        }
    }

    if (showDialog) {
        ValidationDialog(
            onDismiss = { showDialog = false },
            onSubmit = { status, feedback ->
                coroutineScope.launch {
                    val token = prefs.getToken()
                    if (!token.isNullOrBlank()) {
                        viewModel.submitValidation(token, reportId, status, feedback)
                    }
                    showDialog = false
                }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Detail Laporan",
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
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xfff5f2f2))
                .padding(padding)
        ) {
            Image(
                painter = painterResource(id = R.drawable.buttom_curve),
                contentDescription = null,
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .size(300.dp)
                    .offset(x = (-100).dp)
            )

            if (isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else {
                report?.let { rpt ->
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                            .verticalScroll(rememberScrollState())
                    ) {
                        // --- KARTU INFORMASI UTAMA ---
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp),
                            shape = RoundedCornerShape(12.dp),
                            elevation = CardDefaults.cardElevation(4.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                InfoRow(label = "Mahasiswa", value = rpt.user.name)
                                Spacer(Modifier.height(12.dp))
                                InfoRow(label = "Judul Tugas", value = rpt.task.title)
                                Spacer(Modifier.height(12.dp))
                                StatusRow(label = "Status", value = rpt.statusValidasi)

                                rpt.feedback?.let { feedback ->
                                    if (feedback.isNotBlank()) {
                                        HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp))
                                        Text(
                                            text = "Feedback Dosen",
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            color = GrayDark
                                        )
                                        Spacer(Modifier.height(4.dp))
                                        Text(
                                            text = feedback,
                                            fontSize = 14.sp,
                                            color = GraySecondary,
                                            fontStyle = FontStyle.Italic
                                        )
                                    }
                                }
                            }
                        }

                        // --- KARTU SUBTUGAS ---
                        if (rpt.task.subtasks.isNotEmpty()) {
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 8.dp),
                                shape = RoundedCornerShape(12.dp),
                                elevation = CardDefaults.cardElevation(4.dp),
                                colors = CardDefaults.cardColors(containerColor = Color.White)
                            ) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Text(
                                        text = "Daftar Subtugas",
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = GrayDark
                                    )
                                    Spacer(Modifier.height(8.dp))
                                    rpt.task.subtasks.forEachIndexed { index, subtask ->
                                        SubtaskItem(subtask = subtask)
                                        if (index < rpt.task.subtasks.lastIndex) {
                                            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                                        }
                                    }
                                }
                            }
                        }

                        // --- TOMBOL AKSI ---
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 16.dp),
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            ActionButton(
                                text = "Lihat File",
                                icon = Icons.AutoMirrored.Filled.Article,
                                onClick = {
                                    navController.navigate("pdf_viewer/${Uri.encode(rpt.fileUrl)}")
                                },
                                modifier = Modifier.weight(1f)
                            )

                            if (userRole == "dosen") {
                                ActionButton(
                                    text = "Validasi",
                                    icon = Icons.AutoMirrored.Filled.FactCheck,
                                    onClick = { showDialog = true },
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }
                    }
                } ?: Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Data laporan tidak ditemukan.", color = GraySecondary)
                }
            }
        }
    }
}

@Composable
fun SubtaskItem(subtask: SubTask) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = subtask.title,
            modifier = Modifier.weight(1f),
            color = GrayDark,
            fontSize = 14.sp
        )
        Spacer(Modifier.width(16.dp))
        val statusColor = when (subtask.status.lowercase()) {
            "selesai" -> GreenPrimary
            "proses" -> Color(0xFFFFA726)
            else -> GraySecondary
        }
        Text(
            text = subtask.status.uppercase(),
            color = statusColor,
            fontWeight = FontWeight.Bold,
            fontSize = 12.sp
        )
    }
}

@Composable
fun InfoRow(label: String, value: String) {
    Column {
        Text(text = label, fontSize = 12.sp, color = GraySecondary)
        Text(text = value, fontSize = 16.sp, fontWeight = FontWeight.Medium, color = GrayDark)
    }
}

@Composable
fun StatusRow(label: String, value: String) {
    val statusColor = when (value.lowercase()) {
        "disetujui" -> GreenPrimary
        "pending" -> Color(0xFFFFA726)
        "ditolak" -> Color.Red
        else -> GrayDark
    }
    Column {
        Text(text = label, fontSize = 12.sp, color = GraySecondary)
        Text(
            text = value.replaceFirstChar { it.uppercase() },
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = statusColor
        )
    }
}

@Composable
fun ActionButton(
    text: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        modifier = modifier.height(48.dp),
        shape = RoundedCornerShape(10.dp),
        colors = ButtonDefaults.buttonColors(containerColor = GreenPrimary, contentColor = Color.White)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Center) {
            Icon(imageVector = icon, contentDescription = text)
            Spacer(Modifier.width(8.dp))
            Text(text = text, fontWeight = FontWeight.SemiBold)
        }
    }
}
