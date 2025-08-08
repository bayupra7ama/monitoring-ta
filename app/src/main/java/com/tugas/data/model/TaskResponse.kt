package com.tugas.data.model

// TaskInput.kt
data class TaskInput(
    val title: String,
    val start_date: String,
    val end_date: String,
    val status: String
)

// TaskResponse.kt
data class TaskResponse(
    val data: TaskData
)

data class TaskData(
    val id: Int,
    val project_id: Int,
    val title: String,
    val status: String,
    val start_date: String,
    val end_date: String,
)
