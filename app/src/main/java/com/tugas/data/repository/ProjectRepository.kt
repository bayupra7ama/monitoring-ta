package com.tugas.data.repository

import com.tugas.data.api.RetrofitInstance
import com.tugas.data.api.RetrofitInstance.api
import com.tugas.data.model.AddProjectRequest
import com.tugas.data.model.Project
import com.tugas.data.model.ProjectListResponse
import com.tugas.data.model.ReportDetail
import com.tugas.data.model.ResponseWrapper
import com.tugas.data.model.TaskInput
import com.tugas.data.model.TaskResponse
import retrofit2.Call
import retrofit2.Response

class ProjectRepository {
    suspend fun getProjects(token: String): ProjectListResponse {
        return RetrofitInstance.api.getProjects("Bearer $token")
    }
        suspend fun createProject(token: String, request: AddProjectRequest):   Project {
        return RetrofitInstance.api.addProject("Bearer $token", request).data
    }
    suspend fun getMyProjects(token: String): ProjectListResponse {
        return RetrofitInstance.api.getProjects("Bearer $token")
    }



    suspend fun validateReport(
        token: String,
        reportId: Int,
        status: String,
        feedback: String?
    ): Response<ResponseWrapper<ReportDetail>> {
        return api.validateReport(
            "Bearer $token", reportId,
            mapOf(
                "status_validasi" to status,
                "feedback" to (feedback ?: "")
            )
        )
    }



}