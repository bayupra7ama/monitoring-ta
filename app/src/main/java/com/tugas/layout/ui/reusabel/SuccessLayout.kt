package com.tugas.layout.ui.reusabel


import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.scaleIn
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.tugas.layout.R
import com.tugas.layout.ui.theme.GreenPrimary

@Composable
fun SuccessScreen(
    navController: NavController,
    title: String = "Berhasil!",
    message: String = "Data kamu telah berhasil dikirim.",
    buttonText: String = "Kembali",
    onButtonClick: () -> Unit = { navController.popBackStack() },
    iconRes: Int = R.drawable.ic_success
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(24.dp)
        ) {
            // ✅ Animasi muncul halus
            AnimatedVisibility(
                visible = true,
                enter = fadeIn() + scaleIn()
            ) {
                Image(
                    painter = painterResource(id = iconRes),
                    contentDescription = "Success Icon",
                    modifier = Modifier
                        .size(180.dp)
                        .padding(bottom = 16.dp),
                    contentScale = ContentScale.Fit
                )
            }

            // 🟢 Judul sukses
            Text(
                text = title,
                style = TextStyle(
                    color = GreenPrimary,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                ),
                modifier = Modifier.padding(bottom = 8.dp)
            )

            // 📝 Pesan deskripsi
            Text(
                text = message,
                textAlign = TextAlign.Center,
                fontSize = 14.sp,
                color = Color.Black.copy(alpha = 0.8f),
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            Spacer(modifier = Modifier.height(24.dp))

            // 🟩 Tombol aksi
            Button(
                onClick = onButtonClick,
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(containerColor = GreenPrimary),
                modifier = Modifier
                    .fillMaxWidth(0.7f)
                    .height(48.dp)
            ) {
                Text(
                    text = buttonText,
                    color = Color.White,
                    fontWeight = FontWeight.Medium,
                    fontSize = 16.sp
                )
            }
        }
    }
}
