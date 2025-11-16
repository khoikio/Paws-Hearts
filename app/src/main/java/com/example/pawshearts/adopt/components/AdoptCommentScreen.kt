// File mới: package com.example.pawshearts.adopt

package com.example.pawshearts.adopt.components

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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.pawshearts.R
import com.example.pawshearts.adopt.AdoptComment
import com.example.pawshearts.adopt.AdoptViewModel
import com.example.pawshearts.adopt.AdoptViewModelFactory
import com.example.pawshearts.auth.AuthResult
import com.example.pawshearts.auth.AuthViewModel
import com.example.pawshearts.auth.AuthViewModelFactory
import com.example.pawshearts.post.formatTimestamp // <== M CẦN ĐẢM BẢO HÀM NÀY PUBLIC


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdoptCommentScreen(
    adoptPostId: String, // <-- Thay đổi tên biến thành adoptPostId
    adoptViewModel: AdoptViewModel,
    authViewModel: AuthViewModel,
    onBack: () -> Unit
) {
    val context = LocalContext.current.applicationContext as Application

    // 2. LẤY DATA (List cmt và info của M)
    val comments by adoptViewModel.comments.collectAsStateWithLifecycle()
    val addCommentState by adoptViewModel.addCommentState.collectAsStateWithLifecycle(initialValue = null)
    val userData by authViewModel.userProfile.collectAsStateWithLifecycle(null)
    val currentUser = authViewModel.currentUser // T lấy FirebaseUser cho lẹ

    // 3. STATE CHO Ô TEXTFIELD Ở DƯỚI
    var myCommentText by remember { mutableStateOf("") }
    val listState = rememberLazyListState() // Để T cuộn xuống cmt mới nhất

    // 4. BẮT ĐẦU TẢI CMT KHI M VÔ MÀN HÌNH
    LaunchedEffect(adoptPostId) {
        adoptViewModel.fetchComments(adoptPostId)
    }


    // 5. TỰ CUỘN XUỐNG KHI M GỬI CMT MỚI
    LaunchedEffect(comments.size) {
        if (comments.isNotEmpty()) {
            listState.animateScrollToItem(comments.size - 1)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Bình luận (${comments.size})") }, // Nó tự F5 số cmt KKK
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        bottomBar = {
            // CỤC ĐỂ M GÕ CMT Ở DƯỚI ĐÍT NÈ
            BottomCommentBar(
                text = myCommentText,
                onTextChange = { myCommentText = it },
                isLoading = (addCommentState is AuthResult.Loading),
                onSendClick = {
                    if (currentUser != null && userData != null) {
                        // === GỌI HÀM MỚI TRONG ADOPT COMMENT VM ===
                        adoptViewModel.addComment(
                            adoptPostId = adoptPostId, // <== Dùng ID Adopt
                            userId = currentUser.uid,
                            username = userData?.username,
                            userAvatarUrl = userData?.profilePictureUrl,
                            text = myCommentText
                        )
                        myCommentText = ""
                    }
                }
            )
        }
    ) { innerPadding ->

        // DANH SÁCH CMT
        if (comments.isEmpty()) {
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
                    // T VẼ 1 CÁI CMT
                    AdoptCommentRow(comment = comment)
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
private fun AdoptCommentRow(comment: AdoptComment) { // <== Dùng AdoptComment
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
                // CẦN ĐẢM BẢO HÀM formatTimestamp TỒN TẠI VÀ CÓ THỂ IMPORT
                // Hiện tại tôi giữ nguyên, bạn cần đảm bảo nó có trong package post hoặc utility
                val timeText = if (comment.createdAt != null) {
                    com.example.pawshearts.post.formatTimestamp(comment.createdAt.toDate())
                } else {
                    "Đang tải..." // Hoặc chuỗi bạn muốn hiển thị khi timestamp rỗng
                }
                Text(
                    text = timeText,
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
 * Cục UI cho cái TextField ở đít (Tái sử dụng)
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
                enabled = !isLoading && text.isNotBlank(), // Đang gửi hoặc chưa gõ -> Mờ
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
