package com.tugas.data.repository

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.tugas.viewmodel.AuthViewModel
import com.tugas.viewmodel.NotificationViewModel

class AuthViewModelFactory(
    private val prefs: UserPreferences,
    private val repository: AuthRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AuthViewModel::class.java)) {
            return AuthViewModel(prefs, repository) as T
        }

        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
