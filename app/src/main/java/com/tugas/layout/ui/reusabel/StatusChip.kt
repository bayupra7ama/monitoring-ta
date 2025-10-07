package com.tugas.layout.ui.reusabel

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Pending
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tugas.layout.ui.theme.GraySecondary
import com.tugas.layout.ui.theme.GreenPrimary


@Composable
private fun StatusChip(status: String) {
    val (icon, color, text) = when (status.lowercase()) {
        "disetujui" -> Triple(Icons.Default.CheckCircle, GreenPrimary, "Disetujui")
        "pending" -> Triple(Icons.Default.Pending, Color(0xFFFFA000), "Pending")
        "ditolak" -> Triple(Icons.Default.Cancel, Color.Red, "Ditolak")
        else -> Triple(Icons.Default.Info, GraySecondary, "Tidak Diketahui")
    }

    Row(
        modifier = Modifier
            .background(color.copy(alpha = 0.1f), RoundedCornerShape(50))
            .padding(horizontal = 10.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = "Status: $text",
            tint = color,
            modifier = Modifier.size(18.dp)
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
            text = text,
            color = color,
            fontWeight = FontWeight.SemiBold,
            fontSize = 14.sp
        )
    }
}