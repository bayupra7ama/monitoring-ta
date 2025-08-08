package com.tugas.layout.ui.auth

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.tugas.data.api.RetrofitInstance
import com.tugas.data.model.LoginRequest
import com.tugas.data.model.UserData
import com.tugas.data.model.UserProfile
import com.tugas.data.repository.AuthRepository
import com.tugas.data.repository.AuthViewModelFactory
import com.tugas.data.repository.UserPreferences
import com.tugas.viewmodel.AuthViewModel
import kotlinx.coroutines.launch

@Composable
fun LoginScreen(
    onLoginClick: (token: String, user: UserData) -> Unit = { _, _ -> },
    onRegisterNavigate: () -> Unit = {}
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    val coroutineScope = rememberCoroutineScope()

    val context = LocalContext.current
    val userPrefs = remember { UserPreferences(context) }
    val repository = remember { AuthRepository(RetrofitInstance.api) }
    val factory = remember { AuthViewModelFactory(userPrefs, repository) }
    val viewModel: AuthViewModel = viewModel(factory = factory)


    val loginResult by viewModel.loginResult.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center
    ) {
        Text("Masuk", fontSize = 22.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(24.dp))

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                viewModel.login(email, password)
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Masuk")
        }

        Spacer(modifier = Modifier.height(12.dp))

        TextButton(onClick = onRegisterNavigate) {
            Text("Belum punya akun? Daftar")
        }
    }

    // ✅ Efek login berhasil
    LaunchedEffect(loginResult) {
        Log.d("LoginScreen", "LaunchedEffect triggered: $loginResult")
        loginResult?.let {
            coroutineScope.launch {
                userPrefs.saveAuth(it.token, it.user.role)
                Toast.makeText(context, "Selamat datang ${it.user.name}", Toast.LENGTH_SHORT).show()
                onLoginClick(it.token, it.user) // ⬅️ HARUS dipanggil
            }
        }
    }

    // ❌ Kalau gagal login
    LaunchedEffect(errorMessage) {
        errorMessage?.let {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
        }
    }
}


