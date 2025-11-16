package com.example.pawshearts.profile


import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.pawshearts.ui.theme.DarkOrange  // ✅ Giữ lại dòng này

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileTopBar(
    onBackClick: () -> Unit = {},
) {
    Column {
        CenterAlignedTopAppBar(
            title = {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    // 🔶 Tiêu đề Paws & Hearts: màu cam, in đậm
                    Text(
                        text = "Paws & Hearts",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = DarkOrange,
                        textAlign = TextAlign.Center
                    )

                    // 🔹 Dòng phụ Hồ sơ
                    Text(
                        text = "Hồ sơ",
                        fontSize = 18.sp,
                        color = DarkOrange,
                        textAlign = TextAlign.Center
                    )
                }
            },
            colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                containerColor = MaterialTheme.colorScheme.background,
                titleContentColor = MaterialTheme.colorScheme.onBackground
            )
        )

        Divider(
            color = Color.LightGray.copy(alpha = 0.6f),
            thickness = 1.dp,
            modifier = Modifier.fillMaxWidth()
        )
    }
}
