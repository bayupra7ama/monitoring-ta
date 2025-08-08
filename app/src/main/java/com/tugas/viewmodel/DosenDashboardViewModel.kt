package com.tugas.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tugas.data.api.RetrofitInstance
import com.tugas.data.model.Project
import com.tugas.data.model.ProjectDetailResponse
import com.tugas.data.model.UserProfile
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class DosenDashboardViewModel : ViewModel() {

    private val _mahasiswaList = MutableStateFlow<List<UserProfile>>(emptyList())
    val mahasiswaList = _mahasiswaList.asStateFlow()

    private val _projectList = MutableStateFlow<List<Project>>(emptyList())
    val projectList = _projectList.asStateFlow()

    private val _selectedProjectDetail = MutableStateFlow<DashboardProjectResponse?>(null)
    val selectedProjectDetail = _selectedProjectDetail.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    fun fetchMahasiswa(token: String) {
        viewModelScope.launch {
            try {
                val response = RetrofitInstance.api.getMahasiswaBimbingan("Bearer $token")
                if (response.isSuccessful) {
                    _mahasiswaList.value = response.body()?.data ?: emptyList()
                }
            } catch (e: Exception) {
                Log.e("DosenVM", "fetchMahasiswa error: ${e.message}")
            }
        }
    }

    fun fetchProjectsByMahasiswa(token: String, mahasiswaId: Int) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response = RetrofitInstance.api.getProjectsByMahasiswa(mahasiswaId, "Bearer $token")
                if (response.isSuccessful) {
                    _projectList.value = response.body()?.data ?: emptyList()
                }
            } catch (e: Exception) {
                Log.e("DosenVM", "fetchProjects error: ${e.message}")
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun fetchDashboardByProject(token: String, projectId: Int) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response = RetrofitInstance.api.getProjectDashboard(projectId, "Bearer $token")
                if (response.isSuccessful) {
                    _selectedProjectDetail.value = response.body()?.data
                }
            } catch (e: Exception) {
                Log.e("DosenVM", "fetchDashboard error: ${e.message}")
            } finally {
                _isLoading.value = false
            }
        }
    }
}
