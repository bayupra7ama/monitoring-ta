package com.tugas.helper

import android.content.Context
import android.graphics.Bitmap
import android.graphics.pdf.PdfRenderer
import android.net.Uri
import android.os.Build
import android.os.ParcelFileDescriptor
import androidx.annotation.RequiresApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import okio.buffer
import okio.sink
import java.io.File
import java.io.FileOutputStream
import java.time.Duration
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

object PdfUtils {
    fun renderPdfPage(context: Context, file: File, pageIndex: Int): Bitmap? {
        val fileDescriptor = ParcelFileDescriptor.open(file, ParcelFileDescriptor.MODE_READ_ONLY)
        val pdfRenderer = PdfRenderer(fileDescriptor)
        val page = pdfRenderer.openPage(pageIndex)

        val bitmap = Bitmap.createBitmap(page.width, page.height, Bitmap.Config.ARGB_8888)
        page.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)
        page.close()
        pdfRenderer.close()
        fileDescriptor.close()

        return bitmap
    }

    fun copyUriToFile(context: Context, uri: Uri): File {
        val inputStream = context.contentResolver.openInputStream(uri)
        val file = File(context.cacheDir, "temp.pdf")
        val outputStream = FileOutputStream(file)
        inputStream?.copyTo(outputStream)
        inputStream?.close()
        outputStream.close()
        return file
    }
}
suspend fun downloadPdfFile(context: android.content.Context, url: String): File {
    return withContext(Dispatchers.IO) {
        val client = OkHttpClient()
        val request = Request.Builder().url(url).build()

        val response = client.newCall(request).execute()
        if (!response.isSuccessful) throw Exception("Gagal download PDF")

        val tempFile = File.createTempFile("temp_pdf", ".pdf", context.cacheDir)

        response.body?.source()?.use { source ->
            tempFile.sink().buffer().use { sink ->
                sink.writeAll(source)
            }
        }

        tempFile
    }
}

suspend fun renderPdfToBitmaps(context: android.content.Context, file: File): List<Bitmap> {
    return withContext(Dispatchers.IO) {
        val renderer = android.graphics.pdf.PdfRenderer(
            android.os.ParcelFileDescriptor.open(file, android.os.ParcelFileDescriptor.MODE_READ_ONLY)
        )

        val bitmaps = mutableListOf<Bitmap>()
        for (i in 0 until renderer.pageCount) {
            val page = renderer.openPage(i)

            val bitmap = Bitmap.createBitmap(
                page.width,
                page.height,
                Bitmap.Config.ARGB_8888
            )
            page.render(bitmap, null, null, android.graphics.pdf.PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)
            bitmaps.add(bitmap)

            page.close()
        }
        renderer.close()
        bitmaps
    }
}

@RequiresApi(Build.VERSION_CODES.O)
fun timeAgo(isoString: String): String {
    return try {
        val odt = OffsetDateTime.parse(isoString)
        val duration = Duration.between(odt, OffsetDateTime.now())
        when {
            duration.toMinutes() < 1 -> "Baru saja"
            duration.toMinutes() < 60 -> "${duration.toMinutes()} menit yang lalu"
            duration.toHours() < 24 -> "${duration.toHours()} jam yang lalu"
            else -> "${duration.toDays()} hari yang lalu"
        }
    } catch (e: Exception) {
        isoString
    }
}
