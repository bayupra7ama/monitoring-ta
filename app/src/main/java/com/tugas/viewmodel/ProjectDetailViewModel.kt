package com.tugas.viewmodel

import androidx.lifecycle.ViewModel
import com.tugas.data.api.RetrofitInstance
import com.tugas.data.model.ProjectDetail
import com.tugas.data.model.ProjectDetailResponse
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ProjectDetailViewModel : ViewModel() {
    private val _projectDetail = MutableStateFlow<ProjectDetail?>(null)
    val projectDetail = _projectDetail.asStateFlow()

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
                    _projectDetail.value = response.body()?.data
                    _isLoading.value = false
                }

                override fun onFailure(call: Call<ProjectDetailResponse>, t: Throwable) {
                    _isLoading.value = false
                }
            })
    }


}
