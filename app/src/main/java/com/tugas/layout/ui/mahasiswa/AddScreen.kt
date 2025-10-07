    package com.tugas.layout.ui.mahasiswa

    import androidx.compose.foundation.layout.Column
    import androidx.compose.foundation.layout.Spacer
    import androidx.compose.foundation.layout.fillMaxSize
    import androidx.compose.foundation.layout.fillMaxWidth
    import androidx.compose.foundation.layout.height
    import androidx.compose.foundation.layout.padding
    import androidx.compose.foundation.rememberScrollState
    import androidx.compose.foundation.verticalScroll
    import androidx.compose.material3.Button
    import androidx.compose.material3.DropdownMenuItem
    import androidx.compose.material3.ExperimentalMaterial3Api
    import androidx.compose.material3.ExposedDropdownMenuBox
    import androidx.compose.material3.ExposedDropdownMenuDefaults
    import androidx.compose.material3.OutlinedTextField
    import androidx.compose.material3.Text
    import androidx.compose.runtime.Composable
    import androidx.compose.runtime.getValue
    import androidx.compose.runtime.mutableStateOf
    import androidx.compose.runtime.remember
    import androidx.compose.runtime.setValue
    import androidx.compose.ui.Modifier
    import androidx.compose.ui.platform.LocalContext
    import androidx.compose.ui.text.font.FontWeight
    import androidx.compose.ui.unit.dp
    import androidx.compose.ui.unit.sp

    import android.app.DatePickerDialog
    import android.widget.Toast
    import androidx.compose.animation.AnimatedVisibility
    import androidx.compose.animation.fadeIn
    import androidx.compose.animation.slideInVertically
    import androidx.compose.foundation.Image
    import androidx.compose.foundation.background
    import androidx.compose.foundation.clickable
    import androidx.compose.foundation.layout.Box
    import androidx.compose.foundation.layout.size
    import androidx.compose.foundation.shape.RoundedCornerShape
    import androidx.compose.material.icons.Icons
    import androidx.compose.material.icons.filled.CalendarToday
    import androidx.compose.material3.ButtonDefaults
    import androidx.compose.material3.Card
    import androidx.compose.material3.CardDefaults
    import androidx.compose.material3.CircularProgressIndicator
    import androidx.compose.material3.Icon
    import androidx.compose.material3.IconButton
    import androidx.compose.material3.Scaffold
    import androidx.compose.material3.TopAppBar
    import androidx.compose.material3.TopAppBarDefaults

    import androidx.compose.runtime.*
    import androidx.compose.ui.Alignment
    import androidx.compose.ui.graphics.Color
    import androidx.compose.ui.layout.ContentScale
    import androidx.compose.ui.res.painterResource
    import androidx.compose.ui.text.TextStyle
    import androidx.lifecycle.viewmodel.compose.viewModel
    import androidx.navigation.NavController
    import com.tugas.data.model.TaskInput
    import com.tugas.data.repository.UserPreferences
    import com.tugas.layout.R
    import com.tugas.layout.ui.reusabel.SuccessScreen
    import com.tugas.layout.ui.theme.GreenPrimary
    import com.tugas.route.AppRoute
    import com.tugas.viewmodel.TaskViewModel
    import kotlinx.coroutines.launch
    import java.util.Calendar


    @OptIn(ExperimentalMaterial3Api::class)
    @Composable

    fun AddTaskScreen(
        projectId: Int,
        navController: NavController,
        onSubmit: (TaskInput) -> Unit = {}
    ) {
        var title by remember { mutableStateOf("") }
        var startDate by remember { mutableStateOf("") }
        var endDate by remember { mutableStateOf("") }
        var status by remember { mutableStateOf("belum") }
        var statusExpanded by remember { mutableStateOf(false) }

        val viewModel: TaskViewModel = viewModel()
        val context = LocalContext.current
        val coroutineScope = rememberCoroutineScope()
        val userPrefs = remember { UserPreferences(context) }

        val isLoading by viewModel.isLoading
        val taskResponse by viewModel.taskResponse
        val errorMessage by viewModel.errorMessage

        val statusOptions = listOf("belum", "proses", "selesai")

        if (taskResponse != null) {
            SuccessScreen(
                navController = navController,
                title = "Tugas Diterima",
                message = "Tugas Mu telah di buat, Silahkan kerjakan tugas sesuai dengan tenggat waktu yang telah ditentukan. Semangat!",
                buttonText = "Lihat Tugas",
                onButtonClick = {

                       navController.popBackStack()

                }
            )
        } else {
            Scaffold(
                topBar = {
                    TopAppBar(
                        title = {
                            Text(
                                "Tambah Tugas",
                                color = Color.Black,
                                fontWeight = FontWeight.SemiBold
                            )
                        },
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
                }
            ) { paddingValues ->
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.White)
                ) {
                    // 🌈 Background gradient
                    Image(
                        painter = painterResource(id = R.drawable.ic_gradient_profile),
                        contentDescription = null,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(240.dp),
                        contentScale = ContentScale.Crop
                    )

                    // ✨ Animasi masuk halus
                    AnimatedVisibility(
                        visible = true,
                        enter = fadeIn() + slideInVertically(initialOffsetY = { it / 8 }),
                        modifier = Modifier
                            .padding(paddingValues)
                            .fillMaxSize()
                            .padding(horizontal = 24.dp, vertical = 32.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
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
                                        text = "Form Tambah Tugas",
                                        fontSize = 20.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = GreenPrimary
                                    )

                                    Spacer(modifier = Modifier.height(16.dp))

                                    OutlinedTextField(
                                        value = title,
                                        onValueChange = { title = it },
                                        label = { Text("Judul Tugas") },
                                        modifier = Modifier.fillMaxWidth(),
                                        shape = RoundedCornerShape(12.dp)
                                    )

                                    Spacer(modifier = Modifier.height(10.dp))

                                    DatePickerField(
                                        label = "Tanggal Mulai",
                                        value = startDate,
                                        onValueChange = { startDate = it },
                                        isSelected = startDate.isNotEmpty()
                                    )

                                    Spacer(modifier = Modifier.height(10.dp))

                                    DatePickerField(
                                        label = "Tanggal Selesai",
                                        value = endDate,
                                        onValueChange = { endDate = it },
                                        isSelected = endDate.isNotEmpty()
                                    )

                                    Spacer(modifier = Modifier.height(10.dp))

                                    Text("Status", modifier = Modifier.fillMaxWidth())

                                    ExposedDropdownMenuBox(
                                        expanded = statusExpanded,
                                        onExpandedChange = { statusExpanded = !statusExpanded }
                                    ) {
                                        OutlinedTextField(
                                            value = status,
                                            onValueChange = {},
                                            readOnly = true,
                                            trailingIcon = {
                                                ExposedDropdownMenuDefaults.TrailingIcon(expanded = statusExpanded)
                                            },
                                            modifier = Modifier
                                                .menuAnchor()
                                                .fillMaxWidth(),
                                            shape = RoundedCornerShape(12.dp)
                                        )
                                        ExposedDropdownMenu(
                                            expanded = statusExpanded,
                                            onDismissRequest = { statusExpanded = false }
                                        ) {
                                            statusOptions.forEach {
                                                DropdownMenuItem(
                                                    text = { Text(it.replaceFirstChar { c -> c.uppercase() }) },
                                                    onClick = {
                                                        status = it
                                                        statusExpanded = false
                                                    }
                                                )
                                            }
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(20.dp))

                                    Button(
                                        onClick = {
                                            coroutineScope.launch {
                                                val token = userPrefs.getToken() ?: return@launch

                                                // ✅ Validasi input kosong
                                                if (title.isBlank() || startDate.isBlank() || endDate.isBlank()) {
                                                    Toast.makeText(
                                                        context,
                                                        "Semua data harus diisi!",
                                                        Toast.LENGTH_SHORT
                                                    ).show()
                                                    return@launch
                                                }

                                                // ✅ Validasi tanggal
                                                try {
                                                    val format =
                                                        java.text.SimpleDateFormat("yyyy-MM-dd")
                                                    val start = format.parse(startDate)
                                                    val end = format.parse(endDate)

                                                    if (start.after(end)) {
                                                        Toast.makeText(
                                                            context,
                                                            "Tanggal selesai tidak boleh sebelum tanggal mulai!",
                                                            Toast.LENGTH_SHORT
                                                        ).show()
                                                        return@launch
                                                    }
                                                } catch (e: Exception) {
                                                    Toast.makeText(
                                                        context,
                                                        "Format tanggal tidak valid!",
                                                        Toast.LENGTH_SHORT
                                                    ).show()
                                                    return@launch
                                                }

                                                // ✅ Semua valid → mulai upload
                                                val taskInput =
                                                    TaskInput(title, startDate, endDate, status)
                                                viewModel.addTask(token, projectId, taskInput)

                                                // 🔄 Tampilkan loading sambil nunggu respons
                                                viewModel.isLoading.value = true
                                            }
                                        },
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
                                            Text(
                                                "Simpan Tugas",
                                                color = Color.White,
                                                fontWeight = FontWeight.Medium
                                            )
                                        }
                                    }


                                }
                            }
                        }
                    }
                }
                // 🔍 Observasi hasil upload dari ViewModel
                LaunchedEffect(taskResponse, errorMessage) {
                    if (taskResponse != null) {
                        Toast.makeText(context, "Tugas berhasil ditambahkan!", Toast.LENGTH_SHORT)
                            .show()
                        viewModel.isLoading.value = false
                        navController.popBackStack() // ✅ baru pindah halaman kalau sukses
                    } else if (!errorMessage.isNullOrEmpty()) {
                        Toast.makeText(
                            context,
                            "Gagal menambahkan tugas: $errorMessage",
                            Toast.LENGTH_SHORT
                        ).show()
                        viewModel.isLoading.value = false
                    }
                }
                LaunchedEffect(viewModel.errorMessage) {
                    viewModel.errorMessage.value?.let { msg ->
                        Toast.makeText(
                            context,
                            "Gagal menambahkan project: $msg",
                            Toast.LENGTH_SHORT
                        ).show()
                        viewModel.isLoading.value = false
                    }
                }



            }

        }
    }

    @Composable
    fun DatePickerField(
        label: String,
        value: String,
        onValueChange: (String) -> Unit,
        isSelected: Boolean = false
    ) {
        val context = LocalContext.current
        val calendar = Calendar.getInstance()

        val datePickerDialog = remember {
            DatePickerDialog(
                context,
                { _, year, month, dayOfMonth ->
                    val formatted = "%04d-%02d-%02d".format(year, month + 1, dayOfMonth)
                    onValueChange(formatted)
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            )
        }

        OutlinedTextField(
            value = value,
            onValueChange = {},
            readOnly = true,
            label = { Text(label) },
            textStyle = TextStyle(
                color = if (isSelected) Color.Black else Color.Gray,
                fontSize = 14.sp
            ),
            trailingIcon = {
                IconButton(
                    onClick = { datePickerDialog.show() } // ✅ dialog pasti muncul
                ) {
                    Icon(
                        imageVector = Icons.Default.CalendarToday,
                        contentDescription = "Pilih Tanggal",
                        tint = GreenPrimary
                    )
                }
            },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
        )
    }



