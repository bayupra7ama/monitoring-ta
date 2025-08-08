package com.tugas.data.model


data class Project(
    val id: Int,
    val title: String,
    val description: String?,
    val start_date: String,
    val end_date: String,
    val subtasks: List<SubTask>
)

data class ProjectListResponse(
    val meta: MetaProject,
    val data: List<Project>
)

data class MetaProject(
    val code: Int,
    val status: String,
    val message: String?
)
