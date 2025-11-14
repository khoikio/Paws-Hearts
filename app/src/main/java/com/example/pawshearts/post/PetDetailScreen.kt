package com.example.pawshearts.post // M check package M

import android.app.Application
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.pawshearts.R
import com.example.pawshearts.auth.AuthViewModel
import com.example.pawshearts.auth.AuthViewModelFactory

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PetDetailScreen(
    postId: String, // <-- AppNav nó truyền ID bài post vô đây
    onBack: () -> Unit // <-- Để M bấm nút Back
) {
    // 1. LẤY VIEWMODEL (Y CHANG M SỬA 4 FILE HÔM QUA)
    val context = LocalContext.current.applicationContext as Application
    val postViewModel: PostViewModel = viewModel(factory = PostViewModelFactory(context))
    val authViewModel: AuthViewModel = viewModel(factory = AuthViewModelFactory(context))

    // 2. LẤY DATA (CÁI BÀI POST M CHỌN + ID CỦA M)
    val post by postViewModel.selectedPost.collectAsStateWithLifecycle()
    val currentUserId = authViewModel.currentUser?.uid ?: ""

    // 3. BẮT ĐẦU TẢI CHI TIẾT BÀI (M GỌI HÀM BƯỚC 3)
    LaunchedEffect(postId) {
        postViewModel.fetchPostDetails(postId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(post?.petName ?: "Đang tải...") }, // Tí nó F5 tên xịn
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { innerPadding ->

        // CHECK XEM TẢI XONG CHƯA
        val currentPost = post // T gán nó ra biến
        if (currentPost == null) {
            // ĐANG TẢI (HOẶC LỖI)
            Box(Modifier.fillMaxSize().padding(innerPadding), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            // TẢI XÔNG -> HIỆN THỊ KKK
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .verticalScroll(rememberScrollState()) // T CHO CUỘN
            ) {
                // T XÀI LẠI HÀNG CỦA M (TÁCH RA TỪ PostCard)
                // 1. ẢNH BỰ VCL
                AsyncImage(
                    model = currentPost.imageUrl,
                    contentDescription = currentPost.petName,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(350.dp), // Ảnh chi tiết bự hơn
                    contentScale = ContentScale.Crop,
                    placeholder = painterResource(id = R.drawable.avatardefault)
                )

                // 2. TÊN, GIỐNG, TUỔI... (T CODE TRONG PADDING)
                Column(Modifier.padding(16.dp)) {
                    // TÊN THÚ CƯNG
                    Text(
                        text = currentPost.petName,
                        style = MaterialTheme.typography.headlineMedium
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    // THÔNG TIN CHI TIẾT (T XÀI LẠI CODE M CHỬI)
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

                    // INFO NGƯỜI ĐĂNG (T XÀI LẠI UserInfoRow)
                    UserInfoRow(post = currentPost)

                    Spacer(modifier = Modifier.height(16.dp))

                    // MÔ TẢ (CHO M ĐỌC FULL)
                    Text(
                        text = currentPost.description,
                        style = MaterialTheme.typography.bodyLarge
                    )

                    Divider(Modifier.padding(vertical = 16.dp))

                    // NÚT TIM/CMT/SHARE (T XÀI LẠI InteractionRow)
                    InteractionRow(
                        post = currentPost,
                        currentUserId = currentUserId,
                        onLikeClick = {
                            if (currentUserId.isNotBlank()) {
                                postViewModel.toggleLike(currentPost.id, currentUserId)
                            }
                        },
                        onCommentClick = {
                            // TẠM THỜI M ĐỂ RỖNG (M PHẢI SỬA AppNav MỚI CHUYỂN ĐC KKK)
                        },
                        onShareClick = {}
                    )

                    // TÍ M MUỐN M NHÉT LIST CMT VÔ ĐÂY KKK
                }
            }
        }
    }
}