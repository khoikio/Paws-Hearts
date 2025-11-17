package com.example.pawshearts.adopt.components

import android.content.Intent
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import coil.compose.rememberAsyncImagePainter
import com.example.pawshearts.R
import com.example.pawshearts.adopt.AdoptViewModel
import com.example.pawshearts.auth.AuthViewModel
import com.example.pawshearts.navmodel.Routes

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdoptScreen(
    nav: NavHostController,
    adoptViewModel: AdoptViewModel,
    authViewModel: AuthViewModel,
) {
    Log.d("ADOPT_DEBUG", "Bắt đầu vẽ AdoptScreen Composable")

    val allAdoptPosts by adoptViewModel.allAdoptPosts.collectAsStateWithLifecycle()
    val userProfile by authViewModel.userProfile.collectAsStateWithLifecycle()
    val avatarUrl = userProfile?.profilePictureUrl
    val likedPostIds by adoptViewModel.likedPostIds.collectAsStateWithLifecycle()
    val currentUserId = userProfile?.userId ?: ""
    val context = LocalContext.current

    val OrangeColor = Color(0xFFE65100)

    LaunchedEffect(currentUserId) {
        if (currentUserId.isNotEmpty()) {
            adoptViewModel.fetchLikedPosts(currentUserId)
        }
    }

    Log.d("ADOPT_DEBUG", "AdoptScreen Composable đã lấy xong state, chuẩn bị vẽ Scaffold")

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        "Các Bé Cần Chủ Mới Nhận Nuôi",
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 24.sp,
                        color = OrangeColor
                    )
                },
                modifier = Modifier.height(100.dp),
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = OrangeColor
                )
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(MaterialTheme.colorScheme.background),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                CreatePostButton(
                    avatarUrl = avatarUrl,
                    onClick = {
                        nav.navigate(Routes.CREATE_ADOPT_POST_SCREEN)
                    }
                )
            }

            if (allAdoptPosts.isEmpty()) {
                item {
                    Text(
                        "Chưa có bé nào tìm chủ",
                        modifier = Modifier.padding(vertical = 24.dp).fillMaxWidth(),
                        textAlign = TextAlign.Center,
                        color = Color.Gray
                    )
                }
            } else {
                items(allAdoptPosts) { adoptPost ->
                    PostAdopt(
                        post = adoptPost,
                        onEditClick = {
                            nav.navigate("${Routes.PET_DETAIL_SCREEN}/${adoptPost.id}")
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
                                    "Bé ${postToShare.petName} đang tìm chủ! Chi tiết tại app."
                                )
                                type = "text/plain"
                            }
                            ContextCompat.startActivity(context, Intent.createChooser(shareIntent, "Chia sẻ"), null)
                        }
                    )
                }
            }
        }
    }
    Log.d("ADOPT_DEBUG", "Vẽ xong AdoptScreen Composable")
}

@Composable
fun CreatePostButton(
    avatarUrl: String?,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Image(
                painter = if (avatarUrl != null)
                    rememberAsyncImagePainter(avatarUrl)
                else
                    painterResource(id = R.drawable.avatardefault),
                contentDescription = "Avatar",
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .border(1.dp, Color.Gray, CircleShape),
                contentScale = ContentScale.Crop
            )
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(38.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .padding(horizontal = 16.dp),
                contentAlignment = Alignment.CenterStart
            ) {
                Text(
                    "Bạn muốn tìm chủ cho bé nào?",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}
