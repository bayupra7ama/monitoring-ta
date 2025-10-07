package com.tugas.layout.ui.splashscreen

// SplashScreen.kt

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import com.tugas.data.repository.UserPreferences
import com.tugas.layout.R
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first

@Composable
fun SplashScreen(
    onNavigateToLogin: () -> Unit,
    onNavigateToHome: (role: String) -> Unit
) {
    val context = LocalContext.current
    val prefs = remember { UserPreferences(context) } // Asumsi UserPreferences ada di sini

    // LaunchedEffect digunakan untuk menjalankan suspend function (seperti delay & membaca flow)
    // saat composable ini pertama kali ditampilkan.
    LaunchedEffect(key1 = true) {
        // Beri sedikit jeda agar splash screen terlihat
        delay(3000L) // 2 detik

        // Ambil data role dari UserPreferences.
        // .first() akan mengambil nilai pertama yang ada di Flow.
        val role = prefs.roleFlow.first()

        if (role == "mahasiswa" || role == "dosen") {
            // Jika sudah ada role (artinya sudah login), navigasi ke home sesuai role
            onNavigateToHome(role)
        } else {
            // Jika tidak ada, navigasi ke halaman login
            onNavigateToLogin()
        }
    }

    // Tampilan UI untuk Splash Screen
    // Anda bisa ganti dengan logo aplikasi Anda
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        // Ganti 'R.drawable.your_logo' dengan ID drawable logo Anda
        // Jika belum ada, Anda bisa gunakan CircularProgressIndicator() untuk sementara
        Image(
            painter = painterResource(id = R.drawable.ic_logo_poltek), // GANTI DENGAN LOGO ANDA
            contentDescription = "App Logo"
        )
    }
}