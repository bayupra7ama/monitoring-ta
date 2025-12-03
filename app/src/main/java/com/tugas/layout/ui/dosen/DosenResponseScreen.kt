package com.tugas.layout.ui.dosen

import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.rememberVectorPainter
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
import com.tugas.layout.ui.theme.GrayDark
import com.tugas.layout.ui.theme.GraySecondary

import com.tugas.viewmodel.DosenReportViewModel
import kotlinx.coroutines.launch
import androidx.compose.material.icons.filled.ArrowOutward
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.ui.text.style.TextOverflow

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Pending
import com.tugas.helper.timeAgo
import com.tugas.layout.ui.theme.GreenPrimary

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DosenProgressReportScreen(
    navController: NavController,
    onBackClick: () -> Unit
) {
    val context = LocalContext.current
    val viewModel: DosenReportViewModel = viewModel()
    val reports by viewModel.reports.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val prefs = remember { UserPreferences(context) }
    val coroutineScope = rememberCoroutineScope()

    // 🔄 Ambil token & fetch data
    LaunchedEffect(true) {
        coroutineScope.launch {
            prefs.getToken()?.let { token ->
                viewModel.fetchReports(token)
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Laporan Mahasiswa",
                        color = Color.Black,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.SemiBold
                    )
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
                // Background
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .clipToBounds()
                ) {
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

                    // Konten laporan
                    if (isLoading) {
                        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator()
                        }
                    } else {
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(horizontal = 16.dp),
                            contentPadding = PaddingValues(top = 16.dp, bottom = 16.dp) // Tambahkan padding bawah
                        ) {
                            items(reports) { report ->
                                val subtaskTitle = report.subtask?.title ?: "Subtugas Tanpa Judul"

                                ReportCardDosenItem(
                                    reportId = report.id,
                                    studentName = report.user.name,
                                    taskTitle = subtaskTitle,         // ⬅️ pakai judul subtask
                                    status = report.status_validasi,   // ⬅️ kalau di data class sudah pakai statusValidasi
                                    uploadTimestamp = report.created_at,
                                    navController = navController
                                )
                                Spacer(modifier = Modifier.height(16.dp))
                            }



                        }


                    }
                }
            }
        }
    )
}

@RequiresApi(Build.VERSION_CODES.O) // Diperlukan karena fungsi timeAgo
@Composable
fun ReportCardDosenItem(
    reportId: Int,
    studentName: String,
    taskTitle: String,
    status: String,
    uploadTimestamp: String, // Parameter baru untuk waktu upload (format ISO)
    navController: NavController
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        onClick = {
            // Aksi utama: Buka layar detail untuk validasi atau lihat feedback
            navController.navigate("report_detail/$reportId") // Sesuaikan dengan route Anda
        },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Box(modifier = Modifier.padding(16.dp)) {
            Column {
                // 1. INFORMASI UTAMA: Siapa & Apa
                Column(modifier = Modifier.padding(end = 40.dp)) { // Ruang untuk ikon panah
                    Text(
                        text = studentName,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = GrayDark,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = taskTitle,
                        fontSize = 14.sp,
                        color = GraySecondary,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))
                HorizontalDivider(color = Color.LightGray.copy(alpha = 0.5f))
                Spacer(modifier = Modifier.height(16.dp))

                // 2. FOOTER: Status & Waktu Upload
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Status laporan menggunakan Chip
                    StatusChip(status = status)

                    // Waktu upload laporan
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Schedule,
                            contentDescription = "Waktu Upload",
                            tint = GraySecondary,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = timeAgo(uploadTimestamp),
                            fontSize = 14.sp,
                            color = GraySecondary
                        )
                    }
                }
            }

            // 3. IKON AKSI: Navigasi ke detail
            IconButton(
                onClick = {
                    navController.navigate("report_detail/$reportId") // Sesuaikan dengan route Anda
                },
                modifier = Modifier.align(Alignment.TopEnd)
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowOutward,
                    contentDescription = "Buka Detail Laporan",
                    tint = GraySecondary
                )
            }
        }
    }
}


@Composable
private fun StatusChip(status: String) {
    val (icon, color, text) = when (status.lowercase()) {
        "selesai" -> Triple(Icons.Default.CheckCircle, GreenPrimary, "Selesai")
        "revisi"  -> Triple(Icons.Default.Pending, Color(0xFFFFA000), "Revisi")
        "belum"   -> Triple(Icons.Default.Info, GraySecondary, "Belum Dinilai")
        "pending"   -> Triple(Icons.Default.Info, GraySecondary, "Belum Dinilai")
        else      -> Triple(Icons.Default.Info, GraySecondary, "Tidak Diketahui")
    }

    Row(
        modifier = Modifier
            .background(color.copy(alpha = 0.1f), RoundedCornerShape(50))
            .padding(horizontal = 10.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = "Status: $text",
            tint = color,
            modifier = Modifier.size(18.dp)
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
            text = text,
            color = color,
            fontWeight = FontWeight.SemiBold,
            fontSize = 14.sp
        )
    }
}
