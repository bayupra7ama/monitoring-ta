package com.tugas.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tugas.data.model.AddProjectRequest
import com.tugas.data.model.Project
import com.tugas.data.repository.ProjectRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ProjectViewModel : ViewModel() {
    private val repository = ProjectRepository()

    private val _projects = MutableStateFlow<List<Project>>(emptyList())
    val projects: StateFlow<List<Project>> = _projects

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _createResult = MutableStateFlow<Project?>(null)
    val createResult: StateFlow<Project?> = _createResult

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    fun fetchProjects(token: String) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                val response = repository.getProjects(token)
                _projects.value = response.data
            } catch (e: Exception) {
                e.printStackTrace()
                _errorMessage.value = e.message ?: "Gagal memuat data proyek"

            } finally {
                _isLoading.value = false
            }
        }
    }

    fun createProject(token: String, request: AddProjectRequest) {
        viewModelScope.launch {
            try {
                _isLoading.value = true

                val newProject = repository.createProject(token, request)
                _createResult.value = newProject
            } catch (e: Exception) {
                _errorMessage.value = e.message ?: "Gagal menambahkan proyek"

                Log.e("ProjectVM", "Create Failed", e)
            }finally {
                _isLoading.value = false
            }
        }
    }
}
