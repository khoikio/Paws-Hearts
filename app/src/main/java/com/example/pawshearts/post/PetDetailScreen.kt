package com.example.pawshearts.post

import android.app.Application
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Comment
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.pawshearts.R
import com.example.pawshearts.auth.AuthViewModel
import com.example.pawshearts.auth.AuthViewModelFactory
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PetDetailScreen(
    id: String,
    onBack: () -> Unit
) {
    val context = LocalContext.current.applicationContext as Application
    val postViewModel: PostViewModel = viewModel(factory = PostViewModelFactory(context))
    val authViewModel: AuthViewModel = viewModel(factory = AuthViewModelFactory(context))

    val post by postViewModel.selectedPost.collectAsStateWithLifecycle()
    val currentUserId = authViewModel.currentUser?.uid ?: ""

    LaunchedEffect(id) {
        postViewModel.fetchPostDetails(id)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(post?.petName ?: "Đang tải...") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { innerPadding ->
        val currentPost = post
        if (currentPost == null) {
            Box(Modifier.fillMaxSize().padding(innerPadding), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .verticalScroll(rememberScrollState())
            ) {
                AsyncImage(
                    model = currentPost.imageUrl,
                    contentDescription = currentPost.petName,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(350.dp),
                    contentScale = ContentScale.Crop,
                    placeholder = painterResource(id = R.drawable.avatardefault)
                )

                Column(Modifier.padding(16.dp)) {
                    Text(
                        text = currentPost.petName,
                        style = MaterialTheme.typography.headlineMedium
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Giống: ${currentPost.petBreed ?: "Chưa rõ"} • Giới tính: ${currentPost.petGender ?: "Chưa rõ"}",
                        style = MaterialTheme.typography.bodyLarge,
                        color = Color.Gray
                    )
                    Text(
                        text = "Tuổi: ${currentPost.petAge ?: "?"} tháng • Cân nặng: ${currentPost.weightKg ?: "?"} kg",
                        style = MaterialTheme.typography.bodyLarge,
                        color = Color.Gray
                    )
                    Text(
                        text = "📍 ${currentPost.location ?: "Không rõ"}",
                        style = MaterialTheme.typography.bodyLarge,
                        color = Color.Gray
                    )

                    Divider(Modifier.padding(vertical = 16.dp))

                    UserInfoRow(post = currentPost) // <-- GỌI HÀM ĐÃ VIẾT LẠI

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = currentPost.description,
                        style = MaterialTheme.typography.bodyLarge
                    )

                    Divider(Modifier.padding(vertical = 16.dp))

                    InteractionRow( // <-- GỌI HÀM ĐÃ VIẾT LẠI
                        post = currentPost,
                        currentUserId = currentUserId,
                        onLikeClick = {
                            if (currentUserId.isNotBlank()) {
                                postViewModel.toggleLike(currentPost.id, currentUserId)
                            }
                        },
                        onCommentClick = { /* TODO */ },
                        onShareClick = {}
                    )
                }
            }
        }
    }
}

// VIẾT LẠI HÀM UserInfoRow
@Composable
private fun UserInfoRow(post: Post) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AsyncImage(
            model = post.userAvatarUrl,
            contentDescription = "User Avatar",
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape),
            contentScale = ContentScale.Crop
        )
        Spacer(modifier = Modifier.width(12.dp))
        Column {
            Text(
                text = post.userName,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = post.createdAt?.let {
                    val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
                    sdf.format(it.toDate())
                } ?: "Vừa xong",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

// VIẾT LẠI HÀM InteractionRow (tương tự PostActions)
@Composable
private fun InteractionRow(
    post: Post,
    currentUserId: String,
    onLikeClick: () -> Unit,
    onCommentClick: () -> Unit,
    onShareClick: () -> Unit
) {
    Column {
        // --- FOOTER (Like, Comment counts) ---
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "${post.likes.size} lượt thích",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = "${post.commentCount} bình luận",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        
        Divider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            val isLiked = post.likes.contains(currentUserId)
            TextButton(onClick = onLikeClick, modifier = Modifier.weight(1f)) {
                Icon(
                    imageVector = if (isLiked) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                    contentDescription = "Like",
                    tint = if (isLiked) Color.Red else MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text("Thích", color = if (isLiked) Color.Red else MaterialTheme.colorScheme.onSurfaceVariant)
            }
            TextButton(onClick = onCommentClick, modifier = Modifier.weight(1f)) {
                Icon(
                    imageVector = Icons.Default.Comment,
                    contentDescription = "Comment",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text("Bình luận", color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            TextButton(onClick = onShareClick, modifier = Modifier.weight(1f)) {
                Icon(
                    imageVector = Icons.Default.Share,
                    contentDescription = "Share",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text("Chia sẻ", color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}
