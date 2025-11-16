package com.example.pawshearts.settings

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.pawshearts.ui.theme.Theme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
// khai báo một là đường đi nút back để quay lại 2 là hiệu lệnh (quản gia)
fun SettingsScreen(nav: NavController, themeViewModel: SettingViewModel) {

    // câu lệnh đăng ký theo dõi trạng thái của chế độ chủ đề và lưu trữ nó trong biến isDarkMode
    val isDarkMode by themeViewModel.isDarkMode.collectAsStateWithLifecycle()


    Scaffold( // khung sườn đặt các nút, khung
        topBar = {
            TopAppBar(
                title = { Text("Cài đặt") },
                navigationIcon = { // tham số của topbar để đặt một icon về phía bên trái của thanh tiêu đề
                    IconButton(onClick = { nav.navigateUp() }) { // nút Back quay lại màn hình trước
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Quay lại")
                    }
                }
            )
        }
    ) { paddingValues -> // phần thân(content) của khung sườn(scaffold) đẩy nội dung xuống bên dưới thanh tiêu đề

        Column( // xếp tu trên xuống
            modifier = Modifier
                .fillMaxSize() // lấy toàn màn hình
                .padding(paddingValues) // đẩy nội dung xuống dưới thanh tiêu đề
                .padding(16.dp) // đẩy ui lùi vào trong
        ) {
            Row( // nằm ngang
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically, // verticalAlignment: chỉnh dọc, CenterVertically canh giữa
                horizontalArrangement = Arrangement.SpaceBetween // xếp đều theo 2 đầu chiều ngang
            ) {
                Text("Chế độ tối", style = MaterialTheme.typography.titleLarge)
                Switch(
                    checked = isDarkMode, // if bên viewmodel sáng hay tối
                    onCheckedChange = { themeViewModel.toggleDarkMode() } // nút nhấn đổi âfu
                )
            }
        }
    }
}
//==============================================================
// HÀM PREVIEW ĐỂ MÀY XEM TRƯỚC GIAO DIỆN (≧✯◡✯≦)
//==============================================================
@Preview(showBackground = true, name = "Chế độ Sáng")
@Composable
fun SettingsScreenPreviewLight() {
    // 1. Tạo ra các đối tượng "giả" để Composable có thể chạy
    val fakeNavController = rememberNavController()
    val fakeRepository = SettingsRepository(LocalContext.current)
    val fakeViewModel = SettingViewModel(fakeRepository)

    // 2. Bọc trong Theme để xem đúng màu sắc
    Theme(darkTheme = false) {
        SettingsScreen(
            nav = fakeNavController,
            themeViewModel = fakeViewModel
        )
    }
}

@Preview(showBackground = true, name = "Chế độ Tối")
@Composable
fun SettingsScreenPreviewDark() {
    val fakeNavController = rememberNavController()
    val fakeRepository = SettingsRepository(LocalContext.current)
    val fakeViewModel = SettingViewModel(fakeRepository)

    // Bật darkTheme = true để xem trước giao diện nền tối
    Theme(darkTheme = true) {
        SettingsScreen(
            nav = fakeNavController,
            themeViewModel = fakeViewModel
        )
    }
}