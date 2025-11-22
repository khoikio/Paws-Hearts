package com.example.pawshearts.activities

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext // <--- THÊM IMPORT NÀY
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
//import com.example.pawshearts.image.NetworkImage
import com.example.pawshearts.navmodel.Routes
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.runtime.Composable
import androidx.compose.ui.layout.ContentScale
import coil.compose.AsyncImage
import com.example.pawshearts.auth.AuthViewModel


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ActivityDetailScreen(
    nav: NavHostController,
    activityViewModel: ActivityViewModel,
    // THÊM THAM SỐ authViewModel ĐỂ KIỂM TRA QUYỀN
    authViewModel: AuthViewModel,
    activityId: String
) {
    // Lấy context để mở Intent
    val context = LocalContext.current

    LaunchedEffect(activityId) {
        activityViewModel.getActivityById(activityId)
    }

    val activity by activityViewModel.selectedActivity.collectAsStateWithLifecycle()

    DisposableEffect(Unit) {
        onDispose {
            activityViewModel.clearSelectedActivity()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(activity?.title ?: "Chi tiết Hoạt động") },
                navigationIcon = {
                    IconButton(onClick = { nav.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Quay lại")
                    }
                },
                actions = {
                    val currentActivityId = activity?.id
                    // NÚT SỬA (Chỉ hiển thị nếu là Admin, logic quyền admin cần được thêm)
                    if (currentActivityId != null) {
                        TextButton(
                            onClick = {
                                // Tự ghép chuỗi route cho màn hình sửa
                                nav.navigate("${Routes.EDIT_ACTIVITY_SCREEN}/$activityId")
                            }
                        ) {
                            Text("Sửa")
                        }
                    }
                }
            )
        }
    ) { paddingValues ->
        val currentActivity = activity

        if (currentActivity == null) {
            Box(Modifier.fillMaxSize().padding(paddingValues), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .verticalScroll(rememberScrollState())
            ) {
                // Ảnh bìa
                if (currentActivity.imageUrl.isNotBlank()) {
                    // SỬ DỤNG NetworkImage CỦA BẠN ĐỂ HIỂN THỊ ẢNH THỰC TẾ
                    NetworkImage(
                        url = currentActivity.imageUrl,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                            // Bạn có thể thêm background cho mục đích placeholder/loading
                            .background(MaterialTheme.colorScheme.surfaceVariant),
                    )
                }

                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(currentActivity.title, style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold))

                    Divider()

                    Text("🗓️ Ngày: ${currentActivity.date}", style = MaterialTheme.typography.bodyLarge)
                    Text("📍 Địa điểm: ${currentActivity.location}", style = MaterialTheme.typography.bodyLarge)

                    Spacer(Modifier.height(8.dp))

                    Text(
                        "Mô tả:",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold)
                    )
                    Text(currentActivity.description, style = MaterialTheme.typography.bodyMedium)

                    Spacer(Modifier.height(16.dp))

                    // Nút đăng ký (link)
                    Button(
                        onClick = {
                            // LOGIC MỞ LINK BẰNG INTENT
                            if (currentActivity.contactLink.isNotBlank()) {
                                try {
                                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(currentActivity.contactLink))
                                    context.startActivity(intent)
                                } catch (e: Exception) {
                                    // Xử lý lỗi nếu không tìm thấy app để mở link
                                }
                            }
                        },
                        // Nút sẽ tự động bật nếu có link, và mờ đi nếu không có link
                        enabled = currentActivity.contactLink.isNotBlank()
                    ) {
                        Text("Link Đăng ký/Liên hệ")
                    }
                }
            }
        }
    }
}
@Composable
fun NetworkImage(
    url: String,
    modifier: Modifier = Modifier,
    contentDescription: String? = null
) {
    AsyncImage(
        model = url,
        contentDescription = contentDescription,
        modifier = modifier,
        contentScale = ContentScale.Crop // Đảm bảo ảnh lấp đầy không gian
    )
}