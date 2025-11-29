package com.example.pawshearts.activities

import android.content.Intent
import android.net.Uri
import android.widget.Toast
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.pawshearts.navmodel.Routes
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import coil.compose.AsyncImage
import com.example.pawshearts.auth.AuthResult
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

    // Lấy thông tin user hiện tại
    val currentUserData by authViewModel.userProfile.collectAsStateWithLifecycle()

    // Lấy trạng thái đăng ký
    val isRegistered by activityViewModel.isRegistered.collectAsStateWithLifecycle()
    val registerResult by activityViewModel.registerState.collectAsStateWithLifecycle()
    val activity by activityViewModel.selectedActivity.collectAsStateWithLifecycle()

    LaunchedEffect(activityId) {
        activityViewModel.getActivityById(activityId)
        // Kiểm tra xem user này đăng ký chưa để hiện nút cho đúng
        if (currentUserData != null) {
            activityViewModel.checkRegistrationStatus(activityId, currentUserData!!.userId)
        }
    }
    // Xử lý thông báo kết quả
    LaunchedEffect(registerResult) {
        when(registerResult) {
            is AuthResult.Success -> {
                Toast.makeText(context, "Đăng ký thành công! 🎉", Toast.LENGTH_SHORT).show()
                activityViewModel.resetRegisterState()
            }
            is AuthResult.Error -> {
                Toast.makeText(context, "Lỗi: ${(registerResult as AuthResult.Error).message}", Toast.LENGTH_SHORT).show()
                activityViewModel.resetRegisterState()
            }
            else -> {}
        }
    }


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
//                actions = {
//                    val currentActivityId = activity?.id
//                    // NÚT SỬA (Chỉ hiển thị nếu là Admin, logic quyền admin cần được thêm)
//                    if (currentActivityId != null) {
//                        TextButton(
//                            onClick = {
//                                // Tự ghép chuỗi route cho màn hình sửa
//                                nav.navigate("${Routes.EDIT_ACTIVITY_SCREEN}/$activityId")
//                            }
//                        ) {
//                            Text("Sửa")
//                        }
//                    }
//                }
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
                            if (currentUserData != null) {
                                // Gọi hàm đăng ký
                                activityViewModel.registerToActivity(
                                    activityId = activityId,
                                    userId = currentUserData!!.userId,
                                    userName = currentUserData!!.username ?: "User",
                                    userAvatar = currentUserData!!.profilePictureUrl ?: ""
                                )
                            } else {
                                Toast.makeText(context, "Vui lòng đăng nhập lại", Toast.LENGTH_SHORT).show()
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        // Disable nút nếu: Đã đăng ký rồi HOẶC Đang loading
                        enabled = !isRegistered && registerResult !is AuthResult.Loading,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isRegistered) Color.Gray else MaterialTheme.colorScheme.primary
                        )
                    ) {
                        if (registerResult is AuthResult.Loading) {
                            CircularProgressIndicator(modifier = Modifier.size(24.dp), color = Color.White)
                        } else {
                            Text(
                                text = if (isRegistered) "✅ Đã đăng ký tham gia" else "✍️ Đăng ký tham gia ngay"
                            )
                        }
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