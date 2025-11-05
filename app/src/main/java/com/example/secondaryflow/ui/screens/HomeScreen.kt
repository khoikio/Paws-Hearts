package com.example.secondaryflow.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.rememberAsyncImagePainter
import com.example.secondaryflow.data.PetViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(viewModel: PetViewModel = viewModel()) {
    val pets by viewModel.pets.collectAsState()

    LaunchedEffect(Unit) { viewModel.loadPets() }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Danh sách thú cưng", fontWeight = FontWeight.Bold) }) }
    ) { padding ->
        if (pets.isEmpty()) {
            Box(
                Modifier.fillMaxSize().padding(padding),
                contentAlignment = Alignment.Center
            ) { Text("Chưa có thú cưng nào.") }
        } else {
            LazyColumn(
                Modifier.fillMaxSize().padding(padding).padding(12.dp)
            ) {
                items(pets) { pet ->
                    Card(
                        Modifier.fillMaxWidth().padding(vertical = 8.dp),
                        elevation = CardDefaults.cardElevation(6.dp)
                    ) {
                        Column(Modifier.padding(12.dp)) {
                            if (pet.imageUrl.isNotEmpty()) {
                                Image(
                                    painter = rememberAsyncImagePainter(pet.imageUrl),
                                    contentDescription = pet.name,
                                    modifier = Modifier.fillMaxWidth().height(180.dp),
                                    contentScale = ContentScale.Crop
                                )
                                Spacer(Modifier.height(8.dp))
                            }
                            Text(pet.name, style = MaterialTheme.typography.titleLarge)
                            Text("Giống: ${pet.breed}")
                            Text("Tuổi: ${pet.age}")
                            Text("Vị trí: ${pet.location}")
                            Spacer(Modifier.height(4.dp))
                            Text(pet.description, style = MaterialTheme.typography.bodyMedium)
                        }
                    }
                }
            }
        }
    }
}
