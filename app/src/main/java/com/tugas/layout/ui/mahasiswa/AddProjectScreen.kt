package com.tugas.layout.ui.mahasiswa

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.tugas.data.model.AddProjectRequest
import com.tugas.data.repository.UserPreferences
import com.tugas.layout.R
import com.tugas.layout.ui.reusabel.SuccessScreen
import com.tugas.layout.ui.theme.GreenPrimary
import com.tugas.route.AppRoute
import com.tugas.viewmodel.ProjectViewModel
import kotlinx.coroutines.launch
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddProjectScreen(
    navController: NavController,
    onSubmit: () -> Unit
) {
    val context = LocalContext.current
    val viewModel: ProjectViewModel = viewModel()
    val coroutineScope = rememberCoroutineScope()
    val userPrefs = remember { UserPreferences(context) }

    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var startDate by remember { mutableStateOf("") }
    var endDate by remember { mutableStateOf("") }
    var isLoading = viewModel.isLoading.collectAsState().value
    var createResult = viewModel.createResult.collectAsState().value


    if (createResult != null) {
        SuccessScreen(
            navController = navController,
            title = "Proyek Diterima",
            message = "Proyek kamu telah berhasil dibuat! Sekarang kamu bisa menambahkan tugas-tugas di dalam proyek ini.",
            buttonText = "Lihat Project",
            onButtonClick = {
                navController.navigate(AppRoute.HOME) {
                    popUpTo(AppRoute.ADD_PROJECT) { inclusive = true }
                }
            }
        )
    } else {

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Tambah Project", color = Color.Black, fontWeight = FontWeight.SemiBold) },
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
                .background(Color.White)
        ) {
            // 🔹 Ganti background pakai drawable gradient
            Image(
                painter = painterResource(id = R.drawable.ic_gradient_profile),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(240.dp),
                contentScale = ContentScale.Crop
            )

            // 🔹 Card form di tengah
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = 24.dp, vertical = 32.dp),
                contentAlignment = Alignment.Center
            ) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    shape = RoundedCornerShape(20.dp),
                    elevation = CardDefaults.cardElevation(6.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Column(
                        modifier = Modifier
                            .padding(20.dp)
                            .verticalScroll(rememberScrollState()),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Form Tambah Project",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = GreenPrimary
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        OutlinedTextField(
                            value = title,
                            onValueChange = { title = it },
                            label = { Text("Judul Project") },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp)
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        OutlinedTextField(
                            value = description,
                            onValueChange = { description = it },
                            label = { Text("Deskripsi Project") },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp)
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        // 🔹 Date picker untuk tanggal mulai
                        DatePickerField(
                            label = "Tanggal Mulai",
                            value = startDate,
                            onValueChange = { startDate = it },
                            isSelected = startDate.isNotEmpty()
                        )

                        // 🔹 Date picker untuk tanggal selesai
                        DatePickerField(
                            label = "Tanggal Selesai",
                            value = endDate,
                            onValueChange = { endDate = it },
                            isSelected = endDate.isNotEmpty()
                        )

                        Spacer(modifier = Modifier.height(20.dp))

                        Button(
                            onClick = {
                                coroutineScope.launch {
                                    val token = userPrefs.getToken() ?: return@launch

                                    // ✅ Validasi input kosong
                                    if (title.isBlank() || description.isBlank() || startDate.isBlank() || endDate.isBlank()) {
                                        Toast.makeText(context, "Semua data harus diisi!", Toast.LENGTH_SHORT).show()
                                        return@launch
                                    }

                                    // ✅ Validasi format tanggal
                                    try {
                                        val format = java.text.SimpleDateFormat("yyyy-MM-dd")
                                        val start = format.parse(startDate)
                                        val end = format.parse(endDate)

                                        if (start.after(end)) {
                                            Toast.makeText(context, "Tanggal selesai tidak boleh sebelum tanggal mulai!", Toast.LENGTH_SHORT).show()
                                            return@launch
                                        }
                                    } catch (e: Exception) {
                                        Toast.makeText(context, "Format tanggal tidak valid!", Toast.LENGTH_SHORT).show()
                                        return@launch
                                    }

                                    // 🔄 Tampilkan loading dan kirim request
                                 isLoading = true
                                    viewModel.createProject(token, AddProjectRequest(title, description, startDate, endDate))
                                }
                            },
                            enabled = !isLoading, // 🔐 disable tombol saat loading
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = GreenPrimary)
                        ) {
                            if (isLoading) {
                                CircularProgressIndicator(
                                    color = Color.White,
                                    strokeWidth = 2.dp,
                                    modifier = Modifier.size(20.dp)
                                )
                            } else {
                                Text("Simpan Project", color = Color.White, fontWeight = FontWeight.Medium)
                            }
                        }


                    }
                }
            }

        }
        LaunchedEffect(createResult) {
            if (createResult != null) {
                Toast.makeText(context, "Project berhasil ditambahkan!", Toast.LENGTH_SHORT).show()
                isLoading = false
                onSubmit() // ✅ baru pindah halaman kalau sukses
            }
        }
        LaunchedEffect(viewModel.errorMessage) {
            viewModel.errorMessage.value?.let { msg ->
                Toast.makeText(context, "Gagal menambahkan project: $msg", Toast.LENGTH_SHORT).show()
                isLoading= false
            }
        }


    }
}
}

