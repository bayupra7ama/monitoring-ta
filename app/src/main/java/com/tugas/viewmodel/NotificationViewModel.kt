package com.tugas.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tugas.data.model.NotificationItem
import com.tugas.data.repository.AuthRepository
import com.tugas.data.repository.NotificationRepository
import com.tugas.data.repository.UserPreferences
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class NotificationViewModel(
    private val repository: NotificationRepository
) : ViewModel() {

    private val _notifications = MutableStateFlow<List<NotificationItem>>(emptyList())
    val notifications: StateFlow<List<NotificationItem>> = _notifications

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading

    private val _unreadCount = MutableStateFlow(0)
    val unreadCount: StateFlow<Int> = _unreadCount


    fun fetchNotifications(token: String) {
        viewModelScope.launch {
            _loading.value = true
            try {
                val response = repository.getNotifications(token)
                if (response.isSuccessful && response.body()?.data != null) {
                    _notifications.value = response.body()!!.data!!
                }
            } catch (e: Exception) {
                Log.e("NotificationViewModel", "Error fetching notifications: ${e.message}", e)
            } finally {
                _loading.value = false
            }
        }
    }

    fun fetchUnreadCount(token: String) {
        viewModelScope.launch {
            try {
                val response = repository.getUnreadNotifications(token)
                if (response.isSuccessful) {
                    _unreadCount.value = response.body()?.data?.size ?: 0
                } else {
                    Log.e("NotifVM", "Gagal fetch unread: ${response.errorBody()?.string()}")
                }
            } catch (e: Exception) {
                Log.e("NotifVM", "Exception: ${e.message}")
            }
        }
    }

    fun markAsRead(token: String, notificationId: String) {
        viewModelScope.launch {
            try {
                // langsung update state dulu (optimistic)
                val updatedList = _notifications.value.map { notif ->
                    if (notif.id == notificationId) {
                        notif.copy(readAt = "just_now")
                    } else notif
                }
                _notifications.value = updatedList

                // kirim request ke server
                repository.markNotificationAsRead(token, notificationId)
            } catch (e: Exception) {
                Log.e("NotificationViewModel", "Mark as read failed: ${e.message}")
            }
        }
    }


    fun setNotifications(updated: List<NotificationItem>) {
        _notifications.value = updated
    }



}
