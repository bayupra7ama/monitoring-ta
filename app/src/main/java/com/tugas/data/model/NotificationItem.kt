package com.tugas.data.model

import com.google.gson.annotations.SerializedName

data class NotificationItem(
    @SerializedName("id") val id: String,
    @SerializedName("type") val type: String,
    @SerializedName("notifiable_type") val notifiableType: String,
    @SerializedName("notifiable_id") val notifiableId: Int,
    @SerializedName("data") val data: NotificationData,
    @SerializedName("read_at") val readAt: String?,   // ✅ WAJIB ada
    @SerializedName("created_at") val createdAt: String,
    @SerializedName("updated_at") val updatedAt: String
)

data class NotificationData(
    @SerializedName("title") val title: String,
    @SerializedName("message") val message: String,
    @SerializedName("status") val status: String,
    @SerializedName("report_id") val reportId: Int,
    @SerializedName("created_at") val createdAt: String
)

