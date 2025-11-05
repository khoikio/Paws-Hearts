package com.example.pawshearts.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.pawshearts.data.PetRepository
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
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage

@Composable
fun PetDetailScreen(id: String, onBack: () -> Unit) {

    // Tạo state để chứa dữ liệu tải từ Firestore
    var post by remember { mutableStateOf<com.example.pawshearts.data.PetPost?>(null) }
    var loading by remember { mutableStateOf(true) }

    // Gọi Firestore
    LaunchedEffect(id) {
        loading = true
        post = PetRepository().getPetById(id)
        loading = false
    }

    //  Loading UI
    if (loading) {
        Column(
            Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Đang tải dữ liệu...")
        }
        return
    }

    // Không tìm thấy
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

    // Có dữ liệu → hiển thị UI
    Column(Modifier.fillMaxSize()) {
        AsyncImage(
            model = post!!.imageURL.firstOrNull() ?: "https://picsum.photos/seed/paws/1200/800",
            contentDescription = post!!.title,
            modifier = Modifier
                .fillMaxWidth()
                .height(240.dp),
            contentScale = ContentScale.Crop
        )

        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(post!!.title, style = MaterialTheme.typography.headlineSmall)
            Text("${post!!.type.uppercase()} • ${post!!.gender} • ${post!!.ageMonth} tháng")
            Text("Khu vực: ${post!!.location}", color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f))
            Divider(Modifier.padding(vertical = 8.dp))
            Text(post!!.description)

            Spacer(Modifier.height(12.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Button(onClick = { /* TODO: Nhận nuôi */ }) { Text("Quan tâm") }
                OutlinedButton(onClick = onBack) { Text("Quay lại") }
            }
        }
    }
}
