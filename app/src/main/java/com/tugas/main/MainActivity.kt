package com.tugas.main

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.tugas.route.MainApp
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint // ⬅️ WAJIB!

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MainApp() // ⬅️ ini panggil NavHost kita
        }
    }
}
