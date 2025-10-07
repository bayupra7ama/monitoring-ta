package com.tugas.viewmodel

import com.tugas.data.model.ProjectDetailResponse
import com.tugas.data.model.ProjectWithTasks
import com.tugas.data.model.BurndownData

import androidx.lifecycle.ViewModel
import com.tugas.data.api.RetrofitInstance
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
class ProjectDetailViewModel : ViewModel() {
    private val _projectDetail = MutableStateFlow<ProjectWithTasks?>(null)
    val projectDetail = _projectDetail.asStateFlow()

    private val _burndownData = MutableStateFlow<BurndownData?>(null)
    val burndownData = _burndownData.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    fun fetchProjectDetail(projectId: Int, token: String) {
        _isLoading.value = true
        RetrofitInstance.api.getProjectDetail(projectId, "Bearer $token")
            .enqueue(object : Callback<ProjectDetailResponse> {
                override fun onResponse(
                    call: Call<ProjectDetailResponse>,
                    response: Response<ProjectDetailResponse>
                ) {
                    val body = response.body()
                    _projectDetail.value = body?.data?.project
                    _burndownData.value = body?.data?.burndown
                    _isLoading.value = false
                }

                override fun onFailure(call: Call<ProjectDetailResponse>, t: Throwable) {
                    _isLoading.value = false
                }
            })
    }
}

