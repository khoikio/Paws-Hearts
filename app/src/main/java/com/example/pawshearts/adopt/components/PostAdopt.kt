package com.example.pawshearts.adopt.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
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
    post: Adopt, // Giả sử mày có data class AdoptPost
    onEditClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        // (1) Nền Card bây giờ sẽ nghe theo Theme
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        onClick = onEditClick
    ) {
        Column {
            // Phần hiển thị ảnh
            AsyncImage(
                model = post.imageUrl,
                contentDescription = "Ảnh thú cưng",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp),
                contentScale = ContentScale.Crop
            )

            // Phần thông tin bên dưới
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                // Tên thú cưng
                Text(
                    text = post.petName,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    // (2) Màu chữ chính trên Card
                    color = MaterialTheme.colorScheme.onSurface
                )
                // Các thông tin khác
                Text(
                    text = "Giống: ${post.petBreed} • Giới tính: ${post.petGender}",
                    style = MaterialTheme.typography.bodyMedium,
                    // (3) Màu chữ phụ trên Card
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "Tuổi: ${post.petAge} tháng • Cân nặng: ${post.petWeight} kg",
                    style = MaterialTheme.typography.bodyMedium,
                    // (4) Màu chữ phụ trên Card
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}