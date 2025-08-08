package com.tugas.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tugas.data.api.RetrofitInstance
import com.tugas.data.model.ProgressReportResponse
import com.tugas.data.model.ValidateReportRequest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class DosenReportViewModel : ViewModel() {
    private val _reports = MutableStateFlow<List<ProgressReportResponse>>(emptyList())
    val reports = _reports.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    fun fetchReports(token: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val res = RetrofitInstance.api.getAllProgressReports("Bearer $token")
                if (res.isSuccessful) {
                    _reports.value = res.body()?.data ?: emptyList()
                }
            } catch (e: Exception) {
                Log.e("ReportVM", "Error fetch reports: ${e.message}")
            } finally {
                _isLoading.value = false
            }
        }
    }

    suspend fun validateReport(token: String, reportId: Int, status: String, feedback: String?) {
        RetrofitInstance.api.validateReport(reportId, "Bearer $token", ValidateReportRequest(status, feedback))
        fetchReports(token) // refresh data
    }
}
