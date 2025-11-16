package com.example.pawshearts.adopt.components

// === M IMPORT 1 ĐỐNG NÀY VÔ KKK ===
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import coil.compose.rememberAsyncImagePainter
import com.example.pawshearts.R // M check M có R.drawable.avatardefault
import com.example.pawshearts.adopt.AdoptViewModel
import com.example.pawshearts.auth.AuthViewModel
import com.example.pawshearts.navmodel.Routes

// ===================================

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdoptScreen(
    nav: NavHostController,
    adoptViewModel: AdoptViewModel,
    authViewModel: AuthViewModel
) {
    // 1. LẤY LIST TẤT CẢ TỪ NÃO KKK
    val allAdoptPosts by adoptViewModel.allAdoptPosts.collectAsStateWithLifecycle(initialValue = emptyList())
    val userProfile by authViewModel.userProfile.collectAsStateWithLifecycle()
    val avatarUrl = userProfile?.profilePictureUrl

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Tìm chủ (Tất cả)") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant,
                    titleContentColor = MaterialTheme.colorScheme.onSurfaceVariant
                )
            )
        }
    ) { paddingValues ->

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(MaterialTheme.colorScheme.background),            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            // === CÁI NÚT FB M ĐÒI NÈ M ƠI KKK ===
            item {
                CreatePostButton(
                    avatarUrl = avatarUrl, // Avatar xịn
                    onClick = {
                        nav.navigate(Routes.CREATE_ADOPT_POST_SCREEN) // Bấm vô nhảy qua trang Tạo
                    }
                )
            }
            // ===================================

            // === CÁI LIST BÀI KKK ===
            if (allAdoptPosts.isEmpty()) {
                item {
                    Text(
                        "Chưa có bé nào tìm chủ KKK :v",
                        modifier = Modifier.padding(vertical = 24.dp),
                        color = Color.Gray
                    )
                }
            } else {
                items(allAdoptPosts) { adoptPost ->
                    // M XÀI LẠI CÁI PostAdopt Card M code KKK
                    PostAdopt(
                        post = adoptPost,
                        onEditClick = {
                            // Mốt T với M code M bấm vô nó nhảy qua PetDetail KKK
                        }
                    )
                }
            }
        }
    }
}

// === CÁI NÚT FB M GỬI T NÈ KKK ===
@Composable
fun CreatePostButton(
    avatarUrl: String?,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick), // M bấm đâu cũng lụm KKK
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // AVATAR M
            Image(
                painter = if (avatarUrl != null)
                    rememberAsyncImagePainter(avatarUrl)
                else
                    painterResource(id = R.drawable.avatardefault),
                contentDescription = "Avatar",
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .border(1.dp, Color.Gray, CircleShape),
                contentScale = ContentScale.Crop
            )
            // CÁI Ô XÁM M BẤM
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(38.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .padding(horizontal = 16.dp),
                contentAlignment = Alignment.CenterStart
            ) {
                Text(
                    "Bạn muốn tìm chủ cho bé nào?",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontWeight = FontWeight.SemiBold
                )
            }

        }
    }
}