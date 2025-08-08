package com.tugas.layout.ui.auth

import android.widget.Toast
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
import com.tugas.data.model.RegisterRequest
import com.tugas.data.repository.AuthRepository
import com.tugas.data.repository.AuthViewModelFactory
import com.tugas.data.repository.UserPreferences
import com.tugas.viewmodel.AuthViewModel
import kotlinx.coroutines.launch



@OptIn(ExperimentalMaterial3Api::class)
@Composable


fun RegisterScreen(
   // gunakan data dosen lengkap, bukan String
    onRegisterSuccess: (String, String) -> Unit = { _, _ -> } // token dan role
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    val prefs = remember { UserPreferences(context) }
    val repository = remember { AuthRepository(RetrofitInstance.api) }
    val factory = remember { AuthViewModelFactory(prefs, repository) }
    val viewModel: AuthViewModel = viewModel(factory = factory)


    val registerResult by viewModel.registerResult.collectAsState() // ✅ benar
    val errorMessage by viewModel.errorMessage.collectAsState()

    val roleOptions = listOf("mahasiswa", "dosen")
    var selectedRole by remember { mutableStateOf("mahasiswa") }
    var roleExpanded by remember { mutableStateOf(false) }

    var nama by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var nimNidn by remember { mutableStateOf("") }
    var jurusan by remember { mutableStateOf("") }
    var prodi by remember { mutableStateOf("") }

    var selectedDosenIndex by remember { mutableStateOf(0) }
    var dosenExpanded by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Text("Daftar Akun", fontSize = 20.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(value = nama, onValueChange = { nama = it }, label = { Text("Nama") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(value = email, onValueChange = { email = it }, label = { Text("Email") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(value = password, onValueChange = { password = it }, label = { Text("Password") }, visualTransformation = PasswordVisualTransformation(), modifier = Modifier.fillMaxWidth())

        // Role dropdown
        Text("Daftar sebagai", modifier = Modifier.padding(top = 8.dp))
        ExposedDropdownMenuBox(expanded = roleExpanded, onExpandedChange = { roleExpanded = !roleExpanded }) {
            OutlinedTextField(
                value = selectedRole,
                onValueChange = {},
                readOnly = true,
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = roleExpanded) },
                modifier = Modifier.menuAnchor().fillMaxWidth()
            )
            ExposedDropdownMenu(expanded = roleExpanded, onDismissRequest = { roleExpanded = false }) {
                roleOptions.forEach { role ->
                    DropdownMenuItem(
                        text = { Text(role.capitalize()) },
                        onClick = {
                            selectedRole = role
                            roleExpanded = false
                        }
                    )
                }
            }
        }

        OutlinedTextField(
            value = nimNidn,
            onValueChange = { nimNidn = it },
            label = { Text(if (selectedRole == "dosen") "NIDN" else "NIM") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(value = jurusan, onValueChange = { jurusan = it }, label = { Text("Jurusan") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(value = prodi, onValueChange = { prodi = it }, label = { Text("Prodi") }, modifier = Modifier.fillMaxWidth())

        // Jika mahasiswa, tampilkan dosen pembimbing


        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = {
            val request = RegisterRequest(
                name = nama,
                email = email,
                password = password,
                password_confirmation = password,
                role = selectedRole,
                nim_nidn = nimNidn,
                jurusan = jurusan,
                prodi = prodi,
            )
            viewModel.register(request)
        }) {
            Text("Daftar")
        }

        // Jika berhasil
        LaunchedEffect(registerResult) {
            registerResult?.let {
                val token = it.data.token
                val role = it.data.user.role
                coroutineScope.launch {
                    prefs.saveAuth(token, role)
                    onRegisterSuccess(token, role) // navigasi langsung ke dashboard sesuai role
                }
            }
        }

        // Jika gagal
        LaunchedEffect(errorMessage) {
            errorMessage?.let {
                Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun RegisterPreview() {
    RegisterScreen()
}
