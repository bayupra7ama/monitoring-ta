package com.tugas.layout.ui.dosen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.tugas.data.api.RetrofitInstance
import com.tugas.data.model.Project
import com.tugas.data.model.Task
import com.tugas.data.model.TaskResponse
import com.tugas.data.model.UserProfile
import com.tugas.data.repository.NotificationRepository
import com.tugas.data.repository.NotificationViewModelFactory
import com.tugas.data.repository.UserPreferences
import com.tugas.layout.ui.mahasiswa.BurndownChart
import com.tugas.viewmodel.DosenDashboardViewModel
import com.tugas.viewmodel.NotificationViewModel
import com.tugas.viewmodel.TaskWithSubtasks
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DosenDashboardScreen(
    onNavigateToNotifications: () -> Unit // <--- tambahkan parameter navigasi
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

    // Ambil data awal
    LaunchedEffect(Unit) {
        prefs.getToken()?.let { viewModel.fetchMahasiswa(it) }
    }
    LaunchedEffect(Unit) {
        prefs.getToken()?.let {
            viewModel.fetchMahasiswa(it)
            notificationViewModel.fetchUnreadCount(it) // ⬅️ wajib panggil agar badge bisa muncul
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
                title = {Text("Dashboard Dosen", fontSize = 22.sp, fontWeight = FontWeight.Bold)
                        Spacer(Modifier.height(12.dp))},

                actions = {
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
                                contentDescription = "Notifikasi"
                            )
                        }
                    }
                }

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
                    .padding(16.dp)
                    .verticalScroll(scrollState)
            ) {


                // 🔽 Dropdown Mahasiswa
                ExposedDropdown(
                    label = "Pilih Mahasiswa",
                    options = mahasiswaList.map { it.name },
                    selectedOption = selectedMahasiswa?.name,
                    onOptionSelected = { name ->
                        selectedMahasiswa = mahasiswaList.find { it.name == name }
                        selectedProject = null

                        coroutineScope.launch {
                            val token = prefs.getToken()
                            selectedMahasiswa?.let { mhs ->
                                if (token != null) viewModel.fetchProjectsByMahasiswa(token, mhs.id)
                            }
                        }
                    }


                )

                Spacer(Modifier.height(12.dp))

                // 🔽 Dropdown Project
                if (selectedMahasiswa != null) {
                    ExposedDropdown(
                        label = "Pilih Project",
                        options = projectList.map { it.title },
                        selectedOption = selectedProject?.title,
                        onOptionSelected = { title ->
                            selectedProject = projectList.find { it.title == title }
                        }
                    )
                }

                Spacer(Modifier.height(16.dp))

                projectDetail?.let { detail ->
                    Text("Burndown Chart", fontWeight = FontWeight.SemiBold)
                    BurndownChart(
                        labels = detail.burndown.labels,
                        values = detail.burndown.actual
                    )

                    Spacer(Modifier.height(12.dp))
                    Text("Daftar Tugas", fontWeight = FontWeight.SemiBold)

                    detail.project.tasks.forEach { task ->
                        TaskCard(task)
                    }
                }
            }
        }
    }
}


@Composable
fun TaskCard(task: TaskWithSubtasks) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Column(Modifier.padding(12.dp)) {
            Text(task.title, fontWeight = FontWeight.Bold)
            task.subtasks.forEach {
                Text("- ${it.title} (${it.status})", fontSize = 13.sp)
            }
        }
    }
}
