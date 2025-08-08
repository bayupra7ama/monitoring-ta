package com.tugas.data.repository

import com.tugas.data.api.AuthApiService
import com.tugas.data.api.RetrofitInstance
import com.tugas.data.model.LoginRequest
import com.tugas.data.model.LoginResponse
import com.tugas.data.model.NotificationItem
import com.tugas.data.model.RegisterRequest
import com.tugas.data.model.RegisterResponse
import com.tugas.data.model.ResponseWrapper
import retrofit2.Call
import retrofit2.Response
import javax.inject.Inject

// ✅ 1. Jadikan AuthRepository sebagai `class`, bukan object
class AuthRepository @Inject constructor(
    private val apiService: AuthApiService
) {
    fun register(request: RegisterRequest): Call<RegisterResponse> {
        return apiService.register(request)
    }

    suspend fun login(request: LoginRequest): Response<LoginResponse> {
        return apiService.login(request)
    }

    suspend fun getNotifications(token: String): Response<ResponseWrapper<List<NotificationItem>>> {
        return apiService.getNotifications(token)
    }

}





