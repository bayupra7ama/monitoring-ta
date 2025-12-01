package com.tugas.data.model

data class SubTask(
    val id: Int,
    val task_id: Int,
    val title: String,
    val task: TaskDetailData?,

    val status: String
)

data class TaskDetailResponse(
    val id: Int,
    val project_id: Int,
    val title: String,
    val status: String,
    val start_date: String,
    val end_date: String,
    val subtasks: List<SubTask>,
    val project: Project
)

data class ResponseWrapper<T>(
    val meta: Meta,
    val data: T
)

data class MetaResponse(
    val code: Int,
    val status: String,
    val message: String
)
