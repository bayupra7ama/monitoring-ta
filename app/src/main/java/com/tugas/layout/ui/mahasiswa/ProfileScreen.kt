package com.tugas.layout.ui.mahasiswa

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Divider
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.tugas.data.model.UserProfile

@Composable
fun ProfileScreen(
    user: UserProfile,
    onLogout: () -> Unit,
    onEditProfile: () -> Unit = {}
) {
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFF4CAF50))
                .padding(vertical = 16.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Profil",
                color = Color.White,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(Modifier.height(16.dp))

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            elevation = CardDefaults.cardElevation(4.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (!user.photo.isNullOrEmpty()) {
                        AsyncImage(
                            model = user.photo,
                            contentDescription = "Foto Profil",
                            modifier = Modifier
                                .size(64.dp)
                                .clip(CircleShape)
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

                    Spacer(Modifier.width(12.dp))

                    Column {
                        Text(user.name, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                        Text(user.email, fontSize = 14.sp)
                        Text(
                            "${user.role.uppercase()} • ${user.nim_nidn ?: "-"}",
                            fontSize = 12.sp,
                            color = Color.Gray
                        )
                    }

                    Spacer(Modifier.weight(1f))

                    IconButton(onClick = { onEditProfile() }) {
                        Icon(Icons.Default.Edit, contentDescription = "Edit Profil")
                    }
                }

                Spacer(Modifier.height(16.dp))
                Divider()
                Spacer(Modifier.height(8.dp))

                Text("Jurusan: ${user.jurusan ?: "-"}", fontSize = 14.sp)
                Text("Program Studi: ${user.prodi ?: "-"}", fontSize = 14.sp)

                Spacer(Modifier.height(16.dp))

                Button(
                    onClick = onLogout,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
                ) {
                    Text("Sign Out", color = Color.White)
                }
            }
        }
    }
}
