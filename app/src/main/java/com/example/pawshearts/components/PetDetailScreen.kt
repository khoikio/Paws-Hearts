package com.example.pawshearts.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.pawshearts.FakeRepository

@Composable
fun PetDetailScreen(id: String, onBack: () -> Unit) {
    val post = remember(id) { FakeRepository.getPostById(id) }
    if (post == null) {
        Column(
            Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Không tìm thấy bài đăng")
            Spacer(Modifier.height(8.dp))
            OutlinedButton(onClick = onBack) { Text("Quay lại") }
        }
        return
    }

    Column(Modifier.fillMaxSize()) {
        AsyncImage(
            model = post.photos.firstOrNull() ?: "https://picsum.photos/seed/paws/1200/800",
            contentDescription = post.title,
            modifier = Modifier
                .fillMaxWidth()
                .height(240.dp),
            contentScale = ContentScale.Crop
        )
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(post.title, style = MaterialTheme.typography.headlineSmall)
            Text("${post.type.uppercase()} • ${post.gender} • ${post.ageMonth} tháng")
            Text("Khu vực: ${post.location}", color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f))
            HorizontalDivider(Modifier.padding(vertical = 8.dp))
            Text(post.description)
            Spacer(Modifier.height(12.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Button(onClick = { /* TODO: Liên hệ / Nhận nuôi */ }) { Text("Quan tâm") }
                OutlinedButton(onClick = onBack) { Text("Quay lại") }
            }
        }
    }
}
