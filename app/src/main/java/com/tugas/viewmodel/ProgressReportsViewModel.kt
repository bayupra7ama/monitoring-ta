package com.tugas.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tugas.data.api.RetrofitInstance
import com.tugas.data.model.MyReportResponse
import com.tugas.data.model.ProgressReport
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ProgressReportViewModel : ViewModel() {

    private val _reports = MutableStateFlow<List<MyReportResponse>>(emptyList())
    val reports = _reports.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    fun fetchMyReports(token: String) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                val response = RetrofitInstance.api.getMyReports("Bearer $token")
                _reports.value = response.data
            } catch (e: Exception) {
                Log.e("ProgressReportVM", "Error: ${e.message}")
            } finally {
                _isLoading.value = false
            }
        }
    }


}
