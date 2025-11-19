package com.example.pawshearts.notification

import android.app.Application
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationScreen(
    userId: String
) {
    val context = LocalContext.current.applicationContext as Application
    val viewModel: NotificationViewModel = viewModel(factory = NotificationViewModelFactory(context))

    val notifications by viewModel.getNotifications(userId).collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Thông báo") },
                actions = {
                    if (notifications.isNotEmpty()) {
                        IconButton(onClick = { viewModel.clearAllNotifications(userId) }) {
                            Icon(Icons.Default.DeleteSweep, contentDescription = "Xóa tất cả")
                        }
                    }
                }
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(notifications) { notification ->
                Card(
                    shape = MaterialTheme.shapes.large,
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // THÊM AVATAR
                        AsyncImage(
                            model = notification.actorAvatarUrl,
                            contentDescription = "Avatar",
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                        )
                        Spacer(modifier = Modifier.width(16.dp))

                        // NỐI TÊN VÀ NỘI DUNG
                        val fullMessage = buildAnnotatedString {
                            withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                                append(notification.actorName ?: "Ai đó")
                            }
                            append(" ")
                            append(notification.message)
                        }
                        Text(
                            text = fullMessage,
                            modifier = Modifier.weight(1f),
                            style = MaterialTheme.typography.bodyLarge
                        )

                        Spacer(modifier = Modifier.width(8.dp))
                        IconButton(onClick = { viewModel.deleteNotification(notification.id) }) {
                            Icon(Icons.Default.Delete, contentDescription = "Xóa")
                        }
                        Button(onClick = { viewModel.sendTest() }) {
                            Text("Test noti")
                        }
                    }
                }
            }
        }
    }
}
