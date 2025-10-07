package com.tugas.viewmodel

import com.tugas.data.model.ProjectWithTasks


data class DashboardProjectResponse(
    val project: ProjectWithTasks,
    val burndown: BurndownData
)



data class TaskWithSubtasks(
    val id: Int,
    val project_id: Int,
    val title: String,
    val status: String,
    val start_date: String,
    val end_date: String,
    val created_at: String,
    val updated_at: String,
    val subtasks: List<SubTask>
)

data class SubTask(
    val id: Int,
    val task_id: Int,
    val title: String,
    val status: String,
    val created_at: String,
    val updated_at: String
)

data class BurndownData(
    val labels: List<String>,
    val actual: List<Int>,
    val ideal: List<Int>   // 🔥 tambahin ini biar cocok sama response Laravel

)
