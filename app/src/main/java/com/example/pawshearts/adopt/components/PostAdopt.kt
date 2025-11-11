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
import coil.compose.rememberAsyncImagePainter
import com.example.pawshearts.R
import com.example.pawshearts.adopt.Adopt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PostAdopt(
    post: Adopt,
    onEditClick: (Adopt) -> Unit
) {
    Card(
        onClick = { onEditClick(post) },
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(modifier = Modifier.padding(10.dp)) {

            // T VỚI M XÀI 'imageUrl' XỊN M UP LÊN Á KKK
            val painter = if (post.imageUrl != null)
                rememberAsyncImagePainter(post.imageUrl)
            else
                painterResource(id = R.drawable.avatardefault)

            Image(
                painter = painter,
                contentDescription = post.petName,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .clip(RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.height(10.dp))

            // SỬA MẤY CÁI FIELD NÀY CHO NÓ XỊN KKK
            Text(
                post.petName,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                "Giống: ${post.petBreed} - ${post.petAge} tháng", // <-- M HIỆN VẦY XỊN VCL KKK
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                post.description, // <-- 'description' XỊN KKK
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 2 // M cho nó 2 dòng thôi
            )
        }
    }
}