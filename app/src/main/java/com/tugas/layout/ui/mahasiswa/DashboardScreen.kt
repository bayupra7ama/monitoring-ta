package com.tugas.layout.ui.mahasiswa

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.viewmodel.compose.viewModel
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.tugas.data.TaskUIModel
import com.tugas.data.repository.UserPreferences
import com.tugas.layout.R
import com.tugas.layout.ui.theme.ChartActual
import com.tugas.layout.ui.theme.ChartPlanned
import com.tugas.layout.ui.theme.GreenPrimary
import com.tugas.layout.ui.theme.GrayDark
import com.tugas.layout.ui.theme.GraySecondary
import com.tugas.viewmodel.ProjectDetailViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    projectId: Int,
    onTaskClick: (Int) -> Unit = {},
    onAddTaskClick: () -> Unit = {},
    onNavigateToNotifications: () -> Unit
) {
    val context = LocalContext.current
    val viewModel: ProjectDetailViewModel = viewModel()
    val projectDetail by viewModel.projectDetail.collectAsState()
    val burndownData by viewModel.burndownData.collectAsState()

    val isLoading by viewModel.isLoading.collectAsState()
    val coroutineScope = rememberCoroutineScope()
    val prefs = remember { UserPreferences(context) }

    LaunchedEffect(true) {
        coroutineScope.launch {
            val token = prefs.getToken()
            token?.let {
                println("🚀 Fetch project detail dengan token: $it, projectId: $projectId")
                viewModel.fetchProjectDetail(projectId, it)
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.ic_logo_poltek),
                            contentDescription = "POLBENG Logo",
                            modifier = Modifier.size(40.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "POLBENG",
                            color = GrayDark,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                },
                actions = {
                    Box( // Gunakan Box untuk menumpuk Image dan Row IconButton
                        modifier = Modifier
                            // Menggunakan tinggi standar untuk TopAppBar single-row di Material 3
                            .height(TopAppBarDefaults.TopAppBarExpandedHeight)
                            .width(120.dp) // Atur lebar yang cukup untuk gradient dan konten
                    ) {
                        // Background hijau gelombang di sudut kanan atas TopAppBar
                        Image(
                            painter = painterResource(id = R.drawable.ic_gradiend_card),
                            contentDescription = null,
                            contentScale = ContentScale.FillBounds,
                            modifier = Modifier
                                .fillMaxSize() // Isi Box ini
                                .align(Alignment.TopEnd) // Posisikan di kanan atas Box
                        )
                        Row(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(end = 8.dp), // Padding agar tidak terlalu mepet
                            horizontalArrangement = Arrangement.End,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            IconButton(onClick = onNavigateToNotifications) {
                                Icon(
                                    imageVector = Icons.Default.Notifications,
                                    contentDescription = "Notifikasi",
                                    tint = Color.Black // Icon notifikasi putih di atas gradient
                                )
                            }
                            Spacer(modifier = Modifier.width(4.dp))

                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White // Latar belakang topbar putih
                )
            )
        }
    ) { paddingValues ->
        if (isLoading) {
            Box(Modifier.fillMaxSize().padding(paddingValues), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
            return@Scaffold
        }

        projectDetail?.let { project ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues) // Penting untuk mengaplikasikan padding dari Scaffold
                    .background(Color.White) // Latar belakang keseluruhan screen
            ) {
                // Konten yang Tetap (Judul, Deskripsi, Progress Tugas, Burndown Chart)
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp) // Padding horizontal untuk konten tetap
                ) {
                    Text(project.title, fontSize = 22.sp, fontWeight = FontWeight.Bold, color = GrayDark)
                    Text(project.description, fontSize = 14.sp, color = GraySecondary)

                    Spacer(modifier = Modifier.height(16.dp))

                    Text("Progress Tugas", fontWeight = FontWeight.SemiBold, color = GrayDark, fontSize = 18.sp)

                    Spacer(modifier = Modifier.height(8.dp))

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(2.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(250.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                burndownData?.let { burndown ->
                                    BurndownChart(
                                        labels = burndown.labels,
                                        actualValues = burndown.actual,
                                        idealValues = burndown.ideal
                                    )
                                } ?: Text("Data burndown chart tidak tersedia.")
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Daftar Tugas", fontWeight = FontWeight.SemiBold, color = GrayDark, fontSize = 18.sp)
                        TextButton(onClick = onAddTaskClick) {
                            Text("Tambah +", color = GreenPrimary, fontWeight = FontWeight.Medium)
                        }
                    }
                }

                // Bagian yang bisa di-scroll (hanya daftar tugas)
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState()) // Hanya bagian ini yang bisa di-scroll
                        .padding(horizontal = 16.dp) // Padding horizontal untuk konten scrollable
                ) {
                    project.tasks.forEach { task ->
                        TaskItem(
                            TaskUIModel(
                                title = task.title,
                                endDate = task.end_date,
                                status = task.status,
                                progress = if (task.status == "selesai") 1f else 0f,
                                statusColor = when (task.status) {
                                    "belum" -> Color.Red
                                    "proses" -> Color.Yellow
                                    "selesai" -> GreenPrimary
                                    else -> GraySecondary
                                }
                            ),
                            onClick = {
                                onTaskClick(task.id)
                            }
                        )
                    }
                    Spacer(modifier = Modifier.height(16.dp)) // Memberi sedikit ruang di bawah card terakhir
                }
            }
        }
    }
}

@Composable
fun TaskItem(task: TaskUIModel, onClick: () -> Unit = {}) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(2.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(12.dp)
    ) {
        Box(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(12.dp)) {
                Text(task.title, fontWeight = FontWeight.SemiBold, color = GrayDark)
                Text("Deadline: ${task.endDate}", fontSize = 12.sp, color = GraySecondary)

                Spacer(modifier = Modifier.height(8.dp))

                LinearProgressIndicator(
                    progress = task.progress,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp)
                        .clip(RoundedCornerShape(4.dp)),
                    color = task.statusColor,
                    trackColor = Color(0xFFE0E0E0)
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    task.status.uppercase(),
                    fontSize = 12.sp,
                    color = task.statusColor,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

@Composable
fun BurndownChart(
    labels: List<String>,
    actualValues: List<Int>,
    idealValues: List<Int>,
    modifier: Modifier = Modifier
) {
    val maxDataValue = (idealValues.maxOrNull() ?: 0)

    AndroidView(
        modifier = modifier
            .fillMaxWidth()
            .height(300.dp),
        factory = { context ->
            val chart = LineChart(context)

            val plannedEntries = idealValues.mapIndexed { index, value ->
                Entry(index.toFloat(), value.toFloat())
            }

            // Tambahkan titik 0 di akhir agar garis turun sampai 0
            val adjustedActualValues = actualValues.toMutableList()
//            if (adjustedActualValues.last() != 0) {
//                adjustedActualValues.add(0)
//            }

            val actualEntries = adjustedActualValues.mapIndexed { index, value ->
                Entry(index.toFloat(), value.toFloat())
            }


            val plannedDataSet = LineDataSet(plannedEntries, "Tugas yang Direncanakan").apply {
                color = ChartPlanned.toArgb()
                lineWidth = 2.0f
                setCircleColor(ChartPlanned.toArgb())
                circleRadius = 4f
                mode = LineDataSet.Mode.LINEAR
                setDrawFilled(false)
                setDrawValues(false)
            }

            val actualDataSet = LineDataSet(actualEntries, "Tugas yang Diselesaikan").apply {
                color = ChartActual.toArgb()
                lineWidth = 2.0f
                setCircleColor(ChartActual.toArgb())
                circleRadius = 4f
                mode = LineDataSet.Mode.LINEAR
                setDrawFilled(false)
                setDrawValues(false)
            }

            chart.data = LineData(plannedDataSet, actualDataSet)

            chart.apply {
                description.isEnabled = false
                setTouchEnabled(true)
                isDragEnabled = true
                setScaleEnabled(true)
                setPinchZoom(true)

                legend.apply {
                    isEnabled = true
                    verticalAlignment = Legend.LegendVerticalAlignment.BOTTOM
                    horizontalAlignment = Legend.LegendHorizontalAlignment.CENTER
                    orientation = Legend.LegendOrientation.HORIZONTAL
                    setDrawInside(false)
                    textSize = 10f
                    textColor = GrayDark.toArgb()
                }

                axisRight.isEnabled = false




                xAxis.apply {
                    valueFormatter = IndexAxisValueFormatter(labels)
                    position = XAxis.XAxisPosition.BOTTOM
                    granularity = 1f
                    isGranularityEnabled = true
                    axisMinimum = 0f
                    axisMaximum = (labels.size-1).toFloat()   // X tidak melebihi label
                    setDrawGridLines(true)
                    gridColor = Color(0xFFE0E0E0).toArgb()
                    textSize = 10f
                    textColor = GrayDark.toArgb()
                }

                axisLeft.apply {
                    textSize = 10f
                    axisMinimum = 0f
                    granularity = 1f
                    isGranularityEnabled = true
                    axisMaximum = maxDataValue.toFloat()        // Y tidak melebihi data
                    setDrawGridLines(true)
                    gridColor = Color(0xFFE0E0E0).toArgb()
                    textColor = GrayDark.toArgb()
                }

                setExtraOffsets(5f, 5f, 5f, 5f)
                animateX(1000)
                invalidate()
            }
            chart
        }
    )
}

