package com.example.pawshearts.post

import android.app.Application
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Message
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.example.pawshearts.auth.AuthViewModel
import com.example.pawshearts.auth.AuthViewModelFactory
import com.example.pawshearts.navmodel.Routes
import com.example.pawshearts.navmodel.goPetDetail

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(nav: NavHostController) {
    val context = LocalContext.current.applicationContext as Application
    val postViewModel: PostViewModel = viewModel(factory = PostViewModelFactory(context))
    val authViewModel: AuthViewModel = viewModel(factory = AuthViewModelFactory(context))

    val allPosts by postViewModel.allPosts.collectAsStateWithLifecycle()
    val currentUserId = authViewModel.currentUser?.uid ?: ""
    val currentUserProfile by authViewModel.userProfile.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        postViewModel.fetchAllPosts()
    }

    // (1) Nền chính bây giờ sẽ nghe theo Theme
    Column(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
        // === Top App Bar (Phần trên cùng) ===
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 24.dp, bottom = 8.dp, start = 12.dp, end = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            IconButton(onClick = { /*TODO*/ }) {
                // Icon bây giờ sẽ dùng màu primary của Theme
                Icon(Icons.Filled.Message, contentDescription = "Tin nhắn", tint = MaterialTheme.colorScheme.primary)
            }
            Text(
                text = "Paws & Hearts",
                color = MaterialTheme.colorScheme.primary,
                style = MaterialTheme.typography.headlineMedium
            )
            IconButton(onClick = {nav.navigate(Routes.NOTIFICATION_SCREEN)}) {
                Icon(Icons.Filled.Notifications, contentDescription = "Thông báo", tint = MaterialTheme.colorScheme.primary)
            }
        }

        // (2) Thanh tìm kiếm bây giờ sẽ nghe theo Theme
        TextField(
            value = "",
            onValueChange = {},
            placeholder = { Text("Search") },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Tìm kiếm") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            shape = MaterialTheme.shapes.extraLarge, // Bo tròn hơn cho đẹp
            colors = TextFieldDefaults.colors(
                // Lấy màu nền surfaceVariant từ Theme (màu nền phụ)
                unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                unfocusedIndicatorColor = Color.Transparent,
                focusedIndicatorColor = Color.Transparent
            )
        )

        // (3) Thanh "Bạn đang nghĩ gì?" bây giờ sẽ nghe theo Theme
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp)
                .clickable { nav.navigate(Routes.CREATE_POST_SCREEN) },
            shape = MaterialTheme.shapes.large,
            // Lấy màu surface từ Theme (màu của các bề mặt)
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                AsyncImage(
                    model = currentUserProfile?.profilePictureUrl ?: "https://i.pravatar.cc/150",
                    contentDescription = "User Avatar",
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .border(1.dp, Color.LightGray, CircleShape),
                    contentScale = ContentScale.Crop
                )
                Text(
                    text = "Bạn đang nghĩ gì?",
                    style = MaterialTheme.typography.bodyLarge,
                    // Lấy màu chữ phụ từ theme
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Start
                )
            }
        }

        // Danh sách các bài đăng
        if (allPosts.isEmpty()) {
            Box(Modifier.fillMaxSize().padding(top = 24.dp), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            LazyColumn(
                contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(allPosts) { post ->
                    // PostCard cũng sẽ tự động đổi màu nếu bên trong nó dùng màu từ Theme
                    PostCard(
                        post = post,
                        currentUserId = currentUserId,
                        onClick = { nav.goPetDetail(post.id) },
                        onLikeClick = {
                            if (currentUserId.isNotBlank()) {
                                postViewModel.toggleLike(post.id, currentUserId)
                            }
                        },
                        onCommentClick = { nav.navigate(Routes.comment(post.id)) },
                        onShareClick = { /*TODO*/ }
                    )
                }
            }
        }
    }
}
