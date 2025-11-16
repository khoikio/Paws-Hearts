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
import coil.compose.AsyncImage
import coil.compose.rememberAsyncImagePainter
import com.example.pawshearts.R
import com.example.pawshearts.adopt.Adopt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PostAdopt(
    post: Adopt,
    onEditClick: (Adopt) -> Unit,
    onCommentClick: (String) -> Unit,
    // === THÊM THAM SỐ MỚI NÈ M ƠI KKK ===
    isLiked: Boolean, // Trạng thái đã tim hay chưa
    onLikeClick: (String) -> Unit, // Hành động bấm nút Tim
    onShareClick: (Adopt) -> Unit // Hành động bấm nút Chia sẻ
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        // (1) Nền Card bây giờ sẽ nghe theo Theme
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        onClick = onEditClick
    ) {
        Column(modifier = Modifier.padding(10.dp)) {

            // Hình ảnh và thông tin Pet (Giữ nguyên)
            val painter = if (post.imageUrl != null)
                rememberAsyncImagePainter(post.imageUrl)
            else
                painterResource(id = R.drawable.avatardefault)

            Image(
                painter = painter,
                contentDescription = post.petName,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp),
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
                maxLines = 2
            )

            // --- PHẦN ACTIONS (TIM, CMT, CHIA SẺ) ---
            Spacer(modifier = Modifier.height(8.dp))
            Divider(color = Color(0xFFEEEEEE), thickness = 1.dp)
            Spacer(modifier = Modifier.height(4.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround // Chia đều 3 nút
            ) {
                // 1. NÚT TIM (LIKE) - ĐÃ SỬA
                TextButton(
                    onClick = { onLikeClick(post.id) },
                    colors = ButtonDefaults.textButtonColors(contentColor = Color.Gray),
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(
                        imageVector = if (isLiked) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                        contentDescription = "Tim",
                        tint = if (isLiked) Color(0xFFE65100) else Color.Gray, // Tim cam khi đã like
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(Modifier.width(4.dp))

                    // HIỂN THỊ SỐ LƯỢNG TIM THAY VÌ CHỮ "Tim"
                    val likeCount = post.likeCount // <== Lấy số đếm từ đối tượng Adopt

                    Text(
                        // Nếu số lượng > 0 thì hiện số, ngược lại thì hiện chữ "Tim"
                        text = if (likeCount > 0) likeCount.toString() else "Tim",
                        color = if (isLiked || likeCount > 0) Color(0xFFE65100) else Color.Gray, // Màu cam nếu đã Like hoặc có Tim
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold)
                    )
                }

                // 2. NÚT BÌNH LUẬN (COMMENT)
                TextButton(
                    onClick = { onCommentClick(post.id) },
                    colors = ButtonDefaults.textButtonColors(contentColor = Color.Gray),
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(
                        Icons.Default.Comment,
                        contentDescription = "Bình luận",
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(Modifier.width(4.dp))
                    Text("Bình luận")
                }

                // 3. NÚT CHIA SẺ (SHARE)
                TextButton(
                    onClick = { onShareClick(post) }, // Truyền cả object post nếu cần
                    colors = ButtonDefaults.textButtonColors(contentColor = Color.Gray),
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(
                        Icons.Default.Share,
                        contentDescription = "Chia sẻ",
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(Modifier.width(4.dp))
                    Text("Chia sẻ")
                }
            }
            // ----------------------------------------
        }
    }
}