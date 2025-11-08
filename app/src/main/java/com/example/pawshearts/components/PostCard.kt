package com.example.pawshearts.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChatBubbleOutline
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource // <-- T THÊM CÁI NÀY
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.pawshearts.R // <-- T THÊM CÁI NÀY (ĐỂ LẤY AVATAR DEFAULT)
import com.example.pawshearts.data.model.Post
import com.google.firebase.Timestamp // <-- T THÊM CÁI NÀY
import java.util.concurrent.TimeUnit

@Composable
fun PostCard(
    post: Post,
    currentUserId: String, // <-- THÊM (Để  biết like chưa)
    onClick: () -> Unit,
    onLikeClick: () -> Unit,
    onCommentClick: () -> Unit,
    onShareClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // nut tha tim va like
            Text(text = post.petName, style = MaterialTheme.typography.titleMedium)
            Text(
                text = "Giống: ${post.petBreed ?: "Chưa rõ"} • Giới tính: ${post.petGender ?: "Chưa rõ"}",
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray
            )
            Text(
                text = "Tuổi: ${post.petAge ?: "?"} tháng • Cân nặng: ${post.weightKg ?: "?"} kg",
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray
            )
            Spacer(modifier = Modifier.height(12.dp))

            UserInfoRow(
                avatarUrl = post.userAvatarUrl,
                username = post.username,
                timestamp = post.createdAt
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = post.petName,
                style = MaterialTheme.typography.titleMedium
            )

            Spacer(modifier = Modifier.height(4.dp))


            Text(
                text = "Giống: ${post.petBreed ?: "Chưa rõ"} • Giới tính: ${post.petGender ?: "Chưa rõ"}",
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray
            )
            Text(
                text = "Tuổi: ${post.petAge ?: "?"} tháng • Cân nặng: ${post.weightKg ?: "?"} kg",
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray
            )
            Spacer(modifier = Modifier.height(8.dp))

            // Mô tả (Giữ nguyên)
            Text(
                text = post.description,
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(10.dp))

            // LỖI 3: SỬA imageURL.firstOrNull() THÀNH imageUrl
            AsyncImage(
                model = post.imageUrl, // <-- SỬA
                contentDescription = post.petName, // <-- SỬA
                modifier = Modifier
                    .fillMaxWidth()
                    .height(260.dp)
                    .clip(MaterialTheme.shapes.medium),
                contentScale = ContentScale.Crop,
                placeholder = painterResource(id = R.drawable.avatardefault) // Thêm cái này cho nó đỡ xấu
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Địa điểm (T thêm check null)
            Text(
                text = "📍 ${post.location ?: "Không rõ"}",
                color = Color.Gray,
                style = MaterialTheme.typography.bodySmall
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Nút tương tác (Giữ nguyên
            InteractionRow(
                post = post,
                currentUserId = currentUserId,
                onLikeClick = onLikeClick,
                onCommentClick = onCommentClick,
                onShareClick = onShareClick
            )

        }
    }
}


@Composable
fun UserInfoRow(avatarUrl: String?, username: String?, timestamp: Timestamp) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            AsyncImage(
                model = avatarUrl ?: R.drawable.avatardefault, // <-- SỬA (Nếu user ko có avatar)
                contentDescription = "User Avatar",
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(Color.LightGray)
            )

            Spacer(modifier = Modifier.width(8.dp))

            Column {
                Text(text = username ?: "Người dùng PawsHearts", style = MaterialTheme.typography.titleSmall) // <-- SỬA
                Text(text = formatTimestamp(timestamp), color = Color.Gray, style = MaterialTheme.typography.bodySmall) // <-- SỬA
            }
        }

        IconButton(onClick = { /* More options */ }) {
            Icon(Icons.Default.MoreHoriz, contentDescription = "More options", tint = Color.Gray)
        }
    }
}

/**
 * HÀM NÀY ĐỂ TÍNH THỜI GIAN M ĐĂNG BÀI (5 PHÚT TRƯỚC...)
 */
@Composable
fun formatTimestamp(timestamp: Timestamp): String {
    val now = System.currentTimeMillis()
    val diff = now - timestamp.toDate().time // Lấy (ms)

    return when {
        diff < TimeUnit.MINUTES.toMillis(1) -> "Vừa xong"
        diff < TimeUnit.HOURS.toMillis(1) -> "${TimeUnit.MILLISECONDS.toMinutes(diff)} phút trước"
        diff < TimeUnit.DAYS.toMillis(1) -> "${TimeUnit.MILLISECONDS.toHours(diff)} giờ trước"
        else -> "${TimeUnit.MILLISECONDS.toDays(diff)} ngày trước"
    }
}

/**
 * HÀM NÀY M GIỮ NGUYÊN
 */
@Composable
fun InteractionRow(post: Post,
                   currentUserId: String,
                   onLikeClick: () -> Unit,
                   onCommentClick: () -> Unit,
                   onShareClick: () -> Unit
) {
    val isLikedByMe = post.likes.contains(currentUserId)
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            // NÚT TIM XỊN NÈ KKK
            InteractionButton(
                icon = if (isLikedByMe) Icons.Default.Favorite else Icons.Default.FavoriteBorder, // <-- Tim Đỏ/Trắng
                text = post.likes.size.toString(), // <-- Data xịn
                color = if (isLikedByMe) Color.Red else Color.Gray, // <-- Màu xịn
                onClick = onLikeClick // <-- Click xịn
            )
            Spacer(modifier = Modifier.width(24.dp))

            // NÚT COMMENT (Tạm thời)
            InteractionButton(
                icon = Icons.Default.ChatBubbleOutline,
                text = post.commentCount.toString(), // <-- Data xịn
                color = Color.Gray, // <-- Mặc định
                onClick = onCommentClick // <-- Click xịn
            )
        }

        // NÚT SHARE (Tạm thời)
        InteractionButton(
            icon = Icons.Default.Share,
            text = "Chia sẻ",
            color = Color.Gray,
            onClick = onShareClick // <-- Click xịn
        )
    }
}

/**
 * HÀM NÀY M GIỮ NGUYÊN
 */
@Composable
private fun InteractionButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    text: String,
    color: Color, // <-- THÊM CÁI NÀY
    onClick: () -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.clickable { onClick() }
    ) {
        Icon(imageVector = icon, contentDescription = null, tint = color, modifier = Modifier.size(24.dp)) // <-- Xài color
        Spacer(modifier = Modifier.width(6.dp))
        Text(text = text, color = color, style = MaterialTheme.typography.bodyMedium) // <-- Xài color
    }
}