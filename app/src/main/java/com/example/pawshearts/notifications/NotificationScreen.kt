package com.example.pawshearts.notifications

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.pawshearts.data.model.Notification

@Composable
fun NotificationScreen(
    nav: NavHostController,
    notificationViewModel: NotificationViewModel
) {
    val notifications by notificationViewModel.notifications.collectAsState()

    LazyColumn(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        items(notifications) { notif ->
            NotificationItem(notif) {
                notificationViewModel.markAsRead(notif.id)
            }
        }
    }
}

@Composable
fun NotificationItem(notification: Notification, onClick: () -> Unit) {
    Column(
        Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(12.dp)
    ) {
        Text(notification.title)
        Spacer(Modifier.height(4.dp))
        Text(notification.message)
    }
}
