package com.example.pawshearts.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Message
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.pawshearts.components.PostCard
import com.example.pawshearts.goPetDetail
import com.example.pawshearts.data.PetPost
import com.example.pawshearts.data.PetRepository
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(nav: NavHostController) {

    val repo = remember { PetRepository() }
    var posts by remember { mutableStateOf<List<PetPost>>(emptyList()) }
    var loading by remember { mutableStateOf(true) }

    // Load danh sách từ Firestore
    LaunchedEffect(Unit) {
        loading = true
        posts = repo.getAllPets()
        loading = false
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F5))
    ) {

        // Top Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 24.dp, bottom = 8.dp, start = 12.dp, end = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            IconButton(onClick = { }) {
                Icon(Icons.Filled.Message, contentDescription = null, tint = Color(0xFFEA5600))
            }
            Text(
                text = "Paws & Hearts",
                color = Color(0xFFEA5600),
                style = MaterialTheme.typography.headlineMedium
            )
            IconButton(onClick = { }) {
                Icon(Icons.Filled.Notifications, contentDescription = null, tint = Color(0xFFEA5600))
            }
        }

        // Search (chưa áp dụng filter, để sau)
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

        // Loading
        if (loading) {
            Box(
                Modifier
                    .fillMaxSize()
                    .padding(top = 24.dp),
                contentAlignment = Alignment.Center
            ) { Text("Đang tải dữ liệu...") }
            return
        }

        // Không có bài đăng
        if (posts.isEmpty()) {
            Box(
                Modifier
                    .fillMaxSize()
                    .padding(top = 24.dp),
                contentAlignment = Alignment.Center
            ) { Text("Chưa có thú cưng nào được đăng") }
            return
        }

        // Danh sách bài đăng Firestore
        LazyColumn(
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.padding(top = 8.dp)
        ) {
            items(posts) { post ->
                PostCard(post = post, onClick = { nav.goPetDetail(post.postId) })
            }
        }
    }
}
