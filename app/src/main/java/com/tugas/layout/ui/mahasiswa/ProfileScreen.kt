package com.tugas.layout.ui.mahasiswa


import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.tugas.data.model.UserProfile
import com.tugas.layout.R
import com.tugas.layout.ui.theme.GreenPrimary
import com.tugas.layout.ui.theme.GraySecondary

@Composable
fun ProfileScreen(
    user: UserProfile,
    onLogout: () -> Unit,
    onEditProfile: () -> Unit = {}
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF7F5F5))
    ) {
        // 🟢 Header pakai gradient dari drawable
        Image(
            painter = painterResource(id = R.drawable.ic_gradient_profile),
            contentDescription = "Header Gradient",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
        )

        // 🔤 Teks Judul
        Text(
            text = "Profil",
            color = Color.White,
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 60.dp)
        )

        // 🧾 Card Profil
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .offset(y = 120.dp),
            shape = RoundedCornerShape(16.dp),
            elevation = CardDefaults.cardElevation(6.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                // 🔸 Header foto & info user
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        if (!user.photo.isNullOrEmpty()) {
                            Log.d("ProfileScreen", "Foto Profil dipakai: ${user.photo}")
                            AsyncImage(
                                model = user.photo,
                                contentDescription = "Foto Profil",
                                modifier = Modifier
                                    .size(64.dp)
                                    .clip(CircleShape),
                                contentScale = ContentScale.Crop
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = null,
                                modifier = Modifier
                                    .size(64.dp)
                                    .clip(CircleShape),
                                tint = Color.Gray
                            )
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column {
                            Text(
                                text = user.name,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.Black
                            )
                            Text(
                                text = user.email,
                                fontSize = 14.sp,
                                color = GraySecondary
                            )
                            Text(
                                text = "${user.role.uppercase()} • ${user.nim_nidn ?: "-"}",
                                fontSize = 12.sp,
                                color = GraySecondary
                            )
                        }
                    }

                    IconButton(onClick = onEditProfile) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Edit Profil",
                            tint = Color.Black
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // 🔹 Info tambahan
                Text("Jurusan: ${user.jurusan ?: "-"}", fontSize = 14.sp)
                Text("Program Studi: ${user.prodi ?: "-"}", fontSize = 14.sp)

                Spacer(modifier = Modifier.height(24.dp))
                Divider(color = Color(0xFFE0E0E0), thickness = 1.dp)
                Spacer(modifier = Modifier.height(8.dp))

                // 🔹 Tombol Sign Out
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onLogout() }
                        .padding(vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.ExitToApp,
                        contentDescription = null,
                        tint = Color.Red,
                        modifier = Modifier.size(22.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = "Sign Out",
                        color = Color.Red,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }
}
