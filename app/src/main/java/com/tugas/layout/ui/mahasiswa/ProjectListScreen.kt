package com.tugas.layout.ui.mahasiswa

import androidx.compose.foundation.Image
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowOutward
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.tugas.data.api.RetrofitInstance
import com.tugas.data.model.Project
import com.tugas.data.repository.NotificationRepository
import com.tugas.data.repository.NotificationViewModelFactory
import com.tugas.data.repository.UserPreferences
import com.tugas.layout.R
import com.tugas.layout.ui.theme.GrayDark
import com.tugas.layout.ui.theme.GraySecondary
import com.tugas.layout.ui.theme.GreenPrimary
import com.tugas.viewmodel.NotificationViewModel
import com.tugas.viewmodel.ProjectViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProjectListScreen(
    onProjectClick: (Project) -> Unit,
    onAddProjectClick: () -> Unit,
    onNavigateToNotifications: () -> Unit
) {
    val context = LocalContext.current
    val projectViewModel: ProjectViewModel = viewModel()
    val notificationViewModel: NotificationViewModel = viewModel(
        factory = NotificationViewModelFactory(NotificationRepository(RetrofitInstance.api))
    )

    val projects by projectViewModel.projects.collectAsState()
    val isLoading by projectViewModel.isLoading.collectAsState()
    val unreadCount by notificationViewModel.unreadCount.collectAsState()

    val userPrefs = remember { UserPreferences(context) }
    val coroutineScope = rememberCoroutineScope()

    // 🔹 Fetch project dan unread notification
    LaunchedEffect(Unit) {
        coroutineScope.launch {
            val token = userPrefs.getToken()
            if (!token.isNullOrEmpty()) {
                projectViewModel.fetchProjects(token)
                notificationViewModel.fetchUnreadCount(token)
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Image(
                            painter = painterResource(id = R.drawable.ic_logo_poltek),
                            contentDescription = "Logo",
                            modifier = Modifier.size(40.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Project",
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
                            // 🔹 Notifikasi badge otomatis
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
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAddProjectClick,
                containerColor = GreenPrimary
            ) {
                Icon(Icons.Default.Add, contentDescription = "Tambah Project", tint = Color.White)
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            if (isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else if (projects.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Belum ada project", color = GraySecondary)
                }
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    items(projects) { project ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(16.dp))
                                .clickable { onProjectClick(project) },
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                        ) {
                            Box(modifier = Modifier.fillMaxWidth()) {
                                // 🔹 Ikon panah kanan atas
                                Icon(
                                    imageVector = Icons.Default.ArrowOutward,
                                    contentDescription = "Lihat Detail",
                                    tint = GraySecondary,
                                    modifier = Modifier
                                        .align(Alignment.TopEnd)
                                        .padding(12.dp)
                                        .size(20.dp)
                                        .clickable { onProjectClick(project) }
                                )

                                Column(modifier = Modifier.padding(16.dp)) {
                                    Text(
                                        text = project.title,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 16.sp,
                                        color = GrayDark
                                    )

                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = project.description ?: "Tidak ada deskripsi",
                                        fontSize = 14.sp,
                                        color = GraySecondary
                                    )

                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        text = "${project.start_date} s/d ${project.end_date}",
                                        fontSize = 13.sp,
                                        color = GraySecondary,
                                        fontStyle = FontStyle.Italic
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

