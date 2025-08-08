package com.tugas.data.repository

import com.tugas.data.api.AuthApiService
import com.tugas.data.api.RetrofitInstance.api
import com.tugas.data.model.NotificationItem
import com.tugas.data.model.ResponseWrapper
import retrofit2.Response

class NotificationRepository(private val apiService: AuthApiService) {
    suspend fun getNotifications(token: String): Response<ResponseWrapper<List<NotificationItem>>> {
        return apiService.getNotifications("Bearer $token")
    }
    suspend fun getUnreadNotifications(token: String): Response<ResponseWrapper<List<NotificationItem>>> {
        return apiService.getUnreadNotifications("Bearer $token")
    }

    suspend fun markNotificationAsRead(token: String, notificationId: String): Response<ResponseWrapper<Unit>> {
        return apiService.markNotificationAsRead("Bearer $token", notificationId)
    }

}
