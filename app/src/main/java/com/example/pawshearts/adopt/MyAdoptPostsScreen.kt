//package com.example.pawshearts.adopt
//
//import androidx.compose.foundation.background
//import androidx.compose.foundation.layout.Arrangement
//import androidx.compose.foundation.layout.PaddingValues
//import androidx.compose.foundation.layout.fillMaxSize
//import androidx.compose.foundation.layout.padding
//import androidx.compose.foundation.lazy.LazyColumn
//import androidx.compose.foundation.lazy.items
//import androidx.compose.material.icons.Icons
//import androidx.compose.material.icons.filled.Add
//import androidx.compose.material.icons.filled.ArrowBack
//import androidx.compose.material3.ExperimentalMaterial3Api
//import androidx.compose.material3.FloatingActionButton
//import androidx.compose.material3.Icon
//import androidx.compose.material3.IconButton
//import androidx.compose.material3.MaterialTheme
//import androidx.compose.material3.Scaffold
//import androidx.compose.material3.Text
//import androidx.compose.material3.TopAppBar
//import androidx.compose.material3.TopAppBarDefaults
//import androidx.compose.runtime.Composable
//import androidx.compose.runtime.LaunchedEffect
//import androidx.compose.runtime.collectAsState
//import androidx.compose.runtime.getValue
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.graphics.Color
//import androidx.compose.ui.unit.dp
//import androidx.navigation.NavHostController
//import com.example.pawshearts.adopt.components.PostAdopt
//import com.example.pawshearts.auth.AuthViewModel
//import com.example.pawshearts.navmodel.Routes
//
//@OptIn(ExperimentalMaterial3Api::class)
//@Composable
//fun MyAdoptPostsScreen(
//    nav: NavHostController,
//    adoptViewModel: AdoptViewModel,
//    authViewModel: AuthViewModel,
//    targetUserId: String? = null // <- BỔ SUNG THAM SỐ NÀY ĐỂ NHẬN ID TỪ PROFILE KHÁC
//) {
//    // 1. XÁC ĐỊNH NGƯỜI DÙNG VÀ ID CẦN FETCH
//    // Nếu targetUserId có giá trị (tức là xem hồ sơ người khác), dùng nó.
//    // Nếu không, sử dụng ID của người dùng hiện tại.
//    val isMyPosts = targetUserId.isNullOrBlank()
//    val userProfileData by authViewModel.userProfile.collectAsState()
//
//    // Sử dụng ID từ targetUserId nếu có, nếu không, dùng ID của người dùng hiện tại (đã đăng nhập)
//    val userIdToFetch = if (isMyPosts) {
//        userProfileData?.userId ?: ""
//    } else {
//        targetUserId ?: ""
//    }
//
//    // 2. FETCH BÀI ĐĂNG
//    val myAdoptPosts by adoptViewModel.myAdoptPosts.collectAsState()
//
//    LaunchedEffect(userIdToFetch) {
//        if (userIdToFetch.isNotEmpty()) {
//            adoptViewModel.fetchMyAdoptPosts(userIdToFetch)
//        }
//    }
//
//    Scaffold(
//        topBar = {
//            TopAppBar(
//                title = {
//                    // CẬP NHẬT TIÊU ĐỀ
//                    Text(if (isMyPosts) "Bài nhận nuôi của bạn" else "Bài nhận nuôi")
//                },
//                navigationIcon = {
//                    IconButton(onClick = { nav.popBackStack() }) {
//                        Icon(Icons.Default.ArrowBack, contentDescription = "Quay lại")
//                    }
//                },
//                colors = TopAppBarDefaults.topAppBarColors(
//                    containerColor = MaterialTheme.colorScheme.surfaceVariant,
//                    titleContentColor = MaterialTheme.colorScheme.onSurfaceVariant,
//                    navigationIconContentColor = MaterialTheme.colorScheme.onSurfaceVariant
//                )
//            )
//        },
//        // CHỈ HIỂN THỊ NÚT THÊM NẾU LÀ BÀI ĐĂNG CỦA MÌNH
//        floatingActionButton = {
//            if (isMyPosts) {
//                FloatingActionButton(
//                    onClick = {
//                        nav.navigate(Routes.CREATE_ADOPT_POST_SCREEN)
//                    },
//                    containerColor = MaterialTheme.colorScheme.primary,
//                    contentColor = MaterialTheme.colorScheme.onPrimary
//                ) {
//                    Icon(Icons.Default.Add, contentDescription = "Đăng nhận nuôi")
//                }
//            }
//        }
//    ) { paddingValues ->
//
//        LazyColumn(
//            modifier = Modifier
//                .fillMaxSize()
//                .padding(paddingValues)
//                .background(MaterialTheme.colorScheme.background)
//                .padding(horizontal = 16.dp),
//            contentPadding = PaddingValues(vertical = 12.dp),
//            verticalArrangement = Arrangement.spacedBy(12.dp)
//        ) {
//            if (myAdoptPosts.isEmpty()) {
//                item {
//                    Text(
//                        // CẬP NHẬT THÔNG BÁO TÙY THEO CHẾ ĐỘ
//                        text = if (isMyPosts) "Bạn chưa đăng tìm chủ bé nào." else "Người dùng này chưa có bài đăng nhận nuôi nào.",
//                        modifier = Modifier.padding(vertical = 24.dp),
//                        color = Color.Gray
//                    )
//                }
//            } else {
//                items(myAdoptPosts) { adoptPost ->
//                    PostAdopt(
//                        post = adoptPost,
//                        onEditClick = { postToEdit ->
//                            // Điều hướng đến màn hình chi tiết
//                            nav.navigate(Routes.petDetail(postToEdit.id))
//                        },
//                        isLiked = false,
//                        onLikeClick = { /*...*/ },
//                        onShareClick = { /*...*/ },
//                        onCommentClick = { /*...*/ }
//                    )
//                }
//            }
//        }
//    }
//}