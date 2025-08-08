package com.tugas.data.model

data class ProgressReport(
    val id: Int,
    val task_id: Int,
    val user_id: Int,
    val file_path: String,
    val status_validasi: String,
    val file_url: String
)
data class MyReportResponse(
    val id: Int,
    val task_id: Int,
    val user_id: Int,
    val file_url: String,
    val status_validasi: String,
    val feedback: String?,
    val task: TaskDatas?
)

data class TaskDatas(
    val id: Int,
    val title: String,
    val subtasks: List<SubTaskData>
)

data class SubTaskData(
    val id: Int,
    val title: String,
    val status: String
)
