package com.example.pawshearts.adopt

import androidx.compose.foundation.background
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
import androidx.core.content.ContextCompat
import androidx.navigation.NavHostController
import com.example.pawshearts.adopt.components.PostAdopt
import com.example.pawshearts.auth.AuthViewModel
import com.example.pawshearts.navmodel.Routes
import android.content.Intent

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyAdoptPostsScreen(
    nav: NavHostController,
    adoptViewModel: AdoptViewModel,
    authViewModel: AuthViewModel
) {

    val userProfileData by authViewModel.userProfile.collectAsState()
    val currentUserId = userProfileData?.userId ?: ""
    val myAdoptPosts by adoptViewModel.myAdoptPosts.collectAsState()

    val likedPostIds by adoptViewModel.likedPostIds.collectAsState()

    val context = LocalContext.current

    LaunchedEffect(currentUserId) {
        if (currentUserId.isNotEmpty()) {
            adoptViewModel.fetchMyAdoptPosts(currentUserId)
            adoptViewModel.fetchLikedPosts(currentUserId)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Bài nhận nuôi của bạn") },
                navigationIcon = {
                    IconButton(onClick = { nav.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Quay lại")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant,
                    titleContentColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    navigationIconContentColor = MaterialTheme.colorScheme.onSurfaceVariant
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    nav.navigate(Routes.CREATE_ADOPT_POST_SCREEN)
                },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ) {
                Icon(Icons.Default.Add, contentDescription = "Đăng nhận nuôi")
            }
        }
    ) { paddingValues ->

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(MaterialTheme.colorScheme.background)
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            if (myAdoptPosts.isEmpty()) {
                item {
                    Text(
                        "Bạn chưa đăng tìm chủ cho bé nào",
                        modifier = Modifier.padding(vertical = 24.dp),
                        color = Color.Gray
                    )
                }
            } else {
                items(myAdoptPosts) { adoptPost ->
                    PostAdopt(
                        post = adoptPost,
                        nav = nav, // <-- THÊM DÒNG NÀY VÀO
                        onEditClick = {
                            nav.navigate("${Routes.PET_DETAIL}/${adoptPost.id}")
                        },
                        onCommentClick = { postId ->
                            nav.navigate("${Routes.ADOPT_COMMENT_SCREEN}/${postId}")
                        },
                        isLiked = likedPostIds.contains(adoptPost.id),
                        onLikeClick = { postId ->
                            adoptViewModel.toggleLike(postId)
                        },
                        onShareClick = { postToShare ->
                            val shareIntent = Intent().apply {
                                action = Intent.ACTION_SEND
                                putExtra(Intent.EXTRA_TEXT,
                                    "Bé ${postToShare.petName} đang tìm chủ! Giống: ${postToShare.petBreed}. Chi tiết tại app"
                                )
                                type = "text/plain"
                            }
                            ContextCompat.startActivity(context, Intent.createChooser(shareIntent, "Chia sẻ bài viết này"), null)
                        }
                    )
                }
            }
        }
    }
}
