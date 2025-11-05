package com.example.pawshearts.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Message
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.pawshearts.components.PostCard
import com.example.pawshearts.goPetDetail
import com.example.pawshearts.viewmodel.HomeViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    nav: NavHostController,
    homeViewModel: HomeViewModel = viewModel()
) {
    val uiState by homeViewModel.uiState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F5))
    ) {

        // Top Bar: Message - Title - Notification
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 24.dp, bottom = 8.dp, start = 12.dp, end = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            IconButton(onClick = { /* TODO: mở màn hình tin nhắn */ }) {
                Icon(
                    imageVector = Icons.Filled.Message,
                    contentDescription = "Tin nhắn",
                    tint = Color(0xFFEA5600)
                )
            }
            Text(
                text = "Paws & Hearts",
                color = Color(0xFFEA5600),
                style = MaterialTheme.typography.headlineMedium
            )
            IconButton(onClick = { /* TODO: mở notification */ }) {
                Icon(
                    imageVector = Icons.Filled.Notifications,
                    contentDescription = "Thông báo",
                    tint = Color(0xFFEA5600)
                )
            }
        }

        // Search bar
        TextField(
            value = uiState.searchQuery,
            onValueChange = { homeViewModel.onSearchQueryChange(it) },
            placeholder = { Text("Search") },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Search Icon"
                )
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            shape = CircleShape, // Makes the corners fully rounded
            colors = TextFieldDefaults.colors(
                unfocusedContainerColor = Color(0xFFFDEEE2),
                focusedContainerColor = Color(0xFFFDEEE2),
                unfocusedIndicatorColor = Color.Transparent,
                focusedIndicatorColor = Color.Transparent
            )
        )

        // List of posts
        LazyColumn(
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.padding(top = 8.dp)
        ) {
            items(uiState.displayedPosts) { post ->
                PostCard(post = post, onClick = { nav.goPetDetail(post.postId) })
            }
        }
    }
}
