package com.tugas.layout.ui.dosen

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.tugas.data.api.RetrofitInstance
import com.tugas.data.repository.NotificationRepository
import com.tugas.data.repository.NotificationViewModelFactory
import com.tugas.data.repository.UserPreferences
import com.tugas.helper.timeAgo
import com.tugas.layout.R
import com.tugas.layout.ui.theme.GreenPrimary
import com.tugas.viewmodel.NotificationViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun NotificationScreen(
    navController: NavController,
    onNavigateToReportDetail: (Int) -> Unit
) {
    val context = LocalContext.current
    val prefs = remember { UserPreferences(context) }

    val apiService = RetrofitInstance.api
    val viewModel: NotificationViewModel = viewModel(
        factory = NotificationViewModelFactory(NotificationRepository(apiService))
    )

    val notifications by viewModel.notifications.collectAsState()
    val isLoading by viewModel.loading.collectAsState()
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        coroutineScope.launch {
            val token = prefs.getToken()
            Log.d("NotificationScreen", "Token fetched: $token")
            if (!token.isNullOrBlank()) {
                viewModel.fetchNotifications(token)
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Notifikasi", color = Color.Black, fontWeight = FontWeight.SemiBold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_arrow_back),
                            contentDescription = "Kembali",
                            tint = Color.Unspecified
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White),
            )
        },
        containerColor = Color.White
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp)
        ) {
            when {
                isLoading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = GreenPrimary)
                    }
                }

                notifications.isEmpty() -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            "Belum ada notifikasi",
                            color = Color.Gray,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                else -> {
                    LazyColumn {
                        items(notifications) { notif ->
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 6.dp)
                                    .clickable {
                                        notif.data.reportId.let { reportId ->
                                            coroutineScope.launch {
                                                val token = prefs.getToken()
                                                if (!token.isNullOrBlank()) {
                                                    // Update status baca di UI
                                                    val updatedList = notifications.map {
                                                        if (it.id == notif.id) it.copy(readAt = "just-now") else it
                                                    }
                                                    viewModel.setNotifications(updatedList)

                                                    // Mark as read di server
                                                    viewModel.markAsRead(token, notif.id)
                                                }
                                            }
                                            onNavigateToReportDetail(reportId)
                                        }
                                    },
                                elevation = CardDefaults.cardElevation(2.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = if (notif.readAt == null) Color(0xFFE3F2FD) else Color.White
                                ),
                            ) {
                                Column(Modifier.padding(16.dp)) {
                                    Text(
                                        notif.data.title,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.Black
                                    )
                                    Spacer(Modifier.height(4.dp))
                                    Text(notif.data.message)
                                    Spacer(Modifier.height(6.dp))
                                    Text(
                                        text = timeAgo(notif.data.createdAt),
                                        color = Color.Gray,
                                        fontSize = 12.sp
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
