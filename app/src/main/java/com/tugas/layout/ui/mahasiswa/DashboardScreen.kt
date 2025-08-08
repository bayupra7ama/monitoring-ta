package com.tugas.layout.ui.mahasiswa

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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.viewmodel.compose.viewModel
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.tugas.data.TaskUIModel
import com.tugas.data.model.BurndownChartData
import com.tugas.data.repository.UserPreferences
import com.tugas.viewmodel.ProjectDetailViewModel
import kotlinx.coroutines.launch


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    projectId: Int,
    onTaskClick: (Int) -> Unit = {} ,

            onAddTaskClick: () -> Unit = {}
) {
    val context = LocalContext.current
    val viewModel: ProjectDetailViewModel = viewModel()
    val projectDetail by viewModel.projectDetail.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val coroutineScope = rememberCoroutineScope()
    val prefs = remember { UserPreferences(context) }

    var selectedView by remember { mutableStateOf("Project") }
    var selectedTaskTitle by remember { mutableStateOf("") }

    LaunchedEffect(true) {
        coroutineScope.launch {
            val token = prefs.getToken()
            token?.let {
                viewModel.fetchProjectDetail(projectId, it)
            }
        }
    }

    if (isLoading) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }

    projectDetail?.let { project ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Text(project.title, fontSize = 22.sp, fontWeight = FontWeight.Bold)
            Text(project.description, fontSize = 14.sp)

            Spacer(modifier = Modifier.height(16.dp))

            // 🔘 Switch Dropdown
            var switchExpanded by remember { mutableStateOf(false) }
            val viewOptions = listOf("Project", "Per Task")

            ExposedDropdownMenuBox(
                expanded = switchExpanded,
                onExpandedChange = { switchExpanded = !switchExpanded }
            ) {
                OutlinedTextField(
                    value = selectedView,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Lihat Burndown") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = switchExpanded) },
                    modifier = Modifier
                        .menuAnchor()
                        .fillMaxWidth()
                )
                ExposedDropdownMenu(
                    expanded = switchExpanded,
                    onDismissRequest = { switchExpanded = false }
                ) {
                    viewOptions.forEach {
                        DropdownMenuItem(
                            text = { Text(it) },
                            onClick = {
                                selectedView = it
                                switchExpanded = false
                                if (it == "Per Task") {
                                    selectedTaskTitle = project.burndown.per_task.firstOrNull()?.task_title ?: ""
                                }
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            if (selectedView == "Per Task") {
                // 🔽 Pilih Task
                var taskDropdownExpanded by remember { mutableStateOf(false) }
                val taskTitles = project.burndown.per_task.map { it.task_title }

                ExposedDropdownMenuBox(
                    expanded = taskDropdownExpanded,
                    onExpandedChange = { taskDropdownExpanded = !taskDropdownExpanded }
                ) {
                    OutlinedTextField(
                        value = selectedTaskTitle,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Pilih Task") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = taskDropdownExpanded) },
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth()
                    )
                    ExposedDropdownMenu(
                        expanded = taskDropdownExpanded,
                        onDismissRequest = { taskDropdownExpanded = false }
                    ) {
                        taskTitles.forEach {
                            DropdownMenuItem(
                                text = { Text(it) },
                                onClick = {
                                    selectedTaskTitle = it
                                    taskDropdownExpanded = false
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))
            }

            Text("Progress Tugas", fontWeight = FontWeight.Medium)

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .background(Color.LightGray, RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) {
                val chartData: BurndownChartData = if (selectedView == "Project") {
                    project.burndown.project
                } else {
                    val selected = project.burndown.per_task.firstOrNull { it.task_title == selectedTaskTitle }
                    BurndownChartData(
                        labels = selected?.labels ?: emptyList(),
                        actual = selected?.actual ?: emptyList()
                    )
                }


                BurndownChart(
                    labels = chartData.labels,
                    values = chartData.actual
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Daftar Tugas", fontWeight = FontWeight.Medium)
                TextButton(onClick = onAddTaskClick) {
                    Text("Tambah +")
                }
            }

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
                            "selesai" -> Color.Green
                            else -> Color.Gray
                        }
                    ),
                    onClick = {
                        onTaskClick(task.id) // ✅ ini akan panggil navigasi ke TaskDetail
                    }
                )
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
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(task.title, fontWeight = FontWeight.SemiBold)
            Text("Deadline: ${task.endDate}", fontSize = 12.sp, color = Color.Gray)

            Spacer(modifier = Modifier.height(8.dp))

            LinearProgressIndicator(
                progress = task.progress,
                modifier = Modifier.fillMaxWidth(),
                color = task.statusColor
            )

            Text(task.status.uppercase(), fontSize = 12.sp, color = task.statusColor)
        }
    }
}


@Composable
fun BurndownChart(
    labels: List<String>,
    values: List<Int>,
    modifier: Modifier = Modifier
) {
    AndroidView(
        modifier = modifier
            .fillMaxWidth()
            .height(250.dp), // sedikit tinggi biar lega
        factory = { context ->
            val chart = LineChart(context)

            val entries = values.mapIndexed { index, value ->
                Entry(index.toFloat(), value.toFloat())
            }

            val dataSet = LineDataSet(entries, "Sisa Task").apply {
                color = Color(0xFF1976D2).hashCode() // biru material
                valueTextSize = 10f
                lineWidth = 2.5f
                circleRadius = 5f
                setCircleColor(Color(0xFF1976D2).hashCode())
                setDrawFilled(true)
                fillColor = Color(0xFFBBDEFB).hashCode() // soft fill
                mode = LineDataSet.Mode.CUBIC_BEZIER // smooth line
            }

            chart.apply {
                data = LineData(dataSet)
                setTouchEnabled(true)
                setPinchZoom(true)
                description.isEnabled = false
                legend.isEnabled = false
                axisRight.isEnabled = false
                setExtraOffsets(10f, 10f, 10f, 10f)

                xAxis.apply {
                    valueFormatter = IndexAxisValueFormatter(labels)
                    position = XAxis.XAxisPosition.BOTTOM
                    granularity = 1f
                    setDrawGridLines(false)
                    textSize = 10f
                    labelRotationAngle = -45f // miring biar ga numpuk
                }

                axisLeft.apply {
                    textSize = 10f
                    gridColor = android.graphics.Color.LTGRAY
                }

                invalidate()
            }

            chart
        }
    )
}


@Preview(showBackground = true)
@Composable
fun DashboardPreview() {
}
