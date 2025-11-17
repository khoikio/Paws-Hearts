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
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.google.firebase.firestore.FirebaseFirestore
import androidx.compose.ui.tooling.preview.Preview
import com.example.pawshearts.ui.theme.Theme
import com.google.firebase.Timestamp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationScreen(
    userId: String
) {
    val repo = remember {
        NotificationRepository(
            remote = NotificationFirebaseStore(FirebaseFirestore.getInstance())
        )
    }

    val viewModel: NotificationViewModel = viewModel(
        factory = NotificationViewModelFactory(repo)
    )

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
    val bgColor = if (!notification.isRead)  MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f) else Color.Transparent

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(bgColor)
            .padding(horizontal = 12.dp, vertical = 8.dp)
    ) {
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
                text = "${notification.actorName ?: "Ai đó"} ${notification.message}",
                style = MaterialTheme.typography.bodyLarge
            )
        }

        IconButton(onClick = onDelete) {
            Icon(Icons.Default.Close, contentDescription = "Xóa")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun NotificationPreviewLight() {
    val fakeList = listOf(
        Notification(
            id = "1",
            userId = "user1",
            actorName = "Kio",
            type = "like",
            message = "đã thích bài viết của bạn 🧡",
            isRead = false,
            createdAt = Timestamp.now()
        ),
        Notification(
            id = "2",
            userId = "user1",
            actorName = "Mun",
            type = "comment",
            message = "đã bình luận: \"Dễ thương quá!\"",
            isRead = true,
            createdAt = Timestamp.now()
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
