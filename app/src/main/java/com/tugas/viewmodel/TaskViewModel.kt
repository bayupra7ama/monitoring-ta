package com.tugas.viewmodel

import android.content.Context
import android.net.Uri
import android.widget.Toast
import androidx.compose.runtime.mutableStateOf
import androidx.core.net.toFile
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tugas.data.api.RetrofitInstance
import com.tugas.data.model.ProgressReport
import com.tugas.data.model.ResponseWrapper
import com.tugas.data.model.SubTask
import com.tugas.data.model.SubtaskResponse
import com.tugas.data.model.TaskData
import com.tugas.data.model.TaskDetailResponse
import com.tugas.data.model.TaskInput
import com.tugas.data.model.TaskResponse
import com.tugas.data.repository.TaskRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

import java.io.File

class TaskViewModel : ViewModel() {
    private val repository = TaskRepository()

    val isLoading = mutableStateOf(false)
    val taskResponse = mutableStateOf<TaskData?>(null)
    val errorMessage = mutableStateOf<String?>(null)

    private val _isUploading = MutableStateFlow(false)
    val isUploading = _isUploading.asStateFlow()

    private val _taskDetail = MutableStateFlow<TaskDetailResponse?>(null)
    val taskDetail = _taskDetail.asStateFlow()

    fun addTask(token: String, projectId: Int, input: TaskInput) {
        isLoading.value = true
        repository.createTask(token, projectId, input).enqueue(object : Callback<TaskResponse> {
            override fun onResponse(call: Call<TaskResponse>, response: Response<TaskResponse>) {
                isLoading.value = false
                if (response.isSuccessful) {
                    taskResponse.value = response.body()?.data
                } else {
                    errorMessage.value = "Gagal tambah tugas"
                }
            }

            override fun onFailure(call: Call<TaskResponse>, t: Throwable) {
                isLoading.value = false
                errorMessage.value = t.message
            }
        })
    }



        fun fetchTaskDetail(token: String, taskId: Int) {
            RetrofitInstance.api.getTaskDetail(taskId, "Bearer $token")
                .enqueue(object : Callback<ResponseWrapper<TaskDetailResponse>> {
                    override fun onResponse(
                        call: Call<ResponseWrapper<TaskDetailResponse>>,
                        response: Response<ResponseWrapper<TaskDetailResponse>>
                    ) {
                        if (response.isSuccessful) {
                            _taskDetail.value = response.body()?.data
                        }
                    }

                    override fun onFailure(call: Call<ResponseWrapper<TaskDetailResponse>>, t: Throwable) {
                        // handle error
                    }
                })
        }

    fun addSubtask(token: String, taskId: Int, title: String) {
        isLoading.value = true
        RetrofitInstance.api.addSubtask(taskId, mapOf("title" to title), "Bearer $token")
            .enqueue(object : Callback<ResponseWrapper<SubtaskResponse>>{

                override fun onResponse(
                    call: Call<ResponseWrapper<SubtaskResponse>>,
                    response: Response<ResponseWrapper<SubtaskResponse>>
                ) {
                    isLoading.value = false
                    if (response.isSuccessful) {
                        // fetch ulang detail biar langsung ke-refresh
                        fetchTaskDetail(token, taskId)
                    }
                }

                override fun onFailure(call: Call<ResponseWrapper<SubtaskResponse>>, t: Throwable) {
                    isLoading.value = false
                    errorMessage.value = "Gagal menambahkan subtugas"
                }
            })
    }

    val isUpdatingSubtask = mutableStateOf(false)

    suspend fun updateSubtaskStatus(token: String, subtaskId: Int, status: String) {
        isUpdatingSubtask.value = true
        RetrofitInstance.api.updateSubtaskStatus(

            subtaskId,
            mapOf("status" to status),
            "Bearer $token"
        ).enqueue(object : Callback<ResponseWrapper<SubTask>> {
            override fun onResponse(
                call: Call<ResponseWrapper<SubTask>>,
                response: Response<ResponseWrapper<SubTask>>
            ) {
                isUpdatingSubtask.value = false
                if (response.isSuccessful) {
                    // refresh task detail
                    fetchTaskDetail(token, response.body()?.data?.task_id ?:0)
                }
            }

            override fun onFailure(call: Call<ResponseWrapper<SubTask>>, t: Throwable) {
                isUpdatingSubtask.value = false
            }
        })
    }


    suspend fun uploadProgressReport(
        token: String,
        subtaskId: Int,
        uri: Uri,
        context: Context
    ) {
        if (_isUploading.value) return
        _isUploading.value = true

        try {
            val inputStream = context.contentResolver.openInputStream(uri)
            val tempFile = File.createTempFile("upload_", ".pdf", context.cacheDir)
            tempFile.outputStream().use { fileOut ->
                inputStream?.copyTo(fileOut)
            }

            val requestFile = tempFile.asRequestBody("application/pdf".toMediaTypeOrNull())

            // 🔁 GANTI 'laporan' -> 'file' (HARUS SAMA DENGAN Laravel)
            val body = MultipartBody.Part.createFormData(
                "file",           // <- ini yang penting
                tempFile.name,
                requestFile
            )

            val response = RetrofitInstance.api.uploadProgressReport(
                subtaskId,
                body,
                "Bearer $token"
            )

            _isUploading.value = false
            if (response.isSuccessful) {
                Toast.makeText(context, "Upload sukses", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(
                    context,
                    "Upload gagal: ${response.errorBody()?.string()}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        } catch (e: Exception) {
            _isUploading.value = false
            Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }




}


