package com.tugas.layout.ui.auth

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.* // Menggunakan Material 2 Components (TextField, Button, etc.)
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import com.tugas.data.api.RetrofitInstance
import com.tugas.data.model.UserData
import com.tugas.data.repository.AuthRepository
import com.tugas.data.repository.AuthViewModelFactory
import com.tugas.data.repository.UserPreferences
import com.tugas.layout.R // Pastikan ini mengarah ke file R yang benar
import com.tugas.layout.ui.theme.CustomTextFieldBackground
import com.tugas.layout.ui.theme.LightGreen // Mungkin tidak lagi digunakan, tapi biarkan dulu
import com.tugas.layout.ui.theme.White
import com.tugas.viewmodel.AuthViewModel
import kotlinx.coroutines.launch

// --- Definisi Warna Gradient (DIKOREKSI) ---
// Berdasarkan gambar, atasnya hijau tua, bawahnya lebih pekat/menghitam.
val LoginGradientStartColor = Color(0xFF1B5E20) // Hijau gelap di bagian atas
// Mengatur warna akhir agar lebih gelap, mendekati hitam atau hijau yang sangat pekat.
// Saya akan menggunakan warna yang sedikit lebih gelap lagi dari start,
// atau bahkan bisa menjadi hijau gelap yang sama untuk efek 'solid' di bawah.
val LoginGradientEndColor = Color(0xFF000000)   // Menggunakan hitam untuk efek menghitam di bawah

@Composable
fun LoginScreen(
    onLoginClick: (token: String, user: UserData) -> Unit = { _, _ -> },
    onRegisterNavigate: () -> Unit = {},
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }

    val coroutineScope = rememberCoroutineScope()

    val context = LocalContext.current
    val userPrefs = remember { UserPreferences(context) }
    val repository = remember { AuthRepository(RetrofitInstance.api) }
    val factory = remember { AuthViewModelFactory(userPrefs, repository) }
    val viewModel: AuthViewModel = viewModel(factory = factory)

    val loginResult by viewModel.loginResult.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()

    LaunchedEffect(loginResult) {
        Log.d("LoginScreen", "LaunchedEffect triggered: $loginResult")
        loginResult?.let {
            coroutineScope.launch {
                userPrefs.saveAuth(it.token, it.user.role)
                Toast.makeText(context, "Selamat datang ${it.user.name}", Toast.LENGTH_SHORT).show()
                onLoginClick(it.token, it.user)
            }
        }
    }

    LaunchedEffect(errorMessage) {
        errorMessage?.let {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(LoginGradientStartColor, LoginGradientEndColor)
                )
            )
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header: Logo dan Teks
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 50.dp, horizontal = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Baris Logo + Teks
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_logo_poltek), // Pastikan ini mengarah ke drawable yang benar
                        contentDescription = "Logo Politeknik Negeri Bengkalis",
                        modifier = Modifier.size(60.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "POLITEKNIK NEGERI\nBENGKALIS",
                        color = White, // Tetap putih
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold,
                        lineHeight = 22.sp
                    )
                }

                Spacer(modifier = Modifier.height(60.dp))

                // SELAMAT DATANG TUGAS AKHIR MAHASISWA
                Text(
                    text = "SELAMAT DATANG",
                    color = White, // Tetap putih
                    fontSize = 28.sp,
                    fontWeight = FontWeight.ExtraBold,
                    textAlign = TextAlign.Center
                )
                Text(
                    text = "TUGAS AKHIR MAHASISWA",
                    color = White, // Tetap putih
                    fontSize = 24.sp,
                    fontWeight = FontWeight.SemiBold,
                    textAlign = TextAlign.Center
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Form Login
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // 1. Email TextField
                CustomLoginTextField(
                    value = email,
                    onValueChange = { email = it },
                    placeholderText = "Email",
                    leadingIcon = {
                        Icon(Icons.Default.Email, contentDescription = "Email Icon", tint = White) // Icon tetap putih
                    }
                )

                Spacer(modifier = Modifier.height(16.dp))

                // 2. Password TextField
                CustomLoginTextField(
                    value = password,
                    onValueChange = { password = it },
                    placeholderText = "Password",
                    leadingIcon = {
                        Icon(Icons.Default.Lock, contentDescription = "Password Icon", tint = White) // Icon tetap putih
                    },
                    visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    trailingIcon = {
                        val image = if (passwordVisible)
                            Icons.Filled.Visibility
                        else
                            Icons.Filled.VisibilityOff

                        IconButton(onClick = { passwordVisible = !passwordVisible }) {
                            Icon(imageVector  = image, contentDescription = "Toggle password visibility", tint = White) // Icon tetap putih
                        }
                    }
                )

                // Lupa Password?
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.CenterEnd
                ) {
                    TextButton(
                        onClick = { /* Handle Lupa Password */ },
                        contentPadding = PaddingValues(0.dp)
                    ) {
                        Text(
                            text = "Lupa Password ?",
                            color = White, // Tetap putih
                            fontSize = 14.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Tombol LOGIN
                Button(
                    onClick = { viewModel.login(email, password) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    colors = ButtonDefaults.buttonColors(backgroundColor = LightGreen), // Gunakan LightGreen yang sudah ada
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        "LOGIN",
                        color = White, // Tetap putih
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Belum Punya Akun? Register
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Belum Punya Akun?",
                        color = White.copy(alpha = 0.7f), // Tetap putih dengan alpha
                        fontSize = 14.sp
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Register",
                        color = LightGreen, // Atau warna hijau terang yang sama
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.clickable(onClick = onRegisterNavigate)
                    )
                }
            }
        }
    }
}

// Komponen kustom untuk TextField yang gelap dan tanpa border
@Composable
fun CustomLoginTextField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholderText: String,
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    visualTransformation: VisualTransformation = VisualTransformation.None
) {
    TextField(
        value = value,
        onValueChange = onValueChange,
        placeholder = { Text(placeholderText, color = White.copy(alpha = 0.7f)) },
        leadingIcon = leadingIcon,
        trailingIcon = trailingIcon,
        visualTransformation = visualTransformation,
        singleLine = true,
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp),
        colors = TextFieldDefaults.textFieldColors(
            backgroundColor = CustomTextFieldBackground, // Latar belakang yang lebih terang dari DarkGreen
            focusedIndicatorColor = Color.Transparent, // Hilangkan garis bawah
            unfocusedIndicatorColor = Color.Transparent, // Hilangkan garis bawah
            cursorColor = White,
            textColor = White // Warna teks input
        ),
        shape = RoundedCornerShape(8.dp) // Sudut membulat
    )
}

// Preview Composable (Opsional, tapi sangat direkomendasikan)
@Preview(showBackground = true, widthDp = 360, heightDp = 640)
@Composable
fun PreviewLoginScreen() {
    Box(modifier = Modifier.fillMaxSize()) {
        LoginScreen(
            onLoginClick = { token, user -> Log.d("Preview", "Login Clicked: $token") },
            onRegisterNavigate = { Log.d("Preview", "Register Navigate Clicked") }
        )
    }
}