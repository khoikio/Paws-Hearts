package com.example.pawshearts.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.pawshearts.screens.PostCard
import com.example.pawshearts.FakeRepository

@Composable
fun AdoptScreen(navController: NavController) {
    val posts = remember { FakeRepository.getFeed().filter {it.status != "adopted"  } }
    Column(modifier = Modifier.fillMaxSize()) {
        Text(
            text = "Danh sách nhận nuôi",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(16.dp)
        )
        LazyColumn {
            items(posts.size) { idx ->
                PostCard(
                    post = posts[idx],
                    onClick = { navController.navigate("detail/${posts[idx].postId}") }
                )
            }
        }
    }
}