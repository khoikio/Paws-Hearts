package com.example.pawshearts.adopt.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Comment
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Share
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
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import coil.compose.rememberAsyncImagePainter
import com.example.pawshearts.R
import com.example.pawshearts.adopt.Adopt
import com.example.pawshearts.navmodel.Routes
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun PostAdopt(
    post: Adopt,
    nav: NavHostController,
    isLiked: Boolean,
    onEditClick: () -> Unit,
    onCommentClick: (String) -> Unit,
    onLikeClick: (String) -> Unit,
    onShareClick: (Adopt) -> Unit
) {
    val OrangeColor = Color(0xFFE65100)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onEditClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painter = if (post.userAvatarUrl != null) rememberAsyncImagePainter(post.userAvatarUrl) else painterResource(id = R.drawable.avatardefault),
                    contentDescription = "Avatar",
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .border(1.dp, Color.Gray, CircleShape)
                        .clickable {
                            if (post.userId.isNotEmpty()) {
                                nav.navigate(Routes.userProfile(post.userId))
                            }
                        },
                    contentScale = ContentScale.Crop
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = post.userName,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = post.createdAt?.let {
                            val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
                            sdf.format(Date(it.seconds * 1000))
                        } ?: "Vừa xong",
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                }
            }

            if (post.imageUrl != null) {
                Image(
                    painter = rememberAsyncImagePainter(post.imageUrl),
                    contentDescription = "Pet Image",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(250.dp),
                    contentScale = ContentScale.Crop
                )
            }

            Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)) {
                Text(
                    text = post.petName,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 22.sp,
                    color = OrangeColor
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = post.description, maxLines = 2, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }

            Divider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { onLikeClick(post.id) }) {
                    Icon(
                        imageVector = if (isLiked) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                        contentDescription = "Like",
                        tint = if (isLiked) Color.Red else Color.Gray
                    )
                }
                IconButton(onClick = { onCommentClick(post.id) }) {
                    Icon(
                        imageVector = Icons.Default.Comment,
                        contentDescription = "Comment",
                        tint = Color.Gray
                    )
                }
                IconButton(onClick = { onShareClick(post) }) {
                    Icon(
                        imageVector = Icons.Default.Share,
                        contentDescription = "Share",
                        tint = Color.Gray
                    )
                }
            }
        }
    }
}
