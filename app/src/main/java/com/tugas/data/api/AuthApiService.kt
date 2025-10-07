package com.tugas.data.api

import androidx.test.espresso.core.internal.deps.dagger.Provides
import com.tugas.data.model.AddProjectRequest
import com.tugas.data.model.CreateProjectResponse
import com.tugas.data.model.LoginRequest
import com.tugas.data.model.LoginResponse
import com.tugas.data.model.MyReportResponse
import com.tugas.data.model.NotificationItem
import com.tugas.data.model.ProgressReport
import com.tugas.data.model.ProgressReportResponse
import com.tugas.data.model.Project
import com.tugas.data.model.ProjectDetailResponse
import com.tugas.data.model.ProjectListResponse
import com.tugas.data.model.RegisterRequest
import com.tugas.data.model.RegisterResponse
import com.tugas.data.model.ReportDetail
import com.tugas.data.model.ResponseWrapper
import com.tugas.data.model.SubTask
import com.tugas.data.model.SubtaskResponse
import com.tugas.data.model.TaskDetailResponse
import com.tugas.data.model.TaskInput
import com.tugas.data.model.TaskResponse
import com.tugas.data.model.UserProfile
import com.tugas.data.model.ValidateReportRequest
import com.tugas.viewmodel.DashboardProjectResponse
import okhttp3.MultipartBody
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.Path
import javax.inject.Singleton

interface AuthApiService {

    @POST("api/register")
    fun register(@Body request: RegisterRequest): Call<RegisterResponse>



    @POST("api/login")
    suspend fun login(@Body request: LoginRequest): Response<LoginResponse>

    @GET("api/projects")
    suspend fun getProjects(
        @Header("Authorization") token: String
    ): ProjectListResponse

    @POST("api/projects")
    suspend fun addProject(
        @Header("Authorization") token: String,
        @Body request: AddProjectRequest
    ): CreateProjectResponse

    @GET("api/projects/{id}")
    fun getProjectDetail(
        @Path("id") projectId: Int,
        @Header("Authorization") token: String
    ): Call<ProjectDetailResponse>

    @POST("api/projects/{projectId}/tasks")
    fun addTask(
        @Header("Authorization") token: String,
        @Path("projectId") projectId: Int,
        @Body taskInput: TaskInput
    ): Call<TaskResponse>

    @GET("api/tasks/{id}")
    fun getTaskDetail(
        @Path("id") taskId: Int,
        @Header("Authorization") token: String
    ): Call<ResponseWrapper<TaskDetailResponse>>
    @POST("api/tasks/{taskId}/subtasks")
    fun addSubtask(
        @Path("taskId") taskId: Int,
        @Body body: Map<String, String>,
        @Header("Authorization") token: String
    ): Call<ResponseWrapper<SubtaskResponse>>

    @PUT("api/subtasks/{id}")
    fun updateSubtaskStatus(
        @Path("id") subtaskId: Int,
        @Body data: Map<String, String>,
        @Header("Authorization") token: String
    ): Call<ResponseWrapper<SubTask>>

    @Multipart
    @POST("api/tasks/{taskId}/reports")
    suspend fun uploadProgressReport(
        @Path("taskId") taskId: Int,
        @Part file: MultipartBody.Part,
        @Header("Authorization") token: String
    ): Response<ResponseWrapper<ProgressReport>>

    @GET("api/my-reports")
    suspend fun getMyReports(
        @Header("Authorization") token: String
    ): ResponseWrapper<List<MyReportResponse>>

    @GET("api/me")
    suspend fun getProfile(
        @Header("Authorization") token: String
    ): Response<ResponseWrapper<UserProfile>>

    // 🔽 Ambil daftar mahasiswa bimbingan
    @GET("api/dosen/mahasiswa")
    suspend fun getMahasiswaBimbingan(
        @Header("Authorization") token: String
    ): Response<ResponseWrapper<List<UserProfile>>>

    // 🔽 Ambil daftar project milik mahasiswa
    @GET("api/dosen/mahasiswa/{mahasiswaId}/projects")
    suspend fun getProjectsByMahasiswa(
        @Path("mahasiswaId") mahasiswaId: Int,
        @Header("Authorization") token: String
    ): Response<ResponseWrapper<List<Project>>>

    // 🔽 Ambil detail dashboard (burndown + tugas)
    @GET("api/dosen/projects/{projectId}/dashboard")
    suspend fun getProjectDashboard(
        @Path("projectId") projectId: Int,
        @Header("Authorization") token: String
    ): Response<ResponseWrapper<DashboardProjectResponse>>

    @GET("api/reports")
    suspend fun getAllProgressReports(
        @Header("Authorization") token: String
    ): Response<ResponseWrapper<List<ProgressReportResponse>>>

    @PUT("api/reports/{id}/validate")
    suspend fun validateReport(
        @Path("id") reportId: Int,
        @Header("Authorization") token: String,
        @Body request: ValidateReportRequest
    ): Response<ResponseWrapper<ProgressReportResponse>>

    @GET("api/notifications")
    suspend fun getNotifications(
        @Header("Authorization") token: String
    ): Response<ResponseWrapper<List<NotificationItem>>>

    @GET("api/notifications/unread")
    suspend fun getUnreadNotifications(
        @Header("Authorization") authHeader: String
    ): Response<ResponseWrapper<List<NotificationItem>>>

        @GET("api/reports/{id}")
        suspend fun getReportById(
            @Header("Authorization") token: String,
            @Path("id") id: Int
        ): Response<ResponseWrapper<ReportDetail>>

    @POST("api/notifications/{id}/read")
    suspend fun markNotificationAsRead(
        @Header("Authorization") token: String,
        @Path("id") notificationId: String
    ): Response<ResponseWrapper<Unit>>


    @PUT("api/reports/{id}/validate")
    suspend fun validateReport(
        @Header("Authorization") token: String,
        @Path("id") id: Int,
        @Body body: Map<String, String>
    ): Response<ResponseWrapper<ReportDetail>>

    
}

