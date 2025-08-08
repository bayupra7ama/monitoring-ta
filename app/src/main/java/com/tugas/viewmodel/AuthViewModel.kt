package com.tugas.viewmodel

import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tugas.data.api.RetrofitInstance
import com.tugas.data.model.LoginRequest
import com.tugas.data.model.LoginResponse
import com.tugas.data.model.RegisterRequest
import com.tugas.data.model.RegisterResponse
import com.tugas.data.repository.AuthRepository
import com.tugas.data.repository.UserPreferences
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import javax.inject.Inject



    class AuthViewModel(
        private val prefs: UserPreferences,
        private val repository:
        AuthRepository,

        ) : ViewModel() {

    private val _registerResult = MutableStateFlow<RegisterResponse?>(null)
    val registerResult: StateFlow<RegisterResponse?> = _registerResult
    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage
    private val _loginResult = MutableStateFlow<LoginResponse?>(null)
        val loginResult: StateFlow<LoginResponse?> = _loginResult


        private val _role = MutableStateFlow<String?>(null)
        val role: StateFlow<String?> = _role

        private val _token = MutableStateFlow<String?>(null)
        val token: StateFlow<String?> = _token

        init {
            viewModelScope.launch {
                _token.value = prefs.getToken()
                _role.value = prefs.getRole()
            }
        }

        fun login(email: String, password: String) {
            viewModelScope.launch {
                try {
                    val response = RetrofitInstance.api.login(LoginRequest(email, password))
                    if (response.isSuccessful) {
                        val data = response.body()
                        Log.d("AuthViewModel", "Login success: $data")
                        _loginResult.value = data // ⬅️ PASTIKAN ini bukan null
                        _token.value = data?.token
                        _role.value = data?.user?.role

                      data?.let {
                            prefs.saveAuth(it.token, it.user.role)
                        }
                    } else {
                        _errorMessage.value = "Login gagal: ${response.code()}"
                    }
                } catch (e: Exception) {
                    _errorMessage.value = "Error: ${e.message}"
                }
            }
        }

    fun register(request: RegisterRequest) {
        repository.register(request).enqueue(object : Callback<RegisterResponse> {
            override fun onResponse(
                call: Call<RegisterResponse>,
                response: Response<RegisterResponse>
            ) {
                if (response.isSuccessful) {
                    _registerResult.value = response.body()
                } else {
                    _errorMessage.value = "Register gagal: ${response.message()}"
                }
            }

            override fun onFailure(call: Call<RegisterResponse>, t: Throwable) {
                _errorMessage.value = "Error: ${t.message}"
            }
        })
    }

        fun logout() {
            viewModelScope.launch {
                prefs.clear()
                _token.value = null
                _role.value = null
                _loginResult.value = null
            }
        }




            fun clearState() {
                _errorMessage.value = null
            }
        }


