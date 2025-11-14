package com.example.pawshearts.messages.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons

import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.ArrowBackIosNew
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.pawshearts.R
import com.example.pawshearts.messages.ui.components.ChatCardBackground
import com.example.pawshearts.messages.ui.components.ChatOrange
import com.example.pawshearts.messages.ui.components.ChatOuterBackground
import com.example.pawshearts.messages.ui.components.ChatSearchBg
import com.example.pawshearts.messages.presentation.MessagesViewModel

data class ConversationUiModel(
    val id: String,
    val name: String,
    val lastMessage: String,
    val timeLabel: String,
    val unreadCount: Int = 0,                 // số tin chưa đọc
    val statusDotColor: Color? = null,        // chấm trạng thái (online / thông báo)
    val avatarRes: Int = R.drawable.avatardefault // ảnh đại diện
)

@Composable
fun MessagesScreen(
    onBackClick: () -> Unit,
    onThreadClick: (String) -> Unit,
    viewModel: MessagesViewModel = viewModel()
) {
    val conversations by viewModel.conversations.collectAsState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(ChatOuterBackground)
    ) {
        Card(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp, vertical = 24.dp),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(
                containerColor = ChatCardBackground
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 20.dp, vertical = 16.dp)
            ) {
                MessagesHeader(
                    onBackClick = onBackClick,
                    onAddClick = { /* TODO: tạo cuộc trò chuyện mới */ }
                )

                Spacer(modifier = Modifier.height(16.dp))

                SearchConversationField()

                Spacer(modifier = Modifier.height(12.dp))

                LazyColumn {
                    items(conversations) { conversation ->
                        ConversationRow(
                            conversation = conversation,
                            onClick = { onThreadClick(conversation.id) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun MessagesHeader(
    onBackClick: () -> Unit,
    onAddClick: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onBackClick) {
            Icon(
                imageVector = Icons.Outlined.ArrowBackIosNew,
                contentDescription = "Back"
            )
        }

        Spacer(modifier = Modifier.width(4.dp))

        Text(
            text = "Tin nhắn",
            fontSize = 20.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.weight(1f),
        )

        IconButton(onClick = onAddClick) {
            Icon(
                imageVector = Icons.Outlined.Add,
                contentDescription = "New chat"
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SearchConversationField() {
    var query = "" // hiện tại chưa filter, chỉ là UI

    TextField(
        value = query,
        onValueChange = { /* TODO: filter list sau */ },
        modifier = Modifier
            .fillMaxWidth()
            .height(52.dp),
        placeholder = { Text("Tìm kiếm cuộc trò chuyện...") },
        leadingIcon = {
            Icon(
                imageVector = Icons.Outlined.Search,
                contentDescription = "Search"
            )
        },
        singleLine = true,
        shape = RoundedCornerShape(24.dp),
        colors = TextFieldDefaults.colors(
            focusedContainerColor = ChatSearchBg,
            unfocusedContainerColor = ChatSearchBg,
            disabledContainerColor = ChatSearchBg,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            disabledIndicatorColor = Color.Transparent
        )
    )
}

@Composable
private fun ConversationRow(
    conversation: ConversationUiModel,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = painterResource(id = conversation.avatarRes),
            contentDescription = conversation.name,
            modifier = Modifier
                .size(46.dp)
                .clip(CircleShape)
        )

        Spacer(modifier = Modifier.width(12.dp))

        Column(
            modifier = Modifier.weight(1f)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = conversation.name,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.weight(1f))

                Text(
                    text = conversation.timeLabel,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFF9E9E9E)
                )
            }

            Text(
                text = conversation.lastMessage,
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                color = Color(0xFF777777)
            )
        }

        when {
            // Có số chưa đọc: hiện badge tròn cam
            conversation.unreadCount > 0 -> {
                Spacer(modifier = Modifier.width(8.dp))
                Box(
                    modifier = Modifier
                        .size(20.dp)
                        .clip(CircleShape)
                        .background(ChatOrange),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = conversation.unreadCount.toString(),
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.White
                    )
                }
            }

            // Không có số, nhưng có chấm trạng thái
            conversation.statusDotColor != null -> {
                Spacer(modifier = Modifier.width(8.dp))
                Box(
                    modifier = Modifier
                        .size(10.dp)
                        .clip(CircleShape)
                        .background(conversation.statusDotColor)
                )
            }
        }
    }
}

@Preview
@Composable
fun MessagesScreenPreview() {
    MessagesScreen(
        onBackClick = {},
        onThreadClick = {}
    )
}