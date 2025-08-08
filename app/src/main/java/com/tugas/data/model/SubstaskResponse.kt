package com.tugas.data.model

data class SubtaskResponse(
    val id: Int,
    val task_id: Int,
    val title: String,
    val status: String,
    val created_at: String,
    val updated_at: String
)
