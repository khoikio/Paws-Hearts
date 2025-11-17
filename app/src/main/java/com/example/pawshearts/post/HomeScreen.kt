package com.example.pawshearts.post

import android.app.Application
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.example.pawshearts.R
import com.example.pawshearts.auth.AuthViewModel
import com.example.pawshearts.auth.AuthViewModelFactory
import com.example.pawshearts.navmodel.Routes
import com.example.pawshearts.navmodel.goPetDetail
import java.text.Normalizer
import java.util.regex.Pattern

// HÀM LOẠI BỎ DẤU (ĐÃ RẤT TỐT)
fun String.removeAccents(): String {
    val normalized = Normalizer.normalize(this, Normalizer.Form.NFD)
    return Pattern.compile("\\p{InCombiningDiacriticalMarks}+")
        .matcher(normalized)
        .replaceAll("")
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(nav: NavHostController) {
    // --- KHỞI TẠO viewmodel và state ---
    val context = LocalContext.current.applicationContext as Application
    val postViewModel: PostViewModel = viewModel(factory = PostViewModelFactory(context))
    val authViewModel: AuthViewModel = viewModel(factory = AuthViewModelFactory(context))
    // coroutine scope for possible UI feedback (not used here, we log instead)
    val coroutineScope = rememberCoroutineScope()

    val allPosts by postViewModel.allPosts.collectAsStateWithLifecycle()
    val currentUserId = authViewModel.currentUser?.uid ?: ""
    val currentUserProfile by authViewModel.userProfile.collectAsStateWithLifecycle()
    var searchText by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        postViewModel.fetchAllPosts()
    }

    // --- BẮT ĐẦU GIAO DIỆN ---
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background) // Nền chính nghe theo theme
    ) {
        // --- TOP BAR BAO GỒM CẢ THANH SEARCH ---
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surface) // Nền TopBar nghe theo theme
                .padding(16.dp)
        ) {
            // HÀNG 1: ICON, TÊN APP, ICON
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                IconButton(onClick = { nav.navigate(Routes.NOTIFICATION_SCREEN) }) {
                    Icon(
                        imageVector = Icons.Filled.Notifications,
                        contentDescription = "Thông báo",
                        tint = MaterialTheme.colorScheme.primary // Màu nghe theo theme
                    )
                }

                Text(
                    text = "Paws & Hearts",
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.primary // Màu nghe theo theme
                )

                IconButton(onClick = { nav.navigate(Routes.MESSAGES) }) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_chat), // Giả sử M có icon này
                        contentDescription = "Tin nhắn",
                        modifier = Modifier.size(24.dp),
                        colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.primary) // Màu nghe theo theme
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // HÀNG 2: THANH TÌM KIẾM
            TextField(
                value = searchText,
                onValueChange = { searchText = it },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search Icon"
                    )
                },
                placeholder = { Text("Tìm kiếm pet, giống, địa chỉ...") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(24.dp),
                // Thanh search nghe theo theme
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                    unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                    focusedIndicatorColor = MaterialTheme.colorScheme.primary, // Thêm viền khi focus
                    unfocusedIndicatorColor = MaterialTheme.colorScheme.surfaceVariant
                ),
                singleLine = true
            )
        }

        // --- THANH "BẠN ĐANG NGHĨ GÌ?" ---
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp)
                .clickable { nav.navigate(Routes.CREATE_POST_SCREEN) },
            shape = MaterialTheme.shapes.large,
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                AsyncImage(
                    model = currentUserProfile?.profilePictureUrl, // Sửa lại cho đúng model
                    contentDescription = "User Avatar",
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f), CircleShape),
                    contentScale = ContentScale.Crop
                )
                Text(
                    text = "Bạn đang nghĩ gì?",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Start
                )
            }
        }

        // --- DANH SÁCH BÀI ĐĂNG ĐÃ LỌC ---
        val filteredPosts = remember(searchText, allPosts) {
            if (searchText.isBlank()) {
                allPosts
            } else {
                val keyword = searchText.removeAccents().lowercase()
                allPosts.filter { post ->
                    post.petName.removeAccents().lowercase().contains(keyword) ||
                            (post.petBreed?.removeAccents()?.lowercase()?.contains(keyword) ?: false) ||
                            (post.location?.removeAccents()?.lowercase()?.contains(keyword) ?: false) ||
                            post.description.removeAccents().lowercase().contains(keyword) ||
                            post.userName.removeAccents().lowercase().contains(keyword) // Sửa lại cho đúng model
                }
            }
        }

        if (filteredPosts.isEmpty() && allPosts.isNotEmpty()) { // Chỉ hiện khi có bài đăng nhưng không tìm thấy
            Box(
                Modifier
                    .fillMaxSize()
                    .padding(top = 24.dp),
                contentAlignment = Alignment.Center
            ) {
                Text("Không tìm thấy bài đăng nào", color = MaterialTheme.colorScheme.onBackground)
            }
        } else {
            LazyColumn(
                contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(filteredPosts, key = { it.id }) { post -> // Dùng filteredPosts và thêm key
                    PostCard(
                        post = post,
                        currentUserId = currentUserId,
                        onClick = { nav.goPetDetail(post.id) },
                        onLikeClick = {
                            if (currentUserId.isNotBlank()) {
                                postViewModel.toggleLike(post.id, currentUserId)
                            }
                        },
                        onCommentClick = {
                            if (post.id.isNotBlank()) {
                                nav.navigate(Routes.comment(post.id))
                            } else {
                                Log.e("HomeScreen", "Attempted to open comments for post with blank id: $post")
                            }
                        },
                        onShareClick = { /*TODO*/ }
                    )
                }
            }
        }
    }
}
