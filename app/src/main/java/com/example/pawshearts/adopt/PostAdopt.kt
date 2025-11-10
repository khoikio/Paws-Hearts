package com.example.pawshearts.adopt



import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.example.pawshearts.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PostAdopt(
    post: Adopt,
    onEditClick: (Adopt) -> Unit
) {
    Card(
        onClick = { onEditClick(post) }, // ✅ đã sửa
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(8.dp)) {
            val imageRes = when (post.id) {
                1 -> R.drawable.cat1
                2 -> R.drawable.cat2
                3 -> R.drawable.cat3
                else -> R.drawable.cat1
            }

            val painter = if (post.imageUrl != null)
                rememberAsyncImagePainter(post.imageUrl)
            else
                painterResource(id = imageRes)

            Image(
                painter = painter,
                contentDescription = post.title,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .clip(RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(post.title, style = MaterialTheme.typography.titleMedium)
            Text(post.description, style = MaterialTheme.typography.bodyMedium)
        }
    }
}
