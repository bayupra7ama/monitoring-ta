package com.tugas.data.model

data class AddProjectRequest(
    val title: String,
    val description: String,
    val start_date: String,
    val end_date: String
)


data class CreateProjectResponse(
    val meta: MetaAddProject,
    val data: Project
)

data class MetaAddProject(
    val code: Int,
    val status: String,
    val message: String
)

data class AddProject(
    val id: Int,
    val user_id: Int,
    val title: String,
    val description: String,
    val start_date: String,
    val end_date: String,
    val created_at: String,
    val updated_at: String
)

