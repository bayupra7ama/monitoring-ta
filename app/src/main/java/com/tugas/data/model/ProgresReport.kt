package com.tugas.data.model

import com.google.gson.annotations.SerializedName

data class ProgressReport(
    val id: Int,
    @SerializedName("subtask_id")
    val subtaskId: Int,
    val user_id: Int,
    val file_path: String,
    val status_validasi: String,
    val file_url: String
)

data class MyReportResponse(
    val id: Int,
    @SerializedName("subtask_id")
    val subtaskId: Int,
    val user_id: Int,
    val file_url: String,
    val status_validasi: String,
    val subtask: SubTask?      // <-- BUKAN task lagi
)

data class TaskDatas(
    val id: Int,
    val title: String,
    val subtasks: List<SubTaskDetail>
)

data class SubTaskData(
    val id: Int,
    val title: String,

    val status: String
)

data class SubTaskDetail(
    val id: Int,
    val title: String,
    val status: String,
    val task: TaskDetailData?
)

data class TaskDetailData(
    val id: Int,
    val title: String,
    val status: String,
    val start_date: String,
    val end_date: String
)

