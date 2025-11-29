package com.example.pawshearts.notification

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Comment
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import coil.compose.rememberAsyncImagePainter
import com.example.pawshearts.R // ⚠️ Đảm bảo import đúng package R của project mày
import com.google.firebase.auth.FirebaseAuth

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationScreen(
    navController: NavHostController,
    viewModel: NotificationViewModel
) {
    val userId = FirebaseAuth.getInstance().currentUser?.uid

    val notifications by viewModel.notifications.collectAsStateWithLifecycle()
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()
    val error by viewModel.error.collectAsStateWithLifecycle()

    // Gọi load khi vào màn hình
    LaunchedEffect(userId) {
        if (userId != null) {
            viewModel.loadNotifications(userId)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Thông báo 🐾", fontWeight = FontWeight.Bold) },
                actions = {
                    IconButton(onClick = {
                        if (userId != null) viewModel.clearAll(userId)
                    }) {
                        Icon(Icons.Default.Delete, contentDescription = "Xóa tất cả")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant,
                    titleContentColor = MaterialTheme.colorScheme.primary,
                    navigationIconContentColor = MaterialTheme.colorScheme.primary
                )
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            when {
                // 1. Đang tải
                isLoading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }

                // 2. Có lỗi
                error != null -> {
                    Text(
                        text = "Lỗi: $error",
                        modifier = Modifier.align(Alignment.Center),
                        color = MaterialTheme.colorScheme.error
                    )
                }

                // 3. Không có thông báo
                notifications.isEmpty() -> {
                    Column(
                        modifier = Modifier.align(Alignment.Center),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.Notifications,
                            contentDescription = null,
                            tint = Color.Gray,
                            modifier = Modifier.size(64.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Chưa có thông báo nào",
                            style = MaterialTheme.typography.bodyLarge,
                            color = Color.Gray
                        )
                    }
                }

                // 4. Có dữ liệu -> Hiện danh sách
                else -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(vertical = 8.dp)
                    ) {
                        items(
                            items = notifications,
                            key = { it.id.ifEmpty { it.hashCode().toString() } }
                        ) { notification ->
                            NotificationItem(
                                noti = notification,
                                onDelete = { viewModel.deleteNotification(notification.id) },
                                onClick = {
                                    // TODO: Xử lý khi bấm vào (VD: nhảy tới bài viết)
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

// ==========================================
// 👇 GIAO DIỆN TỪNG DÒNG THÔNG BÁO (ITEM) 👇
// ==========================================

@Composable
fun NotificationItem(
    noti: Notification,
    onDelete: () -> Unit,
    onClick: () -> Unit = {}
) {
    // 1. Xác định chế độ Sáng/Tối
    val isDark = isSystemInDarkTheme()

    // 2. Chọn màu nền (Background Color)
    val containerColor = if (isDark) {
        // Tối: Nâu sẫm (Chưa đọc) vs Xám đen (Đã đọc)
        if (!noti.isRead) Color(0xFF3E2723) else Color(0xFF1E1E1E)
    } else {
        // Sáng: Cam sữa (Chưa đọc) vs Trắng (Đã đọc)
        if (!noti.isRead) Color(0xFFFFF3E0) else Color(0xFFFFFFFF)
    }

    // 3. Chọn màu chữ (Content Color) -> QUAN TRỌNG ĐỂ KHÔNG BỊ MÙ CHỮ
    val contentColor = if (isDark) Color.White else Color.Black

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp, horizontal = 8.dp)
            .clickable { onClick() },

        colors = CardDefaults.cardColors(
            containerColor = containerColor,
            contentColor = contentColor // Áp dụng màu chữ
        ),

        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),

        // Viền cam nhạt cho thẻ chưa đọc ở chế độ sáng (cho đẹp)
        border = if (!isDark && !noti.isRead) BorderStroke(1.dp, Color(0xFFFFE0B2)) else null
    ) {
        Row(
            modifier = Modifier
                .padding(12.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.Top
        ) {
            // --- AVATAR ---
            Box {
                Image(
                    painter = if (!noti.actorAvatarUrl.isNullOrEmpty()) {
                        rememberAsyncImagePainter(noti.actorAvatarUrl)
                    } else {
                        painterResource(R.drawable.avatardefault)
                    },
                    contentDescription = "Avatar",
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .border(1.dp, contentColor.copy(alpha = 0.2f), CircleShape),
                    contentScale = ContentScale.Crop
                )

                // Icon nhỏ góc dưới (Tim/Comment)
                Icon(
                    imageVector = getIconByType(noti.type),
                    contentDescription = null,
                    tint = getIconColorByType(noti.type),
                    modifier = Modifier
                        .size(20.dp)
                        .align(Alignment.BottomEnd)
                        .clip(CircleShape)
                        .background(Color.White) // Nền trắng cho icon nhỏ để nổi bật
                        .border(1.dp, Color.LightGray, CircleShape)
                        .padding(2.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            // --- NỘI DUNG CHỮ ---
            Column(modifier = Modifier.weight(1f)) {
                val name = noti.actorName ?: "Ai đó"
                val actionText = getActionText(noti.type)

                Text(
                    text = buildAnnotatedString {
                        withStyle(SpanStyle(fontWeight = FontWeight.Bold)) {
                            append(name)
                        }
                        append(" $actionText")
                    },
                    style = MaterialTheme.typography.bodyMedium,
                    color = contentColor // Màu chữ chuẩn
                )

                Spacer(modifier = Modifier.height(4.dp))

                // Thời gian
                Text(
                    text = formatTimestamp(noti.createdAt),
                    style = MaterialTheme.typography.labelSmall,
                    color = contentColor.copy(alpha = 0.6f) // Màu chữ mờ hơn tí
                )
            }

            // --- NÚT XÓA ---
            IconButton(
                onClick = onDelete,
                modifier = Modifier.size(24.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Xóa",
                    tint = contentColor.copy(alpha = 0.4f),
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}

// ==========================================
// 👇 CÁC HÀM HỖ TRỢ (UTILS) 👇
// ==========================================

fun getActionText(type: String): String {
    return when (type) {
        "LIKE" -> "đã thích bài viết của bạn ❤️"
        "COMMENT" -> "đã bình luận bài viết của bạn 💬"
        "NEW_POST" -> "vừa đăng một bài viết mới 📝"
        "ADOPT_REQ" -> "muốn nhận nuôi thú cưng của bạn 🐾"
        else -> "đã tương tác với bạn"
    }
}

fun getIconByType(type: String): ImageVector {
    return when (type) {
        "LIKE" -> Icons.Default.Favorite
        "COMMENT" -> Icons.Default.Comment
        else -> Icons.Default.Notifications
    }
}

@Composable
fun getIconColorByType(type: String): Color {
    return when (type) {
        "LIKE" -> Color(0xFFE91E63) // Hồng đậm
        "COMMENT" -> Color(0xFF2196F3) // Xanh dương
        else -> MaterialTheme.colorScheme.primary
    }
}

fun formatTimestamp(timestamp: com.google.firebase.Timestamp): String {
    try {
        val now = System.currentTimeMillis()
        val time = timestamp.toDate().time
        val diff = now - time

        val seconds = diff / 1000
        val minutes = seconds / 60
        val hours = minutes / 60
        val days = hours / 24

        return when {
            minutes < 1 -> "Vừa xong"
            minutes < 60 -> "$minutes phút trước"
            hours < 24 -> "$hours giờ trước"
            else -> "$days ngày trước"
        }
    } catch (e: Exception) {
        return "Gần đây"
    }
}