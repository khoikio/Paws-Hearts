package com.example.pawshearts.screens

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
import com.example.pawshearts.ui.theme.AppColor

@Composable
fun PostCard(post: PetPost, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp) // Khoảng cách giữa các card
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp) // Thêm đổ bóng nhẹ
    ) {
        Column {
            // Phần nội dung
            Column(modifier = Modifier.padding(16.dp)) {
                // Thông tin người đăng
                UserInfoRow()

                Spacer(modifier = Modifier.height(12.dp))

                // Mô tả bài đăng (dùng lại title và các thông tin khác)
                Text(
                    text = "${post.title} cần được giải cứu! Em là ${post.type}, giới tính ${post.gender}. Rất ngoan và thân thiện. Cần tìm một mái ấm yêu thương. Hãy liên hệ nếu bạn có thể giúp đỡ!",
                    style = MaterialTheme.typography.bodyLarge,
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis
                )

                // Ảnh lớn của bài đăng
            AsyncImage(
                model = post.photos.firstOrNull() ?: "",
                contentDescription = post.title,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(250.dp), // Chiều cao cố định cho ảnh
                contentScale = ContentScale.Crop // Crop ảnh để vừa với khung hình
            )


                Spacer(modifier = Modifier.height(16.dp))

                // Hàng chứa các nút tương tác (Thích, Bình luận, Chia sẻ)
                InteractionRow()
            }
        }
    }
}

@Composable
fun UserInfoRow() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            // Avatar người đăng
            AsyncImage(
                model = "https://picsum.photos/200", // Thay bằng URL avatar thật
                contentDescription = "User Avatar",
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(Color.LightGray)
            )

            Spacer(modifier = Modifier.width(8.dp))

            // Tên và thời gian đăng
            Column {
                Text(
                    text = "Vàng", // Thay bằng tên người đăng thật
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = "56 phút trước", // Thay bằng thời gian đăng thật
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )
            }
        }

        // Nút "..."
        IconButton(onClick = { /* Xử lý khi nhấn nút more */ }) {
            Icon(Icons.Default.MoreHoriz, contentDescription = "More options", tint = Color.Gray)
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
            InteractionButton(icon = Icons.Default.ChatBubbleOutline, text = "45") { /* TODO: Handle comment */ }
        }

        // Nút bên phải (Share)
        InteractionButton(icon = Icons.Default.Share, text = "Chia sẻ") { /* TODO: Handle share */ }
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
            tint = Color.Gray,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
            text = text,
            color = Color.Gray,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}
