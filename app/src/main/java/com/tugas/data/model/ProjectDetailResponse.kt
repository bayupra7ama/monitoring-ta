package com.tugas.data.model

data class ProjectDetailResponse(
    val data: ProjectDetail
)

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

data class BurndownData(
    val project: BurndownChartData,
    val per_task: List<TaskBurndown>
)

data class BurndownChartData(
    val labels: List<String>,
    val actual: List<Int>
)

data class TaskBurndown(
    val task_title: String,
    val labels: List<String>,
    val actual: List<Int>
)
