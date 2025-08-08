package com.tugas.viewmodel


data class DashboardProjectResponse(
    val project: ProjectWithTasks,
    val burndown: BurndownData
)

data class ProjectWithTasks(
    val id: Int,
    val user_id: Int,
    val title: String,
    val description: String,
    val start_date: String,
    val end_date: String,
    val created_at: String,
    val updated_at: String,
    val tasks: List<TaskWithSubtasks>
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
    val actual: List<Int>
)
