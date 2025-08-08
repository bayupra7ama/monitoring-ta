package com.tugas.data.model

data class RegisterRequest(
    val name: String,
    val email: String,
    val password: String,
    val password_confirmation: String,
    val role: String,
    val nim_nidn: String,
    val jurusan: String,
    val prodi: String
)


data class RegisterResponse(
    val meta: Meta,
    val data: RegisterData
)

data class Meta(
    val code: Int,
    val status: String,
    val message: String
)

data class RegisterData(
    val user: User,
    val token: String
)
