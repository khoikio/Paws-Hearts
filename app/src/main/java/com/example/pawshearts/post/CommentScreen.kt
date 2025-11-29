package com.example.pawshearts.post

import android.app.Application
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
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
import com.example.pawshearts.auth.AuthResult
import com.example.pawshearts.auth.AuthViewModel
import com.example.pawshearts.auth.AuthViewModelFactory
import com.example.pawshearts.post.Comment
import com.google.firebase.Timestamp
import java.util.Date

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CommentScreen(
    postId: String,
    onBack: () -> Unit
) {
    val context = LocalContext.current.applicationContext as Application
    val postViewModel: PostViewModel = viewModel(factory = PostViewModelFactory(context))
    val authViewModel: AuthViewModel = viewModel(factory = AuthViewModelFactory(context))

    val comments by postViewModel.comments.collectAsStateWithLifecycle()
    val addCommentState by postViewModel.addCommentState.collectAsStateWithLifecycle()
    val userData by authViewModel.userProfile.collectAsStateWithLifecycle(null)
    val currentUser = authViewModel.currentUser

    var myCommentText by remember { mutableStateOf("") }
    val listState = rememberLazyListState()

    LaunchedEffect(postId) {
        if (postId.isNotBlank()) {
            postViewModel.fetchComments(postId)
        }
    }

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
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        bottomBar = {
            BottomCommentBar(
                text = myCommentText,
                onTextChange = { myCommentText = it },
                isLoading = (addCommentState is AuthResult.Loading),
                onSendClick = {
                    if (currentUser != null && myCommentText.isNotBlank()) {
                        postViewModel.addComment(
                            postId = postId,
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
        if (comments.isEmpty()) {
            Box(Modifier.fillMaxSize().padding(innerPadding), contentAlignment = Alignment.Center) {
                Text("Chưa có bình luận nào")
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
                    CommentRow(comment = comment)
                }
            }
        }
    }

    LaunchedEffect(addCommentState) {
        if (addCommentState is AuthResult.Error || addCommentState is AuthResult.Success) {
            postViewModel.clearAddCommentState()
        }
    }
}

@Composable
private fun CommentRow(comment: Comment) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        AsyncImage(
            model = comment.userAvatarUrl ?: R.drawable.avatardefault,
            contentDescription = "Avatar",
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(Color.LightGray)
        )
        Column {
            Row(verticalAlignment = Alignment.Bottom, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = comment.username ?: "User",
                    fontWeight = FontWeight.SemiBold,
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = formatTimestamp(comment.createdAt),
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.Gray
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = comment.text,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

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
            OutlinedTextField(
                value = text,
                onValueChange = onTextChange,
                modifier = Modifier.weight(1f),
                placeholder = { Text("Viết bình luận...") },
                shape = RoundedCornerShape(24.dp)
            )
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
                    Icon(Icons.AutoMirrored.Filled.Send, contentDescription = "Gửi", tint = Color.White)
                }
            }
        }
    }
}

fun formatTimestamp(timestamp: Timestamp?): String {
    if (timestamp == null) return ""
    val date = timestamp.toDate()
    val diff = Date().time - date.time
    val minutes = diff / 60000
    val hours = minutes / 60
    val days = hours / 24

    return when {
        minutes < 1 -> "Vừa xong"
        minutes < 60 -> "$minutes phút trước"
        hours < 24 -> "$hours giờ trước"
        else -> "$days ngày trước"
    }
}
