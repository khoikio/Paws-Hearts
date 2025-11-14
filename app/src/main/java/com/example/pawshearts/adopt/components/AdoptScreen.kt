package com.example.pawshearts.adopt.components

// ... (Các imports đã đúng)
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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import coil.compose.rememberAsyncImagePainter
import com.example.pawshearts.R
import com.example.pawshearts.adopt.AdoptViewModel
import com.example.pawshearts.auth.AuthViewModel
import com.example.pawshearts.navmodel.Routes
import com.example.pawshearts.adopt.Adopt // Cần import Adopt vì PostAdopt nhận AdoptPostUI và Adopt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdoptScreen(
    nav: NavHostController,
    adoptViewModel: AdoptViewModel,
    authViewModel: AuthViewModel
) {
    val allAdoptPostsUI by adoptViewModel.allAdoptPostsUI.collectAsState(initial = emptyList())
    val userProfile by authViewModel.userProfile.collectAsState()
    val avatarUrl = userProfile?.profilePictureUrl

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Tìm chủ (Tất cả)") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFFFFF3E0),
                    titleContentColor = Color.Black
                )
            )
        }
    ) { paddingValues ->

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Color(0xFFF5F5F5)),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            // NÚT TẠO BÀI ĐĂNG
            item {
                CreatePostButton(
                    avatarUrl = avatarUrl,
                    onClick = {
                        nav.navigate(Routes.CREATE_ADOPT_POST_SCREEN)
                    }
                )
            }

            // LIST BÀI ĐĂNG
            if (allAdoptPostsUI.isEmpty()) {
                item {
                    Text(
                        text = "Chưa có bé nào tìm chủ KKK :v",
                        modifier = Modifier
                            .padding(vertical = 24.dp)
                            .fillMaxWidth(),
                        color = Color.Gray,
                        // ✅ SỬA LỖI: Dùng TextAlign.Center
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                }
            } else {
                items(allAdoptPostsUI) { adoptPostUI ->
                    PostAdopt(
                        postUI = adoptPostUI,
                        adoptViewModel = adoptViewModel,
                        navController = nav,
                        onEditClick = { /* Chi tiết/Chỉnh sửa */ },
                        post = adoptPostUI.adopt // Truyền Adopt thô
                    )
                }
            }
        }
    }
}

// === CÁI NÚT TẠO BÀI ĐĂNG ===
@Composable
fun CreatePostButton(
    avatarUrl: String?,
    onClick: () -> Unit
) {
    // ... (Code đã đúng cho nút tạo bài đăng)
    Card( /* ... */ ) { /* ... */ }
}