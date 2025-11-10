package com.example.pawshearts.screens

import android.app.Application
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.pawshearts.auth.AuthViewModel
import com.example.pawshearts.goPetDetail
import com.example.pawshearts.navmodel.Routes
import com.example.pawshearts.post.PostViewModel
import com.example.pawshearts.post.PostViewModelFactory
import com.example.pawshearts.components.PostCard // M NHỚ IMPORT CÁI NÀY NHA KKK

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyPostsScreen(
    nav: NavHostController,
    authViewModel: AuthViewModel,
    postViewModel: PostViewModel
) {
    // 1. Lấy thông tin người dùng hiện tại
    val userProfileData by authViewModel.userProfile.collectAsState()
    val currentUserId = userProfileData?.userId ?: ""

    // 2. Lấy danh sách bài đăng từ PostViewModel
    val myPosts by postViewModel.myPosts.collectAsState()

    // 3. Gọi hàm fetchMyPosts KHI có thông tin người dùng
    LaunchedEffect(currentUserId) {
        if (currentUserId.isNotEmpty()) {
            postViewModel.fetchMyPosts(currentUserId)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Bài đăng") },
                navigationIcon = {
                    IconButton(onClick = { nav.popBackStack() }) { // <-- NÚT BACK XỊN
                        Icon(Icons.Default.ArrowBack, contentDescription = "Quay lại")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFFFFF3E0), // Màu cam lợt :3
                    titleContentColor = Color.Black,
                    navigationIconContentColor = Color.Black
                )
            )
        },
        floatingActionButton = { // <-- THÊM NÚT NÀY VÔ KKK
            FloatingActionButton(
                onClick = {
                    nav.navigate(Routes.CREATE_POST_SCREEN) // <-- BẤM VÔ NHẢY QUA TRANG ĐĂNG BÀI
                },
                containerColor = Color(0xFFE65100), // Màu cam xịn
                contentColor = Color.White
            ) {
                Icon(Icons.Default.Add, contentDescription = "Đăng bài mới")
            }
        }
    ) { paddingValues ->

        // 4. Hiển thị danh sách bài đăng
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp), // Thêm tí đệm 2 bên
            contentPadding = PaddingValues(vertical = 12.dp), // Đệm trên dưới
            verticalArrangement = Arrangement.spacedBy(12.dp) // Cách nhau 12dp
        ) {
            if (myPosts.isEmpty()) {
                item {
                    Text(
                        "Bạn chưa có bài đăng :v",
                        modifier = Modifier.padding(vertical = 24.dp),
                        color = Color.Gray
                    )
                }
            } else {
                items(myPosts) { post ->

                    PostCard(
                        post = post,
                        currentUserId = currentUserId,
                        onClick = { nav.goPetDetail(post.id) },
                        onLikeClick = { postViewModel.toggleLike(post.id, currentUserId) },
                        onCommentClick = { nav.navigate(Routes.comment(post.id)) },
                        onShareClick = {}
                    )
                }
            }
        }
    }
}