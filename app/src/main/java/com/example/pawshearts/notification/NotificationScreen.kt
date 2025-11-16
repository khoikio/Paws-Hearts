package com.example.pawshearts.notification

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.pawshearts.data.local.PawsHeartsDatabase
import com.google.firebase.firestore.FirebaseFirestore
import androidx.compose.ui.tooling.preview.Preview
import com.example.pawshearts.ui.theme.Theme  // nếu theme của m tên khác thì sửa lại

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationScreen(
    userId: String
) {
    val context = LocalContext.current

    // Lấy db + dao từ Room
    val db = remember { PawsHeartsDatabase.getDatabase(context) }
    val dao = remember { db.notificationDao() }

    // Tạo repo 1 lần
    val repo = remember {
        NotificationRepository(
            dao = dao,
            remote = NotificationFirebaseStore(FirebaseFirestore.getInstance())
        )
    }

    // Tạo ông quản gia
    val viewModel: NotificationViewModel = viewModel(
        factory = NotificationViewModelFactory(repo)
    )

    // Gọi load dữ liệu khi có userId
    LaunchedEffect(userId) {
        viewModel.loadNotifications(userId)
    }

    val notifications by viewModel.notifications.collectAsStateWithLifecycle()

    NotificationUI(
        notifications = notifications,
        onRead = { id -> viewModel.markAsRead(id) },
        onDelete = { id -> viewModel.deleteNotification(id) },
        onClearAll = { viewModel.clearAll() }
    )
}

// ────────────────────────
//  PHẦN UI LIST + NÚT XÓA HẾT
// ────────────────────────
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationUI(
    notifications: List<Notification>,
    onRead: (String) -> Unit,
    onDelete: (String) -> Unit,
    onClearAll: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Thông báo") },
                actions = {
                    if (notifications.isNotEmpty()) {
                        TextButton(onClick = onClearAll) {
                            Text("Xóa tất cả")
                        }
                    }
                }
            )
        }
    ) { padding ->
        if (notifications.isEmpty()) {
            Box(
                modifier = Modifier
                    .padding(padding)
                    .fillMaxSize(),
            ) {
                Text(
                    text = "Không có thông báo nào",
                    modifier = Modifier.padding(16.dp)
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .padding(padding)
                    .fillMaxSize()
            ) {
                items(notifications) { noti ->
                    NotificationItem(
                        notification = noti,
                        onRead = { onRead(noti.id) },
                        onDelete = { onDelete(noti.id) }
                    )
                }
            }
        }
    }
}

@Composable
fun NotificationItem(
    notification: Notification,
    onRead: () -> Unit,
    onDelete: () -> Unit
) {
    val bgColor = if (!notification.isRead)  MaterialTheme.colorScheme.onPrimary else Color.White

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(bgColor)
            .padding(horizontal = 12.dp, vertical = 8.dp)
    ) {
        // Avatar
        AsyncImage(
            model = notification.actorAvatarUrl,
            contentDescription = null,
            modifier = Modifier
                .size(48.dp)
        )

        Spacer(modifier = Modifier.width(12.dp))

        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = notification.actorName ?: "Người dùng",
                style = MaterialTheme.typography.titleMedium
            )

            Text(
                text = notification.message,
                style = MaterialTheme.typography.bodyMedium
            )
        }

        IconButton(onClick = onDelete) {
            Icon(Icons.Default.Close, contentDescription = "Xóa")
        }
    }

    // Khi user bấm (m sau này có thể wrap Row bằng .clickable để gọi onRead)
    LaunchedEffect(notification.id, notification.isRead) {
        // chỗ này hiện tại chưa dùng click, nên tạm để trống
        // nếu m muốn: khi click item thì gọi onRead()
    }
}


@Preview(
    showBackground = true,
    showSystemUi = true,
    name = "Notification – Light"
)
@Composable
fun NotificationPreviewLight() {
    val fakeList = listOf(
        Notification(
            id = "1",
            userId = "user1",
            actorId = "u2",
            actorName = "Kio",
            actorAvatarUrl = null,
            type = "like",
            message = "Kio đã thích bài viết của bạn 🧡",
            postId = "post1",
            isRead = false
        ),
        Notification(
            id = "2",
            userId = "user1",
            actorId = "u3",
            actorName = "Mun",
            actorAvatarUrl = null,
            type = "comment",
            message = "Mun đã bình luận: \"Dễ thương quá!\"",
            postId = "post2",
            isRead = true
        )
    )

    Theme(darkTheme = false) {
        NotificationUI(
            notifications = fakeList,
            onRead = {},
            onDelete = {},
            onClearAll = {}
        )
    }
}

//@Preview(
//    showBackground = true,
//    showSystemUi = true,
//    name = "Notification – Dark"
//)
//@Composable
//fun NotificationPreviewDark() {
//    val fakeList = listOf(
//        Notification(
//            id = "1",
//            userId = "user1",
//            actorId = "u2",
//            actorName = "Kio",
//            actorAvatarUrl = null,
//            type = "like",
//            message = "Kio đã thích bài viết của bạn 🧡",
//            postId = "post1",
//            isRead = false
//        ),
//        Notification(
//            id = "2",
//            userId = "user1",
//            actorId = "u3",
//            actorName = "Mun",
//            actorAvatarUrl = null,
//            type = "system",
//            message = "Chào mừng bạn đến với Paws & Hearts!",
//            postId = null,
//            isRead = true
//        )
//    )
//
//    Theme(darkTheme = true) {
//        NotificationUI(
//            notifications = fakeList,
//            onRead = {},
//            onDelete = {},
//            onClearAll = {}
//        )
//    }
//}
