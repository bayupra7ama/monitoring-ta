package com.tugas.layout.ui.mahasiswa

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.tugas.data.model.Project
import com.tugas.data.repository.UserPreferences
import com.tugas.viewmodel.ProjectViewModel
import kotlinx.coroutines.launch

@Composable
fun ProjectListScreen(
    onProjectClick: (Project) -> Unit,
    onAddProjectClick: () -> Unit
) {
    val context = LocalContext.current
    val viewModel: ProjectViewModel = viewModel()
    val projects by viewModel.projects.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    val userPrefs = remember { UserPreferences(context) }
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(true) {
        coroutineScope.launch {
            val token = userPrefs.getToken()
            viewModel.fetchProjects(token ?: "")
        }
    }

    Column(Modifier.padding(16.dp)) {
        Text("Daftar Project", fontWeight = FontWeight.Bold, fontSize = 18.sp)
        Spacer(modifier = Modifier.height(8.dp))

        if (isLoading) {
            CircularProgressIndicator()
        } else {
            projects.forEach { project ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                        .clickable { onProjectClick(project) }
                ) {
                    Column(Modifier.padding(12.dp)) {
                        Text(project.title, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        Text(project.description.toString(), )

                        Text(project.start_date + " s/d " + project.end_date)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = onAddProjectClick, modifier = Modifier.fillMaxWidth()) {
            Text("Tambah Project")
        }
    }
}

