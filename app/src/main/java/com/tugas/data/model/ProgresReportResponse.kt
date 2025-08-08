package com.tugas.data.model

import com.google.gson.annotations.SerializedName

data class ProgressReportResponse(
    val id: Int,
    val task_id: Int,
    val user_id: Int,
    val file_path: String,
    val file_url: String,
    val status_validasi: String,
    val feedback: String?,
    val created_at: String,
    val updated_at: String,
    val user: UserProfile,
    val task: TaskOnly
)


data class ValidateReportRequest(
    val status_validasi: String,
    val feedback: String? = null
)
data class TaskOnly(
    val id: Int,
    val project_id: Int,
    val title: String,
    val status: String,
    val start_date: String,
    val end_date: String,
    val created_at: String,
    val updated_at: String
)




data class TaskDetail(
    val id: Int,
    val title: String,
    val project: ProjectSimple
)

data class ProjectSimple(
    val id: Int,
    val title: String
)

data class ReportDetail(
    val id: Int,
    @SerializedName("status_validasi")
    val statusValidasi: String,
    val feedback: String?,
    val user: User,
    val task: Task,
    @SerializedName("file_url")
    val fileUrl: String
)



