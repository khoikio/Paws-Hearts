package com.example.pawshearts.post

import android.app.Application
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Message
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle // <-- M PHẢI CÓ IMPORT NÀY
import androidx.lifecycle.viewmodel.compose.viewModel // <-- M PHẢI CÓ IMPORT NÀY
import androidx.navigation.NavHostController
import com.example.pawshearts.auth.AuthViewModel
import com.example.pawshearts.auth.AuthViewModelFactory
import com.example.pawshearts.navmodel.goPetDetail // <-- M PHẢI CÓ IMPORT NÀY
import com.example.pawshearts.navmodel.Routes
// T XÓA PetPost VÀ PetRepository ĐI, M XÀI HÀNG XỊN KKK
// import com.example.pawshearts.data.PetPost
// import com.example.pawshearts.data.PetRepository

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(nav: NavHostController) {
    val context = LocalContext.current.applicationContext as Application
    val postViewModel: PostViewModel = viewModel(factory = PostViewModelFactory(context)) // <-- HẾT LỖI
    val authViewModel: AuthViewModel = viewModel(factory = AuthViewModelFactory(context))
    val allPosts by postViewModel.allPosts.collectAsStateWithLifecycle()
    val currentUserId = authViewModel.currentUser?.uid ?: ""
    LaunchedEffect(Unit) {
        postViewModel.fetchAllPosts()
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F5))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 24.dp, bottom = 8.dp, start = 12.dp, end = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            IconButton(onClick = { }) {
                Icon(Icons.Filled.Message, contentDescription = null, tint = Color(0xFFEA5600))
            }
            Text(
                text = "Paws & Hearts",
                color = Color(0xFFEA5600),
                style = MaterialTheme.typography.headlineMedium
            )
            IconButton(onClick = { }) {
                Icon(Icons.Filled.Notifications, contentDescription = null, tint = Color(0xFFEA5600))
            }
        }
        TextField(
            value = "",
            onValueChange = {},
            placeholder = { Text("Search") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .background(Color(0xFFFDEEE2), shape = MaterialTheme.shapes.medium),
            colors = TextFieldDefaults.colors(
                unfocusedContainerColor = Color(0xFFFDEEE2),
                focusedContainerColor = Color(0xFFFDEEE2),
                unfocusedIndicatorColor = Color.Transparent,
                focusedIndicatorColor = Color.Transparent
            )
        )
        if (allPosts.isEmpty()) {
            Box(
                Modifier
                    .fillMaxSize()
                    .padding(top = 24.dp),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            LazyColumn(
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.padding(top = 8.dp)
            ) {
                items(allPosts) { post ->
                    PostCard(
                        post = post,
                        currentUserId = currentUserId, // <-- TRUYỀN ID
                        onClick = { nav.goPetDetail(post.id) },
                        onLikeClick = {
                            if (currentUserId.isNotBlank()) { // Check  đăng nhập chưa
                                postViewModel.toggleLike(post.id, currentUserId)
                            }
                        },
                        onCommentClick = {
                            nav.navigate(Routes.comment(post.id))
                        },
                        onShareClick = {
                            // T VỚI M TÍNH SAU KKK :v
                        }
                    )
                }
            }
        }
    }
}