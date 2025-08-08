package com.tugas.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tugas.data.api.RetrofitInstance
import com.tugas.data.model.ReportDetail
import com.tugas.data.repository.ProjectRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ReportDetailViewModel : ViewModel() {
    private val _reportDetail = MutableStateFlow<ReportDetail?>(null)
    val reportDetail: StateFlow<ReportDetail?> = _reportDetail

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    fun fetchReportDetail(token: String, reportId: Int) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response = RetrofitInstance.api.getReportById("Bearer $token", reportId)
                if (response.isSuccessful) {
                    _reportDetail.value = response.body()?.data
                }
            } catch (e: Exception) {
                Log.e("ReportDetailViewModel", "Error: ${e.message}")
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun submitValidation(token: String, reportId: Int, status: String, feedback: String?) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                val response = ProjectRepository().validateReport(token, reportId, status, feedback)
                if (response.isSuccessful) {
                    fetchReportDetail(token, reportId)
                } else {
                    Log.e("Validate", "Gagal: ${response.code()}")
                }
            } catch (e: Exception) {
                Log.e("Validate", "Error: ${e.message}")
            } finally {
                _isLoading.value = false
            }
        }
    }

}
