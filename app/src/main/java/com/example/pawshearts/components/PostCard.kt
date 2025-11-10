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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.pawshearts.R
import com.example.pawshearts.data.model.Post
import com.google.firebase.Timestamp
import java.util.concurrent.TimeUnit // <-- T XÀI CÁI NÀY

// 1. SỬA HÀM CHÍNH (THÊM 4 THAM SỐ T DẶN M)
@Composable
fun PostCard(
    post: Post,
    currentUserId: String, // <-- THÊM (Để T biết M like chưa)
    onClick: () -> Unit,
    onLikeClick: () -> Unit, // <-- THÊM
    onCommentClick: () -> Unit, // <-- THÊM (Cho M bấm)
    onShareClick: () -> Unit // <-- THÊM (Cho M bấm)
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

            // 2. SỬA UserInfoRow (TRUYỀN 'post' VÔ)
            UserInfoRow(post = post)

            Spacer(modifier = Modifier.height(12.dp))

            // LỖI 1: SỬA title THÀNH petName (TỪ file Post.kt)
            Text(
                text = post.petName,
                style = MaterialTheme.typography.titleMedium
            )

            // LỖI 2: SỬA CÁI "KHƠI KHƠI" (M XÓA CÁI CŨ ĐI)
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

            // LỖI 3: SỬA imageURL.firstOrNull() THÀNH imageUrl (TỪ file Post.kt)
            AsyncImage(
                model = post.imageUrl, // <-- SỬA
                contentDescription = post.petName, // <-- SỬA
                modifier = Modifier
                    .fillMaxWidth()
                    .height(260.dp)
                    .clip(MaterialTheme.shapes.medium),
                contentScale = ContentScale.Crop,
                placeholder = painterResource(id = R.drawable.avatardefault) // Thêm cái này
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Địa điểm (T thêm check null)
            Text(
                text = "📍 ${post.location ?: "Không rõ"}",
                color = Color.Gray,
                style = MaterialTheme.typography.bodySmall
            )

            Spacer(modifier = Modifier.height(12.dp))

            // 3. SỬA InteractionRow (TRUYỀN HẾT VÔ)
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

// 2. SỬA HÀM UserInfoRow (NHẬN 'post' CHO GỌN)
@Composable
fun UserInfoRow(post: Post) { // <-- Sửa tham số
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            AsyncImage(
                model = post.userAvatarUrl ?: R.drawable.avatardefault, // Dùng data xịn
                contentDescription = "User Avatar",
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(Color.LightGray)
            )

            Spacer(modifier = Modifier.width(8.dp))

            Column {
                Text(text = post.username ?: "Người dùng PawsHearts", style = MaterialTheme.typography.titleSmall) // Dùng data xịn
                Text(text = formatTimestamp(post.createdAt), color = Color.Gray, style = MaterialTheme.typography.bodySmall) // Dùng data xịn
            }
        }

        IconButton(onClick = { /* More options */ }) {
            Icon(Icons.Default.MoreHoriz, contentDescription = "More options", tint = Color.Gray)
        }
    }
}

// 3. SỬA HÀM InteractionRow (NHẬN HẾT DATA/CLICK)
@Composable
fun InteractionRow(
    post: Post,
    currentUserId: String,
    onLikeClick: () -> Unit,
    onCommentClick: () -> Unit,
    onShareClick: () -> Unit
) {
    // Check xem M (currentUserId) có trong list likes ko
    val isLikedByMe = post.likes.contains(currentUserId)

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            // NÚT TIM XỊN NÈ KKK (HẾT ẢO)
            InteractionButton(
                icon = if (isLikedByMe) Icons.Default.Favorite else Icons.Default.FavoriteBorder, // <-- Tim Đỏ/Trắng
                text = post.likes.size.toString(), // <-- Data xịn (Đếm list)
                color = if (isLikedByMe) Color.Red else Color.Gray, // <-- Màu xịn
                onClick = onLikeClick // <-- Click xịn
            )
            Spacer(modifier = Modifier.width(24.dp))

            // NÚT COMMENT XỊN (HẾT ẢO)
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

// 4. SỬA HÀM InteractionButton (THÊM 'color')
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

// 5. HÀM TIMESTAMP (M PHẢI 'public' NÓ LÊN)
@Composable
fun formatTimestamp(timestamp: Timestamp): String { // <-- T XÓA 'private' ĐI
    val now = System.currentTimeMillis()
    val diff = now - timestamp.toDate().time // Lấy (ms)

    return when {
        diff < TimeUnit.MINUTES.toMillis(1) -> "Vừa xong"
        diff < TimeUnit.HOURS.toMillis(1) -> "${TimeUnit.MILLISECONDS.toMinutes(diff)} phút trước"
        diff < TimeUnit.DAYS.toMillis(1) -> "${TimeUnit.MILLISECONDS.toHours(diff)} giờ trước"
        else -> "${TimeUnit.MILLISECONDS.toDays(diff)} ngày trước"
    }
}