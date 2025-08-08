package com.tugas.layout.ui.dosen

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.tugas.data.api.RetrofitInstance
import com.tugas.data.repository.NotificationRepository
import com.tugas.data.repository.NotificationViewModelFactory
import com.tugas.data.repository.UserPreferences
import com.tugas.viewmodel.NotificationViewModel
import kotlinx.coroutines.launch

@Composable
fun NotificationScreen(
    onNavigateToReportDetail: (Int) -> Unit // ✅ Tambahkan parameter navigasi

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


    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text("Notifikasi", fontSize = 20.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(12.dp))

        if (isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            LazyColumn {
                items(notifications) { notif ->
                    Card(

                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 6.dp)
                            .clickable {
                                notif.data.report_id?.let { reportId ->
                                    coroutineScope.launch {
                                        val token = prefs.getToken()
                                        if (!token.isNullOrBlank()) {
                                            viewModel.markAsRead(token, notif.id)
                                            viewModel.fetchNotifications(token)

                                        }
                                    }
                                    onNavigateToReportDetail(reportId)
                                }
                            }
                        ,
                        elevation = CardDefaults.cardElevation(2.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (notif.readAt == null) Color(0xFFE3F2FD) else Color.White
                        ),
                    ) {
                        Column(Modifier.padding(16.dp)) {
                            Text(notif.data.title, fontWeight = FontWeight.Bold)
                            Spacer(Modifier.height(4.dp))
                            Text(notif.data.message)
                            Spacer(Modifier.height(4.dp))
                            Text(
                                text = notif.data.created_at,
                                fontSize = 12.sp,
                                color = Color.Gray
                            )
                        }
                    }
                }
            }

        }
    }
}

