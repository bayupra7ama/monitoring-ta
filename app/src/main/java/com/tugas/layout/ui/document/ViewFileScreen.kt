package com.tugas.layout.ui.document

import android.app.DownloadManager
import android.content.Context
import android.net.Uri
import android.os.Environment
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.navigation.NavController
import com.rizzi.bouquet.ResourceType
import com.rizzi.bouquet.VerticalPDFReader
import com.rizzi.bouquet.rememberVerticalPdfReaderState
import com.tugas.layout.R


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PdfViewerScreen(fileUrl: String, navController: NavController) {
    // State untuk PDF Reader
    val pdfState = rememberVerticalPdfReaderState(
        resource = ResourceType.Remote(fileUrl),
        isZoomEnable = true
    )

    // State untuk menu dropdown
    var menuExpanded by remember { mutableStateOf(false) }
    val context = LocalContext.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Tampilan Dokumen") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_arrow_back),
                            contentDescription = "Kembali",
                            tint = Color.Unspecified
                        )
                    }
                },
                // --- TAMBAHAN BARU DI SINI ---
                actions = {
                    Box {
                        // Tombol ikon tiga titik untuk membuka menu
                        IconButton(onClick = { menuExpanded = true }) {
                            Icon(
                                imageVector = Icons.Default.MoreVert,
                                contentDescription = "Opsi Lainnya"
                            )
                        }

                        // Menu dropdown yang akan muncul
                        DropdownMenu(
                            expanded = menuExpanded,
                            onDismissRequest = { menuExpanded = false }
                        ) {
                            // Item menu untuk download
                            DropdownMenuItem(
                                text = { Text("Download") },
                                onClick = {
                                    // Tutup menu
                                    menuExpanded = false
                                    // Panggil fungsi download
                                    downloadFile(context, fileUrl)
                                },
                                leadingIcon = {
                                    Icon(
                                        imageVector = Icons.Default.Download,
                                        contentDescription = "Download Dokumen"
                                    )
                                }
                            )
                        }
                    }
                }
                // --- AKHIR DARI TAMBAHAN ---
            )
        }
    ) { paddingValues ->
        VerticalPDFReader(
            state = pdfState,
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(MaterialTheme.colorScheme.background)
        )
    }
}

// Fungsi helper untuk logika download file
private fun downloadFile(context: Context, fileUrl: String) {
    try {
        val downloadManager = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        val uri = Uri.parse(fileUrl)
        val fileName = fileUrl.substringAfterLast('/') // Mengambil nama file dari URL

        val request = DownloadManager.Request(uri)
            .setTitle(fileName) // Judul notifikasi download
            .setDescription("Mendownload dokumen...")
            .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
            .setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fileName)
            .setAllowedOverMetered(true) // Izinkan download via data seluler
            .setAllowedOverRoaming(true)

        downloadManager.enqueue(request)
        Toast.makeText(context, "Mulai mengunduh...", Toast.LENGTH_SHORT).show()
    } catch (e: Exception) {
        Toast.makeText(context, "Gagal memulai unduhan: ${e.message}", Toast.LENGTH_LONG).show()
    }
}