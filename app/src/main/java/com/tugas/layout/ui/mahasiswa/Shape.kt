package com.tugas.layout.ui.mahasiswa


import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection

class WaveShape(private val waveHeightFraction: Float = 0.5f) : Shape {
    override fun createOutline(
        size: Size,
        layoutDirection: LayoutDirection,
        density: Density
    ): Outline {
        return Outline.Generic(
            path = Path().apply {
                // Mulai dari kiri atas
                moveTo(0f, 0f)
                lineTo(size.width, 0f) // Garis lurus ke kanan atas

                // Membuat gelombang di kanan
                val waveStart = size.width * 0.5f // Dimana gelombang mulai (sekitar tengah atau lebih ke kanan)
                val wavePeak = size.width * 0.75f // Puncak gelombang
                val waveEnd = size.width * 0.95f // Akhir gelombang

                // Titik kontrol untuk membuat kurva gelombang
                val controlPoint1X = size.width * 0.8f // Titik kontrol pertama untuk gelombang ke bawah
                val controlPoint1Y = size.height * waveHeightFraction // Seberapa rendah gelombang turun

                val controlPoint2X = size.width * 0.9f // Titik kontrol kedua untuk gelombang ke atas
                val controlPoint2Y = size.height * 0.1f // Seberapa tinggi gelombang naik (sedikit di atas)

                val controlPoint3X = size.width * 0.98f // Titik kontrol ketiga untuk kembali ke bawah
                val controlPoint3Y = size.height * waveHeightFraction * 1.5f // Lebih rendah dari sebelumnya

                val controlPoint4X = size.width * 1.0f // Titik kontrol keempat untuk melengkung ke kanan bawah
                val controlPoint4Y = size.height * 1.0f // Ke kanan bawah penuh

                // Mulai gelombang dari kanan atas, lalu melengkung ke bawah, ke atas, dan kembali ke bawah ke sudut kanan bawah
                cubicTo(
                    x1 = size.width * 0.9f, y1 = 0f, // Kontrol dekat kanan atas
                    x2 = size.width, y2 = size.height * waveHeightFraction * 0.5f, // Kontrol melengkung ke bawah
                    x3 = size.width, y3 = size.height // Langsung ke kanan bawah
                )


                // Untuk bentuk seperti desain Anda, yang lebih seperti "blob" dari kanan atas ke bawah,
                // kita bisa membuatnya lebih sederhana dengan satu atau dua kurva
                reset() // Reset path sebelumnya
                moveTo(0f, 0f) // Kiri atas
                lineTo(size.width * 0.6f, 0f) // Garis lurus ke kanan sampai sekitar 60% lebar

                // Kurva besar yang membentuk blob dari kanan atas ke kanan bawah
                quadraticBezierTo(
                    x1 = size.width * 1.1f, // Titik kontrol di luar batas kanan untuk efek blob
                    y1 = size.height * 0.5f, // Tengah tinggi
                    x2 = size.width,
                    y2 = size.height // Sudut kanan bawah
                )
                lineTo(0f, size.height) // Garis lurus ke kiri bawah
                close() // Tutup shape
            }
        )
    }
}