package com.tugas.layout.ui.dosen

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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.DrawerDefaults.shape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
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
import com.tugas.data.TaskUIModel
import com.tugas.data.api.RetrofitInstance
import com.tugas.data.model.Project
import com.tugas.data.model.UserProfile
import com.tugas.data.repository.NotificationRepository
import com.tugas.data.repository.NotificationViewModelFactory
import com.tugas.data.repository.UserPreferences
import com.tugas.layout.R
import com.tugas.layout.ui.mahasiswa.BurndownChart
import com.tugas.layout.ui.mahasiswa.TaskItem
import com.tugas.layout.ui.theme.ChartActual
import com.tugas.layout.ui.theme.ChartPlanned
import com.tugas.layout.ui.theme.GreenPrimary
import com.tugas.layout.ui.theme.GrayDark
import com.tugas.layout.ui.theme.GraySecondary
import com.tugas.viewmodel.DosenDashboardViewModel
import com.tugas.viewmodel.NotificationViewModel
import com.tugas.viewmodel.TaskWithSubtasks
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DosenDashboardScreen(
    onNavigateToNotifications: () -> Unit
) {
    val context = LocalContext.current
    val prefs = remember { UserPreferences(context) }
    val coroutineScope = rememberCoroutineScope()
    val viewModel: DosenDashboardViewModel = viewModel()

    val mahasiswaList by viewModel.mahasiswaList.collectAsState()
    val projectList by viewModel.projectList.collectAsState()
    val projectDetail by viewModel.selectedProjectDetail.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    val notificationViewModel: NotificationViewModel = viewModel(
        factory = NotificationViewModelFactory(NotificationRepository(RetrofitInstance.api))
    )
    val unreadCount by notificationViewModel.unreadCount.collectAsState()

    var selectedMahasiswa by remember { mutableStateOf<UserProfile?>(null) }
    var selectedProject by remember { mutableStateOf<Project?>(null) }

    val scrollState = rememberScrollState()

    LaunchedEffect(Unit) {
        prefs.getToken()?.let { token ->
            viewModel.fetchMahasiswa(token)
            notificationViewModel.fetchUnreadCount(token)
        }
    }

    LaunchedEffect(selectedProject) {
        prefs.getToken()?.let { token ->
            selectedProject?.let { viewModel.fetchDashboardByProject(token, it.id) }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
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
                    Box(
                        modifier = Modifier
                            .height(TopAppBarDefaults.TopAppBarExpandedHeight)
                            .width(120.dp)
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.ic_gradiend_card),
                            contentDescription = null,
                            contentScale = ContentScale.FillBounds,
                            modifier = Modifier
                                .fillMaxSize()
                                .align(Alignment.TopEnd)
                        )
                        Row(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(end = 8.dp),
                            horizontalArrangement = Arrangement.End,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            BadgedBox(
                                badge = {
                                    if (unreadCount > 0) {
                                        Badge {
                                            Text(unreadCount.toString(), fontSize = 10.sp)
                                        }
                                    }
                                }
                            ) {
                                IconButton(onClick = onNavigateToNotifications) {
                                    Icon(
                                        imageVector = Icons.Default.Notifications,
                                        contentDescription = "Notifikasi",
                                        tint = Color.Black
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.width(4.dp))
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White
                )
            )
        }
    ) { innerPadding ->
        if (isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            Column(
                modifier = Modifier
                    .padding(innerPadding)
                    .padding(horizontal = 16.dp) // Padding horizontal untuk konten utama
                    .verticalScroll(scrollState)
            ) {
                Spacer(Modifier.height(16.dp)) // Spacer setelah TopAppBar
                Text(
                    "Dashboard Dosen",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = GrayDark
                )
                Text(
                    "Monitor proyek dan tugas mahasiswa Anda.",
                    fontSize = 14.sp,
                    color = GraySecondary
                )
                Spacer(Modifier.height(24.dp))

                // Dropdown Mahasiswa
                CustomExposedDropdown(
                    label = "Pilih Mahasiswa",
                    options = mahasiswaList.map { it.name },
                    selectedOption = selectedMahasiswa?.name,
                    onOptionSelected = { name ->
                        selectedMahasiswa = mahasiswaList.find { it.name == name }
                        selectedProject = null // Reset project ketika mahasiswa berubah

                        coroutineScope.launch {
                            val token = prefs.getToken()
                            selectedMahasiswa?.let { mhs ->
                                if (token != null) viewModel.fetchProjectsByMahasiswa(token, mhs.id)
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(Modifier.height(12.dp))

                // Dropdown Project
                if (selectedMahasiswa != null) {
                    CustomExposedDropdown(
                        label = "Pilih Project",
                        options = projectList.map { it.title },
                        selectedOption = selectedProject?.title,
                        onOptionSelected = { title ->
                            selectedProject = projectList.find { it.title == title }
                        },
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                Spacer(Modifier.height(16.dp))

                projectDetail?.let { detail ->
                    Text("Progress Tugas", fontWeight = FontWeight.SemiBold, color = GrayDark, fontSize = 18.sp)
                    Spacer(Modifier.height(8.dp))
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
                                BurndownChart(
                                    labels = detail.burndown.labels,
                                    actualValues = detail.burndown.actual,
                                    idealValues = detail.burndown.ideal
                                )
                            }
                        }
                    }

                    Spacer(Modifier.height(24.dp))
                    Text("Daftar Tugas", fontWeight = FontWeight.SemiBold, color = GrayDark, fontSize = 18.sp)
                    Spacer(Modifier.height(8.dp))
                    detail.project.tasks.forEach { task ->
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
                            }
                        )
                    }
                }
                Spacer(Modifier.height(16.dp)) // Memberi sedikit ruang di bawah card terakhir
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomExposedDropdown(
    label: String,
    options: List<String>,
    selectedOption: String?,
    onOptionSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }

    Box(modifier = modifier) {
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded }
        ) {
            TextField(
                value = selectedOption ?: "",
                onValueChange = {},
                readOnly = true,
                label = { Text(label) },
                trailingIcon = {
                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                },
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                    disabledContainerColor = Color.White, // Tambahkan ini jika Anda ingin mengontrol saat disabled
                    focusedIndicatorColor = GreenPrimary,
                    unfocusedIndicatorColor = GraySecondary,
                    focusedLabelColor = GreenPrimary,
                    unfocusedLabelColor = GraySecondary,
                    cursorColor = GreenPrimary,
                ),
                shape = RoundedCornerShape(12.dp), // Bentuk rounded
                modifier = Modifier
                    .fillMaxWidth()
                    .menuAnchor()
            )

            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                options.forEach { option ->
                    DropdownMenuItem(
                        text = { Text(option) },
                        onClick = {
                            onOptionSelected(option)
                            expanded = false
                        }
                    )
                }
            }
        }
    }
}


@Composable
fun TaskCardDosen(task: com.tugas.data.model.TaskWithSubtasks) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        elevation = CardDefaults.cardElevation(2.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(task.title, fontWeight = FontWeight.SemiBold, color = GrayDark)
            Spacer(Modifier.height(4.dp))
            if (task.subtasks.isNotEmpty()) {
                task.subtasks.forEach { subtask ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Anda bisa menyesuaikan warna atau ikon berdasarkan status subtask
                        val subtaskStatusColor = when (subtask.status.lowercase()) {
                            "belum" -> Color.Red
                            "proses" -> Color(0xFFFFA000) // Oranye
                            "selesai" -> GreenPrimary
                            else -> GraySecondary
                        }
                        Text(
                            "- ${subtask.title}",
                            fontSize = 13.sp,
                            color = GrayDark,
                            modifier = Modifier.weight(1f)
                        )
                        Text(
                            subtask.status.uppercase(),
                            fontSize = 12.sp,
                            color = subtaskStatusColor,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            } else {
                Text("Tidak ada sub-tugas", fontSize = 13.sp, color = GraySecondary)
            }
        }
    }
}