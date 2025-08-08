package com.tugas.viewmodel

import android.util.Log
import com.tugas.data.api.RetrofitInstance

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tugas.data.model.ResponseWrapper
import com.tugas.data.model.User
import com.tugas.data.model.UserProfile
import com.tugas.data.repository.UserPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class UserViewModel : ViewModel() {
    private val _user = MutableStateFlow<UserProfile?>(null)
    val user: StateFlow<UserProfile?> = _user

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    fun fetchProfile(token: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response = RetrofitInstance.api.getProfile("Bearer $token")
                if (response.isSuccessful) {
                    _user.value = response.body()?.data
                } else {
                    Log.e("UserViewModel", "Gagal ambil profile: ${response.message()}")
                }
            } catch (e: Exception) {
                Log.e("UserViewModel", "Error: ${e.message}")
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun clearState() {
        _user.value = null
    }
}
