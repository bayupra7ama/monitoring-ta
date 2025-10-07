package com.tugas.layout.ui.document

import android.content.Context
import android.graphics.Bitmap
import android.graphics.pdf.PdfRenderer
import android.os.ParcelFileDescriptor
import android.widget.ImageView
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import java.io.File

@Composable
fun PdfRendererView(context: Context, file: File) {
    AndroidView(factory = {
        ImageView(it).apply {
            val fileDescriptor = ParcelFileDescriptor.open(file, ParcelFileDescriptor.MODE_READ_ONLY)
            val renderer = PdfRenderer(fileDescriptor)
            val page = renderer.openPage(0)

            val bitmap = Bitmap.createBitmap(
                page.width, page.height,
                Bitmap.Config.ARGB_8888
            )
            page.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)
            setImageBitmap(bitmap)

            page.close()
            renderer.close()
            fileDescriptor.close()
        }
    }, modifier = Modifier.fillMaxSize())
}
