package com.tugas.layout.ui.mahasiswa

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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.tugas.data.model.AddProjectRequest
import com.tugas.data.repository.UserPreferences
import com.tugas.viewmodel.ProjectViewModel
import kotlinx.coroutines.launch
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddProjectScreen(
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

    val datePicker = remember { Calendar.getInstance() }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Text("Tambah Project", fontSize = 20.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(value = title, onValueChange = { title = it }, label = { Text("Judul Project") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(value = description, onValueChange = { description = it }, label = { Text("Deskripsi") }, modifier = Modifier.fillMaxWidth())
        DatePickerField("Tanggal Mulai", startDate) { startDate = it }
        DatePickerField("Tanggal Selesai", endDate) { endDate = it }

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = {
            coroutineScope.launch {
                val token = userPrefs.getToken()
                if (token != null) {
                    val request = AddProjectRequest(title, description, startDate, endDate)
                    viewModel.createProject(token, request)
                    Toast.makeText(context, "Project berhasil ditambahkan", Toast.LENGTH_SHORT).show()
                    onSubmit()
                }
            }
        }, modifier = Modifier.fillMaxWidth()) {
            Text("Simpan Project")
        }
    }
}
