package com.example.pawshearts.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.pawshearts.FakeRepository

@Composable
fun PetDetailScreen(navController: NavController, petId: String) {
    val post = remember { FakeRepository.getFeed().find { it.postId == petId } }

    if (post == null) {
        Text("Không tìm thấy thú cưng!")
        return
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        AsyncImage(
            model = post.photos.firstOrNull() ?: "",
            contentDescription = post.title,
            modifier = Modifier
                .fillMaxWidth()
                .height(250.dp)
        )
        Spacer(Modifier.height(16.dp))
        Text(
            text = post.title,
            style = MaterialTheme.typography.headlineMedium
        )
        Text(text = "${post.type} • ${post.gender} • ${post.location}")
        Spacer(Modifier.height(8.dp))
        Text(text = post.description)
        Spacer(Modifier.height(24.dp))
        Button(onClick = { /* TODO: handle interest */ }) {
            Text("Quan tâm")
        }
        Spacer(Modifier.height(24.dp))
        OutlinedButton(onClick = { navController.popBackStack() }) {
            Text("Quay lại")
        }
    }
}