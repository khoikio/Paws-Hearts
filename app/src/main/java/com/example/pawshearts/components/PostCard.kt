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
import com.example.pawshearts.data.PetPost

@Composable
fun PostCard(post: PetPost, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {

            // Thông tin người đăng
            UserInfoRow()

            Spacer(modifier = Modifier.height(12.dp))

            // Tiêu đề bài đăng
            Text(
                text = post.title,
                style = MaterialTheme.typography.titleMedium
            )

            Spacer(modifier = Modifier.height(4.dp))

            // Thông tin thú cưng (giống grab pet app)
            Text(
                text = "${post.breed} • ${post.ageMonth} tháng • ${post.weightKg} kg • ${post.gender}",
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Mô tả
            Text(
                text = post.description,
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Ảnh thú cưng (lấy ảnh đầu tiên trong list)
            AsyncImage(
                model = post.imageURL.firstOrNull(),
                contentDescription = post.title,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(260.dp)
                    .clip(MaterialTheme.shapes.medium),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Địa điểm
            Text(
                text = "📍 ${post.location}",
                color = Color.Gray,
                style = MaterialTheme.typography.bodySmall
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Nút tương tác
            InteractionRow()
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
            AsyncImage(
                model = "https://picsum.photos/200",
                contentDescription = "User Avatar",
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(Color.LightGray)
            )

            Spacer(modifier = Modifier.width(8.dp))

            Column {
                Text(text = "Người đăng ẩn danh", style = MaterialTheme.typography.titleSmall)
                Text(text = "56 phút trước", color = Color.Gray, style = MaterialTheme.typography.bodySmall)
            }
        }

        IconButton(onClick = { /* More options */ }) {
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
        Row(verticalAlignment = Alignment.CenterVertically) {
            InteractionButton(icon = Icons.Default.FavoriteBorder, text = "128") {}
            Spacer(modifier = Modifier.width(24.dp))
            InteractionButton(icon = Icons.Default.ChatBubbleOutline, text = "45") {}
        }

        InteractionButton(icon = Icons.Default.Share, text = "Chia sẻ") {}
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
        Icon(imageVector = icon, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(24.dp))
        Spacer(modifier = Modifier.width(6.dp))
        Text(text = text, color = Color.Gray, style = MaterialTheme.typography.bodyMedium)
    }
}
