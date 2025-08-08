package com.tugas.data.model

data class User(
    val id: Int,
    val name: String,
    val email: String,
    val role: String,
    val nim_nidn: String,
    val jurusan: String,
    val prodi: String,
    val photo: String?
)
