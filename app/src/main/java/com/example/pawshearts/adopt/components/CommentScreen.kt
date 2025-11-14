package com.example.pawshearts.adopt.components

// === IMPORTS HỆ THỐNG VÀ COMPOSE ĐÚNG ===
import android.app.Application
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import android.util.Log
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle // Import chính xác
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.pawshearts.R
import com.example.pawshearts.auth.AuthViewModel
import com.example.pawshearts.auth.AuthViewModelFactory
import com.example.pawshearts.auth.AuthResult
import com.example.pawshearts.adopt.AdoptViewModel
import com.example.pawshearts.adopt.AdoptViewModelFactory
import com.example.pawshearts.adopt.Comment
import java.util.Date
import java.util.concurrent.TimeUnit
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.Timestamp // Import này cần cho việc chuyển đổi Comment.createdAt


/**
 * Hàm tiện ích để hiển thị thời gian đăng.
 */
fun formatTimestamp(timestamp: Timestamp?): String {
    timestamp ?: return ""
    val date = timestamp.toDate()
    val diff = Date().time - date.time
    val minutes = TimeUnit.MILLISECONDS.toMinutes(diff)
    return when {
        minutes < 60 -> "${minutes}p" // phút
        minutes < 24 * 60 -> "${minutes / 60}h" // giờ
        else -> "${minutes / (24 * 60)}d" // ngày
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CommentScreen(
    postId: String,
    onBack: () -> Unit
) {
    // Khởi tạo ViewModel
    val context = LocalContext.current.applicationContext as Application
    val adoptViewModel: AdoptViewModel = viewModel(factory = AdoptViewModelFactory(context))
    val authViewModel: AuthViewModel = viewModel(factory = AuthViewModelFactory(context))

    // Lấy Data từ ViewModel
    val comments by adoptViewModel.comments.collectAsStateWithLifecycle(initialValue = emptyList())
    val addCommentState by adoptViewModel.addCommentState.collectAsStateWithLifecycle(initialValue = AuthResult.Idle)
    val userData by authViewModel.userProfile.collectAsStateWithLifecycle(initialValue = null)
    val currentUser = authViewModel.currentUser

    // State cho TextField
    var myCommentText by remember { mutableStateOf("") }
    val listState = rememberLazyListState()

    // BẮT ĐẦU TẢI CMT KHI VÔ MÀN HÌNH
    LaunchedEffect(postId) {
        adoptViewModel.fetchComments(postId)
    }

    // TỰ CUỘN XUỐNG KHI GỬI CMT MỚI
    LaunchedEffect(comments.size) {
        if (comments.isNotEmpty()) {
            listState.animateScrollToItem(comments.size - 1)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Bình luận (${comments.size})") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        bottomBar = {
            // CỤC ĐỂ GÕ CMT Ở DƯỚI ĐÍT
            BottomCommentBar(
                text = myCommentText,
                onTextChange = { myCommentText = it },
                isLoading = (addCommentState is AuthResult.Loading),
                onSendClick = {
                    // ✅ BƯỚC 1: TẠO BẢN SAO CỤC BỘ KHÔNG THAY ĐỔI ĐƯỢC
                    val profileData = userData
                    val user = currentUser

                    // ✅ BƯỚC 2: Kiểm tra null trên bản sao cục bộ (Smart Cast được áp dụng)
                    if (user != null && profileData != null) {
                        adoptViewModel.addComment(
                            postId = postId,
                            userId = user.uid,
                            // ✅ Sử dụng profileData (đã được Smart Cast)
                            username = profileData.username,
                            userAvatarUrl = profileData.profilePictureUrl,
                            text = myCommentText
                        )
                        myCommentText = ""
                    } else {
                        // Log lỗi nếu người dùng cố gắng gửi khi chưa đăng nhập
                        Log.w("CommentScreen", "User chưa đăng nhập hoặc profile data bị thiếu.")
                    }
                }
            )
        }
    ) { innerPadding ->

        // DANH SÁCH CMT
        if (comments.isEmpty() && addCommentState != AuthResult.Loading) {
            Box(Modifier.fillMaxSize().padding(innerPadding), contentAlignment = Alignment.Center) {
                Text("Chưa có bình luận nào :(")
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(horizontal = 16.dp),
                state = listState,
                verticalArrangement = Arrangement.spacedBy(16.dp),
                contentPadding = PaddingValues(top = 16.dp, bottom = 16.dp)
            ) {
                items(comments) { comment ->
                    // VẼ 1 DÒNG CMT
                    CommentRow(comment = comment)
                }
            }
        }
    }

    // Tự reset state sau khi Gửi
    LaunchedEffect(addCommentState) {
        if (addCommentState is AuthResult.Error || addCommentState is AuthResult.Success) {
            adoptViewModel.clearAddCommentState()
        }
    }
}


/**
 * Cục UI cho 1 dòng cmt
 */
@Composable
private fun CommentRow(comment: Comment) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Avatar thằng cmt
        AsyncImage(
            model = comment.userAvatarUrl ?: R.drawable.avatardefault,
            contentDescription = "Avatar",
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(Color.LightGray)
        )
        // Cục tên + nội dung cmt
        Column {
            Row(verticalAlignment = Alignment.Bottom, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = comment.username ?: "User",
                    fontWeight = FontWeight.SemiBold,
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = formatTimestamp(comment.createdAt), // Dùng hàm format mới
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.Gray
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            // Nội dung cmt
            Text(
                text = comment.text,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

/**
 * Cục UI cho cái TextField ở đít
 */
@Composable
private fun BottomCommentBar(
    text: String,
    onTextChange: (String) -> Unit,
    isLoading: Boolean,
    onSendClick: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shadowElevation = 8.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Ô GÕ CMT
            OutlinedTextField(
                value = text,
                onValueChange = onTextChange,
                modifier = Modifier.weight(1f),
                placeholder = { Text("Viết bình luận...") },
                shape = RoundedCornerShape(24.dp)
            )

            // NÚT GỬI
            IconButton(
                onClick = onSendClick,
                enabled = !isLoading && text.isNotBlank(),
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(if (!isLoading && text.isNotBlank()) MaterialTheme.colorScheme.primary else Color.LightGray)
            ) {
                if (isLoading) {
                    CircularProgressIndicator(modifier = Modifier.size(24.dp), color = Color.White, strokeWidth = 2.dp)
                } else {
                    Icon(Icons.Default.Send, contentDescription = "Gửi", tint = Color.White)
                }
            }
        }
    }
}