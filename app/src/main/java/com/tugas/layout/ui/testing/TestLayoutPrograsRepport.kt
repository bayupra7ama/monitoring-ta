package ai.codia.x.composeui.demo

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tugas.layout.R
import com.tugas.layout.ui.theme.CodiaDemoComposeUITheme

/**
 * Created by codia-figma
 */
@Composable
fun CodiaMainView() {
    // Box-1020:8-Manajemen Tugas dan Subtugas
    Box(
        contentAlignment = Alignment.TopStart,
        modifier = Modifier
            .background(Color(0xfff5f2f2))
            .size(375.dp, 812.dp)
            .clipToBounds(),
    ) {
        // Image-1020:9-Vector
        Image(
            painter = painterResource(id = R.drawable.image1_10209),
            contentDescription = null,
            contentScale = ContentScale.Fit,
            modifier = Modifier
                .align(Alignment.TopStart)
                .offset(x = 252.813.dp, y = 99.768.dp)
                .size(280.312.dp, 304.687.dp),
        )
        // Image-1020:10-Group 65
        Image(
            painter = painterResource(id = R.drawable.image2_102010),
            contentDescription = null,
            contentScale = ContentScale.Fit,
            modifier = Modifier
                .align(Alignment.TopStart)
                .offset(x = 17.dp, y = 31.dp)
                .size(36.dp, 36.dp),
        )
        // Image-1020:14-Vector
        Image(
            painter = painterResource(id = R.drawable.image3_102014),
            contentDescription = null,
            contentScale = ContentScale.Fit,
            modifier = Modifier
                .align(Alignment.TopStart)
                .offset(x = -50.dp, y = 376.dp)
                .size(527.094.dp, 540.963.dp),
        )
        // Text-1020:15-Laporan Progres
        Text(
            modifier = Modifier
                .align(Alignment.TopStart)
                .wrapContentSize()
                .offset(x = 76.dp, y = 41.dp),
            text = "Laporan Progres",
            color = Color(0xff000000),
            fontSize = 15.sp,
            fontWeight = FontWeight.Normal,
            textAlign = TextAlign.Left,
            overflow = TextOverflow.Ellipsis,
        )
        // Image-1020:16-Vector
        Image(
            painter = painterResource(id = R.drawable.image4_102016),
            contentDescription = null,
            contentScale = ContentScale.Fit,
            modifier = Modifier
                .align(Alignment.TopStart)
                .offset(x = 200.813.dp, y = 2.dp)
                .size(280.312.dp, 304.687.dp),
        )
        // Image-1020:17-Group 77
        Image(
            painter = painterResource(id = R.drawable.image5_102017),
            contentDescription = null,
            contentScale = ContentScale.Fit,
            modifier = Modifier
                .align(Alignment.TopStart)
                .offset(x = 17.dp, y = 31.dp)
                .size(36.dp, 36.dp),
        )
        // Image-1020:21-Group 147
        Image(
            painter = painterResource(id = R.drawable.image6_102021),
            contentDescription = null,
            contentScale = ContentScale.Fit,
            modifier = Modifier
                .align(Alignment.TopStart)
                .offset(x = 0.dp, y = 724.dp)
                .size(375.dp, 88.dp),
        )
    }
}

@Preview(showBackground = true)
@Composable
fun CodiaMainViewPreview() {
    CodiaDemoComposeUITheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            val scrollState = rememberScrollState()
            Column(modifier = Modifier.verticalScroll(scrollState)) {
                CodiaMainView()
            }
        }
    }
}
