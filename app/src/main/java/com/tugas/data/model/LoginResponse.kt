package com.tugas.data.model

data class LoginResponse(
    val user: UserData,
    val token: String
)

data class UserData(
    val id: Int,
    val name: String,
    val email: String,
    val role: String,
    val nim_nidn: String,
    val jurusan: String,
    val prodi: String,
    val photo: String?
)
