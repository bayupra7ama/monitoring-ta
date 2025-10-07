package com.tugas.layout.ui.auth

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.* // Import semua dari Material3
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel

import kotlinx.coroutines.launch
import android.widget.Toast
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import com.tugas.data.api.RetrofitInstance
import com.tugas.data.model.RegisterRequest
import com.tugas.data.repository.AuthRepository
import com.tugas.data.repository.AuthViewModelFactory
import com.tugas.data.repository.UserPreferences
import com.tugas.viewmodel.AuthViewModel

// Anda perlu mendefinisikan warna-warna ini di Colors.kt atau di sini jika belum ada
val DarkGreen = Color(0xFF1B5E20) // Contoh warna hijau gelap
val PrimaryGreen = Color(0xFF66BB6A) // Contoh warna hijau primer
val Gray = Color.Black
val CustomTextFieldBackground = Color(0x33FFFFFF) // Warna field transparan putih

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(
    onRegisterSuccess: (String, String) -> Unit = { _, _ -> },
    onLoginNavigate: () -> Unit = {}
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    val prefs = remember { UserPreferences(context) }
    val repository = remember { AuthRepository(RetrofitInstance.api) }
    val factory = remember { AuthViewModelFactory(prefs, repository) }
    val viewModel: AuthViewModel = viewModel(factory = factory)


    val registerResult by viewModel.registerResult.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()

    // State untuk data input
    val roleOptions = listOf("mahasiswa", "dosen")
    var selectedRole by remember { mutableStateOf("mahasiswa") }
    var roleExpanded by remember { mutableStateOf(false) }

    // Data dummy untuk Jurusan dan Program Studi
    val jurusanOptions = listOf("Teknik Informatika", "Sistem Informasi", "Manajemen", "Akuntansi")
    var selectedJurusan by remember { mutableStateOf("") }
    var jurusanExpanded by remember { mutableStateOf(false) }

    // Contoh prodi berdasarkan jurusan. Anda bisa membuat logika yang lebih kompleks.
    val prodiOptionsMap = mapOf(
        "Teknik Informatika" to listOf("S1 Teknik Informatika", "D3 Teknik Komputer"),
        "Sistem Informasi" to listOf("S1 Sistem Informasi", "D3 Manajemen Informatika"),
        "Manajemen" to listOf("S1 Manajemen", "D3 Manajemen Bisnis"),
        "Akuntansi" to listOf("S1 Akuntansi", "D3 Perpajakan")
    )
    var selectedProdi by remember { mutableStateOf("") }
    var prodiExpanded by remember { mutableStateOf(false) }

    var nama by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var nimNidn by remember { mutableStateOf("") }


    // Mengganti Box utama dengan struktur berlapis untuk fixed header dan scrollable content
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.LightGray) // Background utama putih
    ) {
        // --- Bagian Background Hijau Melengkung (tetap di bawah) ---
        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp) // Sesuaikan tinggi sesuai kebutuhan
        ) {
            val width = size.width
            val height = size.height
            val greenColor = Color(0xFF66BB6A) // Warna hijau utama
            val lighterGreen = Color(0xFF81C784) // Warna hijau yang sedikit lebih terang/pastel

            // Path untuk bentuk melengkung
            val path = Path().apply {
                moveTo(0f, 0f)
                lineTo(width, 0f)
                lineTo(width, height * 0.7f)
                quadraticBezierTo(
                    x1 = width * 0.75f, y1 = height * 1.05f,
                    x2 = 0f, y2 = height * 0.6f
                )
                close()
            }
            drawPath(path = path, color = greenColor)

            // Lingkaran-lingkaran di bagian atas hijau
            drawCircle(color = lighterGreen, radius = 50.dp.toPx(), center = Offset(x = width * 0.85f, y = height * 0.1f))
            drawCircle(color = lighterGreen, radius = 30.dp.toPx(), center = Offset(x = width * 0.95f, y = height * 0.05f))
            drawCircle(color = lighterGreen, radius = 20.dp.toPx(), center = Offset(x = width * 0.7f, y = height * 0.03f))
            drawCircle(color = lighterGreen, radius = 25.dp.toPx(), center = Offset(x = width * 0.9f, y = height * 0.25f))
            drawCircle(color = lighterGreen, radius = 15.dp.toPx(), center = Offset(x = width * 0.6f, y = height * 0.15f))
        }

        // --- FIXED HEADER (Panah Back dan Teks "Buat Akun Barumu !") ---
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .padding(top = 24.dp) // Padding atas untuk header
        ) {
            // Icon Back
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Back",
                tint = Gray, // Ubah ke putih agar terlihat di background hijau
                modifier = Modifier
                    .clickable { onLoginNavigate() }
                    .padding(bottom = 8.dp) // Jarak antara icon dan teks
            )

            // Teks Header
            Text(
                "Buat Akun",
                fontSize = 32.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Gray,
            )
            Text(
                "Barumu !",
                fontSize = 32.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Gray,
                modifier = Modifier.padding(bottom = 8.dp) // Jarak antara teks header dan form
            )
        }

        // --- SCROLLABLE FORM FIELDS ---
        // Letakkan Column ini di bawah header, dengan padding atas yang sesuai
        // agar tidak tumpang tindih dengan header yang fixed.
        // Tinggi dari fixed header + padding kira-kira 200.dp
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp)
                .padding(top = 200.dp) // Offset agar tidak menutupi header fixed
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            CustomRegisterTextField(
                value = nama,
                onValueChange = { nama = it },
                placeholderText = "Nama Lengkap",
                leadingIcon = { Icon(Icons.Default.Person, contentDescription = null, tint = Gray.copy(alpha = 0.7f)) }
            )
            Spacer(modifier = Modifier.height(16.dp))

            CustomRegisterTextField(
                value = email,
                onValueChange = { email = it },
                placeholderText = "Email",
                leadingIcon = { Icon(Icons.Default.Email, contentDescription = null, tint = Gray.copy(alpha = 0.7f)) }
            )
            Spacer(modifier = Modifier.height(16.dp))

            // Role dropdown
            Column(modifier = Modifier.fillMaxWidth()) {
                ExposedDropdownMenuBox(
                    expanded = roleExpanded,
                    onExpandedChange = { roleExpanded = !roleExpanded },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    TextField(
                        value = selectedRole.capitalize(),
                        onValueChange = {},
                        readOnly = true,
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = roleExpanded, ) },
                        leadingIcon = { Icon(Icons.Default.Person, contentDescription = null, tint = Gray.copy(alpha = 0.7f)) },
                        placeholder = { Text("Dosen/Mahasiswa", color = Gray.copy(alpha = 0.7f)) },
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth(),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = CustomTextFieldBackground, unfocusedContainerColor = CustomTextFieldBackground, disabledContainerColor = CustomTextFieldBackground,
                            focusedIndicatorColor = Color.Transparent, unfocusedIndicatorColor = Color.Transparent, disabledIndicatorColor = Color.Transparent,
                            cursorColor = Gray, focusedTextColor = Gray, unfocusedTextColor = Gray, disabledTextColor = Gray,
                            focusedLeadingIconColor = Gray.copy(alpha = 0.7f), unfocusedLeadingIconColor = Gray.copy(alpha = 0.7f), disabledLeadingIconColor = Gray.copy(alpha = 0.7f),
                            focusedTrailingIconColor = Gray.copy(alpha = 0.7f), unfocusedTrailingIconColor = Gray.copy(alpha = 0.7f), disabledTrailingIconColor = Gray.copy(alpha = 0.7f),
                            focusedPlaceholderColor = Gray.copy(alpha = 0.7f), unfocusedPlaceholderColor = Gray.copy(alpha = 0.7f), disabledPlaceholderColor = Gray.copy(alpha = 0.7f)
                        ),
                        shape = RoundedCornerShape(8.dp)
                    )

                    ExposedDropdownMenu(
                        expanded = roleExpanded,
                        onDismissRequest = { roleExpanded = false },
                        modifier = Modifier.background(CustomTextFieldBackground)
                    ) {
                        roleOptions.forEach { role ->
                            DropdownMenuItem(
                                text = { Text(role.capitalize(), color = Gray) },
                                onClick = { selectedRole = role; roleExpanded = false },
                                colors = MenuDefaults.itemColors(textColor = Gray)
                            )
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))

            CustomRegisterTextField(
                value = nimNidn,
                onValueChange = { nimNidn = it },
                placeholderText = if (selectedRole == "dosen") "NIDN" else "NIM",
                leadingIcon = { Icon(Icons.Default.Person, contentDescription = null, tint = Gray.copy(alpha = 0.7f)) }
            )
            Spacer(modifier = Modifier.height(16.dp))

            // Jurusan dropdown
            Column(modifier = Modifier.fillMaxWidth()) {
                ExposedDropdownMenuBox(
                    expanded = jurusanExpanded,
                    onExpandedChange = { jurusanExpanded = !jurusanExpanded },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    TextField(
                        value = selectedJurusan,
                        onValueChange = {},
                        readOnly = true,
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = jurusanExpanded, ) },
                        leadingIcon = { Icon(Icons.Default.Edit, contentDescription = null, tint = Gray.copy(alpha = 0.7f)) },
                        placeholder = { Text("Jurusan", color = Gray.copy(alpha = 0.7f)) },
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth(),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = CustomTextFieldBackground, unfocusedContainerColor = CustomTextFieldBackground, disabledContainerColor = CustomTextFieldBackground,
                            focusedIndicatorColor = Color.Transparent, unfocusedIndicatorColor = Color.Transparent, disabledIndicatorColor = Color.Transparent,
                            cursorColor = Gray, focusedTextColor = Gray, unfocusedTextColor = Gray, disabledTextColor = Gray,
                            focusedLeadingIconColor = Gray.copy(alpha = 0.7f), unfocusedLeadingIconColor = Gray.copy(alpha = 0.7f), disabledLeadingIconColor = Gray.copy(alpha = 0.7f),
                            focusedTrailingIconColor = Gray.copy(alpha = 0.7f), unfocusedTrailingIconColor = Gray.copy(alpha = 0.7f), disabledTrailingIconColor = Gray.copy(alpha = 0.7f),
                            focusedPlaceholderColor = Gray.copy(alpha = 0.7f), unfocusedPlaceholderColor = Gray.copy(alpha = 0.7f), disabledPlaceholderColor = Gray.copy(alpha = 0.7f)
                        ),
                        shape = RoundedCornerShape(8.dp)
                    )

                    ExposedDropdownMenu(
                        expanded = jurusanExpanded,
                        onDismissRequest = { jurusanExpanded = false },
                        modifier = Modifier.background(CustomTextFieldBackground)
                    ) {
                        jurusanOptions.forEach { jurusanItem ->
                            DropdownMenuItem(
                                text = { Text(jurusanItem, color = Gray) },
                                onClick = {
                                    selectedJurusan = jurusanItem
                                    selectedProdi = "" // Reset prodi saat jurusan berubah
                                    jurusanExpanded = false
                                },
                                colors = MenuDefaults.itemColors(textColor = Gray)
                            )
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))

            // Program Studi dropdown (tergantung Jurusan)
            Column(modifier = Modifier.fillMaxWidth()) {
                ExposedDropdownMenuBox(
                    expanded = prodiExpanded,
                    onExpandedChange = { prodiExpanded = !prodiExpanded },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    TextField(
                        value = selectedProdi,
                        onValueChange = {},
                        readOnly = true,
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = prodiExpanded, ) },
                        leadingIcon = { Icon(Icons.Default.Edit, contentDescription = null, tint = Gray.copy(alpha = 0.7f)) },
                        placeholder = { Text("Program Studi", color = Gray.copy(alpha = 0.7f)) },
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth(),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = CustomTextFieldBackground, unfocusedContainerColor = CustomTextFieldBackground, disabledContainerColor = CustomTextFieldBackground,
                            focusedIndicatorColor = Color.Transparent, unfocusedIndicatorColor = Color.Transparent, disabledIndicatorColor = Color.Transparent,
                            cursorColor = Gray, focusedTextColor = Gray, unfocusedTextColor = Gray, disabledTextColor = Gray,
                            focusedLeadingIconColor = Gray.copy(alpha = 0.7f), unfocusedLeadingIconColor = Gray.copy(alpha = 0.7f), disabledLeadingIconColor = Gray.copy(alpha = 0.7f),
                            focusedTrailingIconColor = Gray.copy(alpha = 0.7f), unfocusedTrailingIconColor = Gray.copy(alpha = 0.7f), disabledTrailingIconColor = Gray.copy(alpha = 0.7f),
                            focusedPlaceholderColor = Gray.copy(alpha = 0.7f), unfocusedPlaceholderColor = Gray.copy(alpha = 0.7f), disabledPlaceholderColor = Gray.copy(alpha = 0.7f)
                        ),
                        shape = RoundedCornerShape(8.dp),
                        enabled = selectedJurusan.isNotEmpty() // Nonaktifkan jika jurusan belum dipilih
                    )

                    ExposedDropdownMenu(
                        expanded = prodiExpanded,
                        onDismissRequest = { prodiExpanded = false },
                        modifier = Modifier.background(CustomTextFieldBackground)
                    ) {
                        // Tampilkan prodi berdasarkan jurusan yang dipilih
                        prodiOptionsMap[selectedJurusan]?.forEach { prodiItem ->
                            DropdownMenuItem(
                                text = { Text(prodiItem, color = Gray) },
                                onClick = {
                                    selectedProdi = prodiItem
                                    prodiExpanded = false
                                },
                                colors = MenuDefaults.itemColors(textColor = Gray)
                            )
                        } ?: run {
                            // Tampilkan pesan jika tidak ada prodi atau jurusan belum dipilih
                            DropdownMenuItem(
                                text = { Text("Pilih Jurusan terlebih dahulu", color = Gray.copy(alpha = 0.7f)) },
                                onClick = { /* Do nothing */ },
                                enabled = false
                            )
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))

            CustomRegisterTextField(
                value = password,
                onValueChange = { password = it },
                placeholderText = "Password",
                visualTransformation = PasswordVisualTransformation(),
                leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null, tint = Gray.copy(alpha = 0.7f)) }
            )
            Spacer(modifier = Modifier.height(16.dp))

            CustomRegisterTextField(
                value = confirmPassword,
                onValueChange = { confirmPassword = it },
                placeholderText = "Confirm Password",
                visualTransformation = PasswordVisualTransformation(),
                leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null, tint = Gray.copy(alpha = 0.7f)) }
            )
            Spacer(modifier = Modifier.height(32.dp))

            // Tombol Sign Up
            Button(
                onClick = {
                    if (password != confirmPassword) {
                        Toast.makeText(context, "Password dan Confirm Password tidak sama!", Toast.LENGTH_SHORT).show()
                        return@Button
                    }
                    val request = RegisterRequest(
                        name = nama, email = email, password = password, password_confirmation = confirmPassword,
                        role = selectedRole, nim_nidn = nimNidn, jurusan = selectedJurusan, prodi = selectedProdi,
                    )
                    viewModel.register(request)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryGreen),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(
                    "Sign Up",
                    color = Gray,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Link Login
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Sudah punya akun?",
                    color = Gray.copy(alpha = 0.7f),
                    fontSize = 14.sp
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "Login",
                    color = PrimaryGreen,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.clickable(onClick = onLoginNavigate)
                )
            }
            Spacer(modifier = Modifier.height(30.dp))
        }
    }

    // --- LOGIC (Dipertahankan) ---
    LaunchedEffect(registerResult) {
        registerResult?.let {
            val token = it.data.token
            val role = it.data.user.role
            coroutineScope.launch {
                prefs.saveAuth(token, role)
                onRegisterSuccess(token, role)
            }
        }
    }
    LaunchedEffect(errorMessage) {
        errorMessage?.let {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
        }
    }
}

// CustomRegisterTextField tetap sama, tidak perlu diubah lagi
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomRegisterTextField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholderText: String,
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    visualTransformation: androidx.compose.ui.text.input.VisualTransformation = androidx.compose.ui.text.input.VisualTransformation.None,
    enabled: Boolean = true // Tambahkan parameter enabled
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        placeholder = { Text(placeholderText, color = Gray.copy(alpha = 0.7f)) },
        leadingIcon = leadingIcon,
        trailingIcon = trailingIcon,
        visualTransformation = visualTransformation,
        singleLine = true,
        modifier = Modifier.fillMaxWidth(),
        colors = TextFieldDefaults.colors(
            focusedContainerColor = CustomTextFieldBackground, unfocusedContainerColor = CustomTextFieldBackground, disabledContainerColor = CustomTextFieldBackground,
            focusedIndicatorColor = Color.Transparent, unfocusedIndicatorColor = Color.Transparent, disabledIndicatorColor = Color.Transparent,
            cursorColor = Gray, focusedTextColor = Gray, unfocusedTextColor = Gray, disabledTextColor = Gray,
            focusedLeadingIconColor = Gray.copy(alpha = 0.7f), unfocusedLeadingIconColor = Gray.copy(alpha = 0.7f), disabledLeadingIconColor = Gray.copy(alpha = 0.7f),
            focusedTrailingIconColor = Gray.copy(alpha = 0.7f), unfocusedTrailingIconColor = Gray.copy(alpha = 0.7f), disabledTrailingIconColor = Gray.copy(alpha = 0.7f),
            focusedPlaceholderColor = Gray.copy(alpha = 0.7f), unfocusedPlaceholderColor = Gray.copy(alpha = 0.7f), disabledPlaceholderColor = Gray.copy(alpha = 0.7f)
        ),
        shape = RoundedCornerShape(8.dp),
        enabled = enabled // Gunakan parameter enabled
    )
}

@Preview(showBackground = true, widthDp = 360, heightDp = 640)
@Composable
fun RegisterScreenPreview() {
    RegisterScreen()
}