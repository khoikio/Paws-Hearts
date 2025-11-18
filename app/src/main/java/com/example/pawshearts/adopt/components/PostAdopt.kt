package com.example.pawshearts.adopt.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Comment
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder // <== ICON TIM TRỐNG
import androidx.compose.material.icons.filled.Share // <== ICON CHIA SẺ
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.example.pawshearts.R
import com.example.pawshearts.adopt.Adopt
import androidx.compose.ui.text.style.TextOverflow

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PostAdopt(
    post: Adopt,
    onEditClick: (Adopt) -> Unit,
    onCommentClick: (String) -> Unit,
    // === THAM SỐ ĐÃ THÊM ===
    isLiked: Boolean,
    onLikeClick: (String) -> Unit,
    onShareClick: (Adopt) -> Unit
) {
    val OrangeColor = Color(0xFFE65100)

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        onClick = { onEditClick(post) } // Mở chi tiết/chỉnh sửa
    ) {
        Column(modifier = Modifier.padding(10.dp)) {

            // --- HEADER VÀ THÔNG TIN PET ---
            val painter = if (post.imageUrl != null && post.imageUrl.isNotEmpty())
                rememberAsyncImagePainter(post.imageUrl)
            else
                painterResource(id = R.drawable.avatardefault)

            Image(
                painter = painter,
                contentDescription = post.petName,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .clip(RoundedCornerShape(8.dp)), // Làm tròn ảnh chút
                contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                post.petName,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                "Giống: ${post.petBreed} - ${post.petAge} tháng",
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                post.description,
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

        }
    }
}