package com.tugas.data.api


object RetrofitInstance {
    private const val BASE_URL = "https://brief-sawfly-square.ngrok-free.app/"

    val api: AuthApiService by lazy {
        retrofit2.Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(retrofit2.converter.gson.GsonConverterFactory.create())
            .build()
            .create(AuthApiService::class.java)
    }
}
