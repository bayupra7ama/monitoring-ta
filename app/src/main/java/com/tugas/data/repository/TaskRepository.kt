package com.tugas.data.repository

import com.tugas.data.api.RetrofitInstance
import com.tugas.data.model.TaskInput
import com.tugas.data.model.TaskResponse
import retrofit2.Call

class TaskRepository {
    fun createTask(token: String, projectId: Int, task: TaskInput): Call<TaskResponse> {
        return RetrofitInstance.api.addTask("Bearer $token", projectId, task)
    }
}