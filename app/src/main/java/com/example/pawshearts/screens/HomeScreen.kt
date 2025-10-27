package com.example.pawshearts.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Message
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.pawshearts.FakeRepository
import com.example.pawshearts.screens.PostCard
import com.example.pawshearts.ui.theme.AppBottomNav
import com.example.pawshearts.ui.theme.NavItem

@Composable
fun HomeScreen(navController: NavController) {
    val posts = remember { FakeRepository.getFeed() }
    var activeRoutes by remember { mutableStateOf(setOf(NavItem.Home.route)) }

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
            value = "",
            onValueChange = {},
            placeholder = { Text("Search") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .background(Color(0xFFFDEEE2), shape = MaterialTheme.shapes.medium),
            colors = TextFieldDefaults.colors(
                unfocusedContainerColor = Color(0xFFFDEEE2),
                focusedContainerColor = Color(0xFFFDEEE2),
                unfocusedIndicatorColor = Color.Transparent,
                focusedIndicatorColor = Color.Transparent
            )
        )

        Spacer(Modifier.height(16.dp))

        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 10.dp)
        ) {
            items(posts) { post ->
                PostCard(
                    post = post,
                    onClick = { navController.navigate("detail/${post.postId}") }
                )
                Spacer(modifier = Modifier.height(12.dp))
            }
        }

        // Bottom Navigation gọi từ AppIcons.kt
        AppBottomNav(
            activeRoutes = activeRoutes,
            onItemSelected = { item ->
                activeRoutes = setOf(item.route)
                // TODO: Điều hướng màn hình tại đây nếu muốn
                // navController.navigate(item.route)
            }
        )
    }
}