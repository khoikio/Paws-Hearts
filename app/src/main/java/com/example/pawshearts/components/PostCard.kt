package com.example.pawshearts.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChatBubbleOutline
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.pawshearts.PetPost
import com.example.pawshearts.ui.theme.DarkText
import com.example.pawshearts.ui.theme.GrayText

@Composable
fun PostCard(post: PetPost, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column {
            // 1. Thông tin người đăng
            UserInfoRow()

            // 2. Ảnh lớn của bài đăng
            AsyncImage(
                model = post.photos.firstOrNull() ?: "",
                contentDescription = post.title,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp), // Tăng chiều cao ảnh cho giống Figma
                contentScale = ContentScale.Crop
            )

            // Phần nội dung bên dưới ảnh
            Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)) {
                // 3. Mô tả bài đăng
                Text(
                    text = post.description,
                    style = MaterialTheme.typography.bodyLarge,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    color = DarkText
                )

                Spacer(modifier = Modifier.height(12.dp))

                // 4. Hàng chứa các nút tương tác
                InteractionRow()
            }
        }
    }
}

@Composable
fun UserInfoRow() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            // Avatar người đăng
            AsyncImage(
                model = "https://picsum.photos/seed/paws/200", // Thay bằng URL avatar thật
                contentDescription = "User Avatar",
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(Color.LightGray)
            )

            Spacer(modifier = Modifier.width(12.dp))

            // Tên và thời gian đăng
            Column {
                Text(
                    text = "Kim Gang", // Thay bằng tên người đăng thật
                    style = MaterialTheme.typography.titleMedium,
                    color = DarkText
                )
                Text(
                    text = "2 giờ trước", // Thay bằng thời gian đăng thật
                    style = MaterialTheme.typography.bodySmall,
                    color = GrayText
                )
            }
        }

        // Nút "..."
        IconButton(onClick = { /* Xử lý khi nhấn nút more */ }) {
            Icon(Icons.Default.MoreHoriz, contentDescription = "More options", tint = GrayText)
        }
    }
}

@Composable
fun InteractionRow() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        // Nhóm nút bên trái (Like, Comment)
        Row(verticalAlignment = Alignment.CenterVertically) {
            InteractionButton(icon = Icons.Default.FavoriteBorder, text = "128") { /* TODO: Handle like */ }
            Spacer(modifier = Modifier.width(24.dp))
            InteractionButton(icon = Icons.Default.ChatBubbleOutline, text = "16") { /* TODO: Handle comment */ }
        }

        // Nút bên phải (Share) - chỉ có icon
        IconButton(onClick = { /* TODO: Handle share */ }) {
            Icon(
                imageVector = Icons.Default.Share,
                contentDescription = "Share",
                tint = GrayText,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

@Composable
private fun InteractionButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    text: String,
    onClick: () -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.clickable { onClick() }
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = GrayText,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
            text = text,
            color = GrayText,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}
