package com.tugas.data.model

data class NotificationItem(
    val id: String,
    val type: String,
    val notifiableType: String,
    val notifiableId: Int,
    val data: NotificationData,
    val readAt: String?,
    val createdAt: String,
    val updatedAt: String
)

data class NotificationData(
    val title: String,
    val message: String,
    val status: String,
    val created_at: String,
    val report_id: Int? = null, // atau val task_id: Int? jika lebih relevan

)
