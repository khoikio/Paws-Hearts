package com.example.pawshearts.messages.ui.screens

import android.app.Application
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInHorizontally
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Send
import androidx.compose.material.icons.outlined.ArrowBackIosNew
import androidx.compose.material.icons.outlined.AttachFile
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.pawshearts.R
import com.example.pawshearts.messages.ui.components.ChatCardBackground
import com.example.pawshearts.messages.ui.components.ChatInputBg
import com.example.pawshearts.messages.model.ChatMessageUiModel
import com.example.pawshearts.messages.ui.components.ChatOrange
import com.example.pawshearts.messages.ui.components.ChatOuterBackground
import com.example.pawshearts.messages.presentation.ChatViewModel
import com.example.pawshearts.messages.model.MessageStatus


// ===== ChatScreen =====
@Composable
fun ChatScreen(
    threadId: String,
    onBackClick: () -> Unit
) {
    val context = LocalContext.current.applicationContext as Application
    val chatViewModel: ChatViewModel = viewModel(
        factory = ViewModelProvider.AndroidViewModelFactory(context)
    )

    LaunchedEffect(threadId) {
        chatViewModel.loadThread(threadId)
    }

    val messages by chatViewModel.messages.collectAsState()
    val isTyping by chatViewModel.isTyping.collectAsState() // cần có trong ViewModel

    // State cuộn cho danh sách
    val listState = rememberLazyListState()

    // Khi số lượng messages thay đổi → cuộn xuống cuối
    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.lastIndex)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(ChatOuterBackground)
            .imePadding() // đẩy UI lên khi bàn phím hiện
    ) {
        ChatHeader(onBackClick = onBackClick)

        HorizontalDivider(color = Color(0xFFF0F0F0))

        // Card khung chat (tuỳ chọn)
        Card(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = ChatCardBackground),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                LazyColumn(
                    state = listState,
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(messages, key = { it.id }) { msg ->
                        ChatBubble(message = msg)
                    }
                    //if (chatViewModel.isTyping.collectAsState().value) {
                    //    TypingIndicator()
                    //}
                    // Typing indicator ở cuối danh sách
                    if (isTyping) {
                        item {
                            TypingIndicator(name = "Ngọc Anh")
                        }
                    }
                }

                ChatInputBar(
                    onSend = { text -> chatViewModel.sendMessage(text) },
                    onAttach = { /* TODO: đính kèm file */ }
                )
            }
        }
    }
}

// ===== Header =====
@Composable
private fun ChatHeader(onBackClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(WindowInsets.statusBars.asPaddingValues()),
            //.padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onBackClick) {
            Icon(
                imageVector = Icons.Outlined.ArrowBackIosNew,
                contentDescription = "Back"
            )
        }

        Image(
            painter = painterResource(id = R.drawable.avatardefault),
            contentDescription = "Ngoc Anh",
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
        )

        Spacer(modifier = Modifier.width(8.dp))

        Text(
            text = "Ngọc Anh",
            fontWeight = FontWeight.SemiBold,
            fontSize = 18.sp,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

// ===== Bubble với trạng thái + hiệu ứng xuất hiện =====
@Composable
private fun ChatBubble(message: ChatMessageUiModel) {
    val bubbleColor = if (message.isMine) ChatOrange else Color(0xFFF5F5F5)
    val textColor = if (message.isMine) Color.White else Color(0xFF222222)

    AnimatedVisibility(
        visible = true,
        enter = fadeIn() + slideInHorizontally(
            initialOffsetX = { if (message.isMine) 200 else -200 }
        )
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
                Spacer(modifier = Modifier.width(6.dp))
            }

            Column(horizontalAlignment = if (message.isMine) Alignment.End else Alignment.Start) {
                Box(
                    modifier = Modifier
                        .widthIn(max = 260.dp)
                        .background(
                            color = bubbleColor,
                            shape = RoundedCornerShape(
                                topStart = 20.dp,
                                topEnd = 20.dp,
                                bottomStart = if (message.isMine) 20.dp else 4.dp,
                                bottomEnd = if (message.isMine) 4.dp else 20.dp
                            )
                        )
                        .padding(horizontal = 14.dp, vertical = 10.dp)
                ) {
                    Text(
                        text = message.text,
                        color = textColor,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                // Thời gian + trạng thái (chỉ hiển thị trạng thái cho tin của mình)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = message.time,
                        style = MaterialTheme.typography.labelSmall,
                        color = Color(0xFF9E9E9E)
                    )
                    if (message.isMine) {
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = when (message.status) {
                                MessageStatus.SENDING -> "Đang gửi..."
                                MessageStatus.SENT -> "Đã gửi"
                                MessageStatus.SEEN -> "Đã xem"
                            },
                            style = MaterialTheme.typography.labelSmall,
                            color = Color(0xFF9E9E9E)
                        )
                    }
                }
            }

            if (message.isMine) {
                Spacer(modifier = Modifier.width(6.dp))
                Image(
                    painter = painterResource(id = R.drawable.avatar1),
                    contentDescription = null,
                    modifier = Modifier
                        .size(28.dp)
                        .clip(CircleShape)
                        .align(Alignment.Bottom)
                )
            }
        }
    }
}

// ===== Input bar =====
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ChatInputBar(onSend: (String) -> Unit, onAttach: () -> Unit) {
    var text by remember { mutableStateOf("") }

    Surface(tonalElevation = 4.dp) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextField(
                value = text,
                onValueChange = { text = it },
                modifier = Modifier
                    .weight(1f)
                    .height(52.dp),
                placeholder = { Text("Nhập tin nhắn...", textAlign = TextAlign.Start) },
                singleLine = true,
                shape = RoundedCornerShape(24.dp),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = ChatInputBg,
                    unfocusedContainerColor = ChatInputBg,
                    disabledContainerColor = ChatInputBg,
                    errorContainerColor = ChatInputBg,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    disabledIndicatorColor = Color.Transparent,
                    errorIndicatorColor = Color.Transparent
                )
            )

            Spacer(modifier = Modifier.width(8.dp))

            IconButton(
                onClick = onAttach,
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFFF0E4D9))
            ) {
                Icon(Icons.Outlined.AttachFile, contentDescription = "Attach")
            }

            Spacer(modifier = Modifier.width(6.dp))

            IconButton(
                onClick = {
                    if (text.isNotBlank()) {
                        onSend(text)
                        text = ""
                    }
                },
                modifier = Modifier
                    .size(46.dp)
                    .clip(CircleShape)
                    .background(ChatOrange)
            ) {
                Icon(Icons.AutoMirrored.Outlined.Send, contentDescription = "Send", tint = Color.White)
            }
        }
    }
}

// ===== Typing indicator =====
@Composable
private fun TypingIndicator(name: String) {
    val transition = rememberInfiniteTransition(label = "typing")
    val dot1 by transition.animateFloat(
        initialValue = 0.3f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(500),
            repeatMode = RepeatMode.Reverse
        ),
        label = "dot1"
    )
    val dot2 by transition.animateFloat(
        initialValue = 0.3f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(500, delayMillis = 150),
            repeatMode = RepeatMode.Reverse
        ),
        label = "dot2"
    )
    val dot3 by transition.animateFloat(
        initialValue = 0.3f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(500, delayMillis = 300),
            repeatMode = RepeatMode.Reverse
        ),
        label = "dot3"
    )

    Row(
        modifier = Modifier
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.Start
    ) {
        // Bubble typing giống tin nhắn của đối phương
        Row(
            modifier = Modifier
                .background(Color(0xFFF5F5F5), RoundedCornerShape(20.dp))
                .padding(horizontal = 14.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("$name đang nhập…", color = Color.Gray)
            Spacer(modifier = Modifier.width(8.dp))
            Dot(alpha = dot1)
            Spacer(modifier = Modifier.width(4.dp))
            Dot(alpha = dot2)
            Spacer(modifier = Modifier.width(4.dp))
            Dot(alpha = dot3)
        }
    }
}

@Composable
private fun Dot(alpha: Float) {
    Box(
        modifier = Modifier
            .size(6.dp)
            .clip(CircleShape)
            .background(Color.Gray.copy(alpha = alpha))
    )
}
