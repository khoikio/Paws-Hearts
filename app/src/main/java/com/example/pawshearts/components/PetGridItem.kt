package com.example.pawshearts.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.pawshearts.PetPost
import com.example.pawshearts.ui.theme.DarkText

@Composable
fun PetGridItem(
    post: PetPost,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier.clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column {
            AsyncImage(
                model = post.photos.firstOrNull(),
                contentDescription = post.title,
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f) // Makes the image square
                    .clip(MaterialTheme.shapes.medium),
                contentScale = ContentScale.Crop

            )
            Column(modifier = Modifier.padding(12.dp)) {

            }
        }
    }
}
