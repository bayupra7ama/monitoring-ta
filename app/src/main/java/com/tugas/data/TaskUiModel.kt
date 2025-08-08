package com.tugas.data

import androidx.compose.ui.graphics.Color

data class TaskUIModel(
    val title: String,
    val endDate: String,
    val status: String,
    val progress: Float,
    val statusColor: Color
)

val sampleTasks = listOf(
    TaskUIModel("Analisis Kebutuhan", "2025-06-20", "belum", 0f, Color.Red),
    TaskUIModel("Desain UI/UX", "2025-06-25", "proses", 0.5f, Color.Yellow),
    TaskUIModel("Implementasi", "2025-07-01", "selesai", 1f, Color.Green)
)

data class ProjectUIModel(
    val id: Int,
    val title: String,
    val description: String,
    val startDate: String,
    val endDate: String
)

val sampleProjects = listOf(
    ProjectUIModel(1, "Sistem Monitoring TA", "Aplikasi tracking tugas akhir", "2025-06-01", "2025-08-01"),
    ProjectUIModel(2, "Sistem Informasi Alumni", "Pengelolaan data alumni", "2025-06-10", "2025-08-10")
)
data class ProgressReportUI(
    val id: Int,
    val title: String,
    val startDate: String,
    val endDate: String,
    val status: String,
    val feedback: String?,
    val fileName: String,
    val subTasks: List<String> = emptyList()
)


val sampleReports = listOf(
    ProgressReportUI(
        id = 1,
        title = "Pendahuluan",
        startDate = "10 Okt 2024",
        endDate = "24 Okt 2024",
        status = "selesai",
        feedback = "Bagus, lanjutkan ke Bab 2",
        fileName = "Pendahuluan_TA.pdf",
        subTasks = listOf("Menentukan topik", "Menulis latar belakang", "Merumuskan tujuan")
    ),
    ProgressReportUI(
        id = 2,
        title = "Tinjauan Pustaka",
        startDate = "25 Okt 2024",
        endDate = "8 Nov 2024",
        status = "belum",
        feedback = null,
        fileName = "Tinjauan_Pustaka.pdf",
        subTasks = listOf("Cari referensi", "Rangkuman literatur")
    )
)


