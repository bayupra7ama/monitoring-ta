package com.tugas.layout.ui.mahasiswa

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.Image // Tambahkan import ini
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues // Tambahkan import ini
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset // Tambahkan import ini
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds // Tambahkan import ini
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale // Tambahkan import ini
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.tugas.data.model.MyReportResponse
import com.tugas.data.model.SubTaskData
import com.tugas.data.model.TaskDatas
import com.tugas.data.repository.UserPreferences
import com.tugas.layout.R
import com.tugas.layout.ui.theme.GreenPrimary // Pastikan ini masih relevan jika Anda ingin menggunakan warna GreenPrimary
import com.tugas.layout.ui.theme.GrayDark
import com.tugas.layout.ui.theme.GraySecondary
import com.tugas.viewmodel.ProgressReportViewModel
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale
import androidx.compose.material.icons.automirrored.filled.ListAlt
import androidx.compose.material.icons.filled.ArrowOutward
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Pending
import androidx.compose.material3.TextButton
import androidx.compose.ui.text.style.TextOverflow


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProgressReportScreen(
    onBackClick: () -> Unit,
    navController: NavController,

    // Ingat untuk mengganti `Unit` ini dengan `navController.popBackStack()` di tempat Anda memanggilnya
) {
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

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Laporan Progres",
                        color = Color.Black,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Kembali",
                            tint = Color.Black
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White
                )
            )
        },
        content = { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xfff5f2f2)) // Warna background utama dari CodiaMainView
                    .padding(innerPadding) // Penting agar konten tidak tumpang tindih dengan TopAppBar
            ) {
                // Background dengan bentuk melengkung dan elemen grafis dari CodiaMainView
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .fillMaxSize()
                        .clipToBounds()
                ) {
                    // 🟢 Gambar di sudut kanan atas
                    Image(
                        painter = painterResource(id = R.drawable.top_curve),
                        contentDescription = null,
                        contentScale = ContentScale.Fit,
                        modifier = Modifier
                            .align(Alignment.TopEnd) // ✅ Sudut kanan atas
                            .matchParentSize()
                            .offset(x = 40.dp, y = (-20).dp) // opsional, geser sedikit keluar
                    )

                    // 🔵 Gambar di sudut kiri bawah
                    Image(
                        painter = painterResource(id = R.drawable.buttom_curve),
                        contentDescription = null,
                        contentScale = ContentScale.Fit,
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .size(300.dp)// ✅ Sudut kiri bawah
                            .offset(x = (-100).dp, ) // opsional, geser sedikit keluar
                    )

                    // ✨ Konten laporan progres (LazyColumn) tetap di tengah
                    if (isLoading) {
                        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator()
                        }
                    } else {
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(horizontal = 16.dp),
                            contentPadding = PaddingValues(top = 16.dp)
                        ) {
                            items(reports) { report ->
                                ReportCardItem(report = report, navController = navController)
                                Spacer(modifier = Modifier.height(16.dp))
                            }
                        }
                    }
                }
            }
        }
    )
}


@Composable
fun ReportCardItem(report: MyReportResponse, navController: NavController) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        onClick = {
            // Aksi utama saat kartu di-klik adalah navigasi ke detail
            navController.navigate("report_detail/${report.id}") // Ganti "report_detail" sesuai route Anda
        },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Box(modifier = Modifier.padding(16.dp)) {
            Column {
                // BAGIAN HEADER: Judul
                Text(
                    text = report.task?.title ?: "Tugas Tidak Bernama",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = GrayDark,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.padding(end = 40.dp) // Beri ruang untuk ikon panah
                )

                Spacer(modifier = Modifier.height(12.dp))

                // BAGIAN BODY: Ringkasan subtugas
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ListAlt,
                        contentDescription = "Subtugas",
                        tint = GraySecondary,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "${report.task?.subtasks?.size ?: 0} Subtugas",
                        fontSize = 14.sp,
                        color = GraySecondary
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // BAGIAN FOOTER: Status dan Link File
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Komponen status yang lebih modern
                    StatusChip(status = report.status_validasi)

                    // Tombol untuk melihat file jika ada
                    report.file_url?.let { fileUrl ->
                        TextButton(
                            onClick = {
                                navController.navigate("pdf_viewer/${Uri.encode(fileUrl)}")
                            }
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_pdf_icon),
                                contentDescription = "Lihat PDF",
                                tint = Color.Red,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(text = "Lihat File", color = GrayDark)
                        }
                    }
                }
            }

            // IKON PANAH DI KANAN ATAS
            IconButton(
                onClick = {
                    navController.navigate("report_detail/${report.id}") // Ganti "report_detail" sesuai route Anda
                },
                modifier = Modifier.align(Alignment.TopEnd)
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowOutward,
                    contentDescription = "Lihat Detail Laporan",
                    tint = GraySecondary
                )
            }
        }
    }
}


@Composable
private fun StatusChip(status: String) {
    val (icon, color, text) = when (status.lowercase()) {
        "disetujui" -> Triple(Icons.Default.CheckCircle, GreenPrimary, "Disetujui")
        "pending" -> Triple(Icons.Default.Pending, Color(0xFFFFA000), "Pending")
        "ditolak" -> Triple(Icons.Default.Cancel, Color.Red, "Ditolak")
        else -> Triple(Icons.Default.Info, GraySecondary, "Tidak Diketahui")
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





