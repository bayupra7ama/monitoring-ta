package com.tugas.layout.ui.profil

import android.content.res.Configuration
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
import androidx.compose.material.icons.filled.Home
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tugas.layout.R
import com.tugas.layout.ui.theme.GreenPrimary
import com.tugas.layout.ui.theme.GraySecondary

@Composable
fun ProfileScreen(
    onEditClick: () -> Unit = {},
    onSignOutClick: () -> Unit = {},
    onLaporanClick: () -> Unit = {}
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF7F5F5))
    ) {
        // 🟢 Header pakai SVG dari Figma
        Image(
            painter = painterResource(id = R.drawable.ic_gradient_profile),
            contentDescription = "Header Gradient",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxWidth()
                .height(400.dp)
        )

        // 🔤 Teks “Profil” di atas gradient
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
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Image(
                            painter = painterResource(id = R.drawable.image1_1005643),
                            contentDescription = "Foto Profil",
                            modifier = Modifier
                                .size(60.dp)
                                .clip(CircleShape),
                            contentScale = ContentScale.Crop
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "Andre Winata",
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp,
                                color = Color.Black
                            )
                            Text(
                                text = "andrewinata@gmail.com",
                                fontSize = 14.sp,
                                color = GraySecondary
                            )
                            Text(
                                text = "Dosen",
                                fontSize = 14.sp,
                                color = GraySecondary
                            )
                        }
                    }

                    IconButton(onClick = onEditClick) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Edit Profil",
                            tint = Color.Black
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // 🔹 Menu Laporan Mahasiswa
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onLaporanClick() }
                        .padding(vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Folder,
                        contentDescription = null,
                        tint = GreenPrimary,
                        modifier = Modifier.size(22.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = "Laporan Mahasiswa",
                        color = Color.Black,
                        fontSize = 15.sp
                    )
                }

                Divider(color = Color(0xFFE0E0E0), thickness = 1.dp)

                // 🔹 Menu Sign Out
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onSignOutClick() }
                        .padding(vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.ExitToApp,
                        contentDescription = null,
                        tint = GreenPrimary,
                        modifier = Modifier.size(22.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = "Sign Out",
                        color = Color.Black,
                        fontSize = 15.sp
                    )
                }
            }
        }

        // ⚫ Bottom Navigation
        BottomNavigationBar(
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }
}

@Composable
fun BottomNavigationBar(modifier: Modifier = Modifier) {
    var selectedItem by remember { mutableStateOf(2) } // tab Profile aktif
    NavigationBar(
        containerColor = Color.White,
        modifier = modifier
            .fillMaxWidth()
            .height(70.dp)
    ) {
        val items = listOf(
            Icons.Default.Home,
            Icons.Default.Folder,
            Icons.Default.Person
        )

        items.forEachIndexed { index, icon ->
            NavigationBarItem(
                selected = selectedItem == index,
                onClick = { selectedItem = index },
                icon = {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = if (selectedItem == index) Color.Black else Color.LightGray,
                        modifier = Modifier.size(26.dp)
                    )
                },
                alwaysShowLabel = false
            )
        }
    }
}

@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_NO)
@Composable
fun ProfileScreenPreview() {
    ProfileScreen()
}
