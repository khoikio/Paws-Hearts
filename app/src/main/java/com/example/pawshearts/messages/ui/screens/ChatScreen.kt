package com.example.pawshearts.messages.ui.screens

import android.app.Application
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInHorizontally
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Send
import androidx.compose.material.icons.outlined.ArrowBackIosNew
import androidx.compose.material.icons.outlined.AttachFile
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.pawshearts.R
import com.example.pawshearts.messages.model.ChatMessageUiModel
import com.example.pawshearts.messages.model.GLOBAL_THREAD_ID
import com.example.pawshearts.messages.model.MessageStatus
import com.example.pawshearts.messages.presentation.ChatViewModel
import com.example.pawshearts.messages.presentation.ChatViewModelFactory
import com.example.pawshearts.messages.ui.components.ChatCardBackground
import com.example.pawshearts.messages.ui.components.ChatInputBg
import com.example.pawshearts.messages.ui.components.ChatOrange
import com.example.pawshearts.messages.ui.components.ChatOuterBackground
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch

@Composable
fun ChatScreen(
    threadId: String,
    nav: NavHostController
) {
    val app = LocalContext.current.applicationContext as Application
    val currentUser = FirebaseAuth.getInstance().currentUser

    // Nếu chưa login thì thôi, show màn trống
    if (currentUser == null) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(ChatOuterBackground),
            contentAlignment = Alignment.Center
        ) {
            Text("Bạn chưa đăng nhập")
        }
        return
    }

    // 1 ChatViewModel duy nhất cho cả màn
    val chatViewModel: ChatViewModel = viewModel(
        factory = ChatViewModelFactory(
            app = app,
            currentUserId = currentUser.uid,
            currentUserName = currentUser.displayName
        )
    )

    // Hủy listener khi rời màn
    DisposableEffect(Unit) {
        onDispose {
            chatViewModel.stopAllListeners()
        }
    }

    // Load đúng thread khi threadId thay đổi
    LaunchedEffect(threadId) {
        chatViewModel.loadThread(threadId)
    }

    val messages by chatViewModel.messages.collectAsState()
    val isTyping by chatViewModel.isTyping.collectAsState()
    val isGlobal = threadId == GLOBAL_THREAD_ID
    val headerTitle by chatViewModel.headerTitle.collectAsState()
    val headerAvatarRes = if (isGlobal) R.drawable.ic_app else R.drawable.avatardefault

    val listState = rememberLazyListState()
    val scope = rememberCoroutineScope()

    // Auto scroll xuống cuối khi có tin mới
    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            scope.launch { listState.animateScrollToItem(messages.lastIndex) }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .imePadding()
    ) {
        ChatHeader(
            title = headerTitle,
            avatarRes = headerAvatarRes,
            onBackClick = { nav.popBackStack() }
        )

        HorizontalDivider(color = Color(0xFFF0F0F0))

        Card(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                // Danh sách tin nhắn
                LazyColumn(
                    state = listState,
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 12.dp, vertical = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(items = messages, key = { it.id }) { msg ->
                        ChatBubble(message = msg)
                    }
                    item { Spacer(modifier = Modifier.height(8.dp)) }
                }

                // Đang nhập...
                AnimatedVisibility(visible = isTyping) {
                    Text(
                        text = "Đang nhập...",
                        modifier = Modifier.padding(start = 12.dp, bottom = 6.dp),
                        style = LocalTextStyle.current.copy(
                            color = Color.Gray,
                            fontSize = 12.sp
                        )
                    )
                }

                // Thanh nhập tin nhắn
                ChatInputBar(
                    onSend = { text ->
                        val trimmed = text.trim()
                        if (trimmed.isNotEmpty()) {
                            chatViewModel.sendMessage(trimmed)
                        }
                    },
                    onAttach = { /* TODO: handle file attach */ }
                )
            }
        }
    }
}

@Preview
@Composable
fun ChatScreenPreview() {
    // Fake NavHostController for preview
    val nav = NavHostController(LocalContext.current)
    ChatScreen(threadId = GLOBAL_THREAD_ID, nav = nav)
}

@Composable
private fun ChatHeader(
    title: String,
    avatarRes: Int,
    onBackClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(WindowInsets.statusBars.asPaddingValues()),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onBackClick) {
            Icon(Icons.Outlined.ArrowBackIosNew, contentDescription = "Back",
                tint = MaterialTheme.colorScheme.onBackground    )
        }
        Image(
            painter = painterResource(id = avatarRes),
            contentDescription = title,
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = title,
            color = MaterialTheme.colorScheme.onBackground,
            fontWeight = FontWeight.SemiBold,
            fontSize = 18.sp,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Preview
@Composable
private fun ChatHeaderPreview() {
    ChatHeader(
        title = "Paw Hub",
        avatarRes = R.drawable.ic_app,
        onBackClick = {}
    )
}

@Composable
private fun ChatBubble(message: ChatMessageUiModel) {
    val textColor = if (message.isMine) Color.White else MaterialTheme.colorScheme.onSurface
    val bubbleColor = if (message.isMine) ChatOrange else MaterialTheme.colorScheme.surfaceVariant
    AnimatedVisibility(
        visible = true,
        enter = fadeIn() + slideInHorizontally(initialOffsetX = { if (message.isMine) 200 else -200 })
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = if (message.isMine) Arrangement.End else Arrangement.Start
        ) {
            if (!message.isMine) {
                Image(
                    painter = painterResource(id = R.drawable.avatardefault),
                    contentDescription = null,
                    modifier = Modifier
                        .size(28.dp)
                        .clip(CircleShape)
                        .align(Alignment.Bottom)
                )
                Spacer(Modifier.width(6.dp))
            }

            Column(
                horizontalAlignment = if (message.isMine) Alignment.End else Alignment.Start
            ) {
                Box(
                    modifier = Modifier
                        .widthIn(max = 260.dp)
                        .background(
                            bubbleColor,
                            RoundedCornerShape(
                                topStart = 20.dp,
                                topEnd = 20.dp,
                                bottomStart = if (message.isMine) 20.dp else 4.dp,
                                bottomEnd = if (message.isMine) 4.dp else 20.dp
                            )
                        )
                        .padding(14.dp)
                ) {
                    Column {
                        Text(message.text, color = textColor)
                        Spacer(modifier = Modifier.height(6.dp))
                        if (message.isMine) {
                            val statusText = when (message.status) {
                                MessageStatus.SENDING -> "Đang gửi..."
                                MessageStatus.SENT -> "Đã gửi"
                                MessageStatus.FAILED -> "Gửi thất bại"
                                else -> ""
                            }
                            if (statusText.isNotEmpty()) {
                                Text(
                                    statusText,
                                    color = textColor.copy(alpha = 0.8f),
                                    style = MaterialTheme.typography.labelSmall
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true, name = "My Message")
@Composable
private fun ChatBubbleMinePreview() {
    val message = ChatMessageUiModel(
        id = "1",
        text = "Hello, this is my message.",
        time = "10:00",
        isMine = true,
        status = MessageStatus.SENT,
        threadId = "global"
    )
    ChatBubble(message = message)
}

@Preview(showBackground = true, name = "Their Message")
@Composable
private fun ChatBubbleTheirsPreview() {
    val message = ChatMessageUiModel(id = "2", text = "Hi, this is a reply.", time = "10:01", isMine = false, status = MessageStatus.SEEN, threadId = "global")
    ChatBubble(message = message)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ChatInputBar(
    onSend: (String) -> Unit,
    onAttach: () -> Unit
) {
    var text by remember { mutableStateOf("") }
    val canSend = text.isNotBlank()

    Surface(tonalElevation = 4.dp,
        color = MaterialTheme.colorScheme.surface) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onAttach) {
                Icon(
                    imageVector = Icons.Outlined.AttachFile,
                    contentDescription = "attach",
                            tint = MaterialTheme.colorScheme.onSurface
                )
            }

            TextField(
                value = text,
                onValueChange = { text = it },
                modifier = Modifier
                    .weight(1f)
                    .height(52.dp),
                placeholder = { Text("Nhập tin nhắn...") },
                singleLine = true,
                shape = RoundedCornerShape(24.dp),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                    unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,

                    // 👈 Màu chữ khi gõ
                    focusedTextColor = MaterialTheme.colorScheme.onSurface,
                    unfocusedTextColor = MaterialTheme.colorScheme.onSurface,

                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                ),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
                keyboardActions = KeyboardActions(onSend = {
                    if (canSend) {
                        onSend(text)
                        text = ""
                    }
                })
            )

            Spacer(Modifier.width(8.dp))

            IconButton(
                onClick = {
                    if (canSend) {
                        onSend(text)
                        text = ""
                    }
                },
                modifier = Modifier
                    .size(46.dp)
                    .clip(CircleShape)
                    .background(if (canSend) ChatOrange else Color(0xFFCCCCCC))
            ) {
                Icon(
                    Icons.AutoMirrored.Outlined.Send,
                    contentDescription = "send",
                    tint = Color.White
                )
            }
        }
    }
}

@Preview
@Composable
private fun ChatInputBarPreview() {
    ChatInputBar(
        onSend = {},
        onAttach = {}
    )
}

