import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.graphics.BlendMode // Import BlendMode

@Composable
fun CurvedBackgroundScreenImproved() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White) // Background utama putih
    ) {
        // Bagian background hijau melengkung
        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp) // Sesuaikan tinggi sesuai kebutuhan
        ) {
            val width = size.width
            val height = size.height
            val greenColor = Color(0xFF66BB6A) // Warna hijau utama

            // --- Gambar Background Hijau Melengkung ---
            val path = Path().apply {
                // Mulai dari kiri atas
                moveTo(0f, 0f)
                // Garis ke kanan atas
                lineTo(width, 0f)
                // Garis ke kanan bawah, sedikit di atas titik paling rendah lengkungan
                lineTo(width, height * 0.7f)

                // Kunci untuk kelengkungan yang lebih halus dan seperti di gambar:
                // Menggunakan quadraticBezierTo untuk membuat kurva yang melengkung ke atas
                // Control point akan berada di bawah garis lengkung yang sebenarnya,
                // sehingga menarik garis ke bawah sebelum melengkung ke atas di titik akhir.
                quadraticBezierTo(
                    x1 = width * 0.75f, y1 = height * 1.05f, // Control point agak ke kanan dan lebih rendah dari tinggi
                    x2 = 0f, y2 = height * 0.6f // Titik akhir kurva di kiri bawah
                )

                // Tutup path
                close()
            }

            drawPath(
                path = path,
                color = greenColor
            )

            // --- Gambar Lingkaran-lingkaran di Bagian Atas Hijau ---
            val lighterGreen = Color(0xFF81C784) // Warna hijau yang sedikit lebih terang/pastel

            // Lingkaran besar di kanan atas
            drawCircle(
                color = lighterGreen,
                radius = 50.dp.toPx(),
                center = Offset(x = width * 0.85f, y = height * 0.1f),
                // Gunakan BlendMode.SrcOver atau lainnya jika ingin efek transparan,
                // tapi biasanya default sudah cukup untuk efek overlay
            )
            // Lingkaran menengah
            drawCircle(
                color = lighterGreen,
                radius = 30.dp.toPx(),
                center = Offset(x = width * 0.95f, y = height * 0.05f)
            )
            // Lingkaran kecil
            drawCircle(
                color = lighterGreen,
                radius = 20.dp.toPx(),
                center = Offset(x = width * 0.7f, y = height * 0.03f)
            )
            // Lingkaran lain di area lengkungan
            drawCircle(
                color = lighterGreen,
                radius = 25.dp.toPx(),
                center = Offset(x = width * 0.9f, y = height * 0.25f)
            )
            drawCircle(
                color = lighterGreen,
                radius = 15.dp.toPx(),
                center = Offset(x = width * 0.6f, y = height * 0.15f)
            )
        }

        // Contoh teks di atas background
        Column(
            modifier = Modifier
                .padding(start = 16.dp, top = 60.dp) // Sesuaikan padding
        ) {
            // Teks "Back" icon (simulasi)
            Text(
                text = "<-", // Atau gunakan Icon dari Material Icons
                fontSize = 24.sp,
                color = Color.White,
                modifier = Modifier.padding(bottom = 16.dp)
            )
            Text(
                text = "Buat Akun",
                fontSize = 32.sp,
                color = Color.White
            )
            Text(
                text = "Barumu !",
                fontSize = 32.sp,
                color = Color.White
            )
        }
    }
}

@Preview(showBackground = true, widthDp = 360, heightDp = 640)
@Composable
fun CurvedBackgroundScreenImprovedPreview() {
    CurvedBackgroundScreenImproved()
}