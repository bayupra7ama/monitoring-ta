package com.tugas.data.model



data class ProjectDetail(
    val id: Int,
    val user_id: Int,
    val title: String,
    val description: String,
    val start_date: String,
    val end_date: String,
    val tasks: List<Task>,
    val burndown: BurndownData
)

data class Task(
    val id: Int,
    val title: String,
    val status: String,
    val start_date: String,
    val end_date: String
    ,val subtasks: List<SubTask>
)


data class BurndownChartData(
    val labels: List<String>,
    val actual: List<Int>
)

data class TaskBurndown(
    val labels: List<String>,
    val actual: List<Int>,
    val ideal: List<Int>   // 🔥 tambahin ini biar cocok sama response Laravel

)


data class ProjectDetailResponse(
    val meta: Meta,
    val data: ProjectDetailData
)

data class ProjectDetailData(
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


data class BurndownData(
    val labels: List<String>,
    val actual: List<Int>,
    val ideal: List<Int>
)

