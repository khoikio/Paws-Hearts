package com.example.pawshearts.ui.theme

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Pets
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector

/**
 * AppIcons – nơi gom toàn bộ icon dùng trong app.
 * Ưu điểm: sau này muốn đổi icon (ví dụ từ Filled sang Outlined) chỉ sửa ở đây.
 */
object AppIcons {
    // Bottom navigation
    val Home: ImageVector = Icons.Filled.Home
    val Donate: ImageVector = Icons.Filled.Favorite
    val Adopt: ImageVector = Icons.Filled.Pets
    val Profile: ImageVector = Icons.Filled.Person

    val Notification: ImageVector = Icons.Filled.Notifications

    // Common actions
    val Location: ImageVector = Icons.Filled.LocationOn
    val Info: ImageVector = Icons.Filled.Info
    val Warning: ImageVector = Icons.Filled.Warning
}

@Composable
fun BottomNavTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = lightColorScheme(
            primary = Color(0xFFFF6B35),
            secondary = Color(0xFFFF8C42),
            background = Color(0xFF1A1A1A),
            surface = Color(0xFFF5E6E8),
            onPrimary = Color.White,
            onSecondary = Color.White,
            onBackground = Color.White,
            onSurface = Color(0xFF1A1A1A)
        ),
        content = content
    )
}