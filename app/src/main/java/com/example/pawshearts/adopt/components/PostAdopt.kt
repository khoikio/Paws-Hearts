package com.example.pawshearts.adopt.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.pawshearts.adopt.Adopt

// Imports cho UI
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.Comment
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Share
import com.example.pawshearts.adopt.AdoptViewModel.AdoptPostUI
import com.example.pawshearts.adopt.AdoptViewModel
import android.content.Context
import androidx.navigation.NavHostController
import android.content.Intent
import androidx.compose.ui.Alignment
import androidx.compose.ui.platform.LocalContext
import androidx.compose.foundation.clickable
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.pawshearts.navmodel.Routes // Cần import Routes để navigate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PostAdopt(
    postUI: AdoptPostUI,
    adoptViewModel: AdoptViewModel,
    navController: NavHostController,
    onEditClick: () -> Unit,
    post: Adopt // Dữ liệu thô (có thể không cần nếu dùng postUI.adopt)
) {
    val context = LocalContext.current
    val adoptPost = postUI.adopt

    Card( /* ... */ ) {
        Column {

            // TODO: PHẦN HIỂN THỊ THÔNG TIN USER VÀ MÔ TẢ

            // --- HÀNG NÚT TƯƠNG TÁC ---
            Divider(color = Color(0xFFF0F0F0), thickness = 1.dp)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // 1. NÚT TIM (LIKE/FAVORITE)
                InteractionButton(
                    icon = if (postUI.isLiked) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                    label = adoptPost.likeCount.toString(),
                    tint = if (postUI.isLiked) Color.Red else Color.Gray,
                    onClick = {
                        adoptViewModel.toggleLike(adoptPost.id)
                    }
                )

                // 2. NÚT BÌNH LUẬN (COMMENT)
                InteractionButton(
                    icon = Icons.Outlined.Comment,
                    label = adoptPost.commentCount.toString(),
                    tint = Color.Gray,
                    onClick = {
                        navController.navigate("${Routes.COMMENT_SCREEN}/${adoptPost.id}")
                    }
                )

                // 3. NÚT CHIA SẺ (SHARE)
                InteractionButton(
                    icon = Icons.Outlined.Share,
                    label = "Chia sẻ",
                    tint = Color.Gray,
                    onClick = {
                        val shareContent = adoptViewModel.getShareableContent(adoptPost.id)
                        sharePostIntent(context, shareContent)
                    }
                )
            }
        }
    }
}

// === CÁC HÀM HỖ TRỢ ===

@Composable
fun InteractionButton(
    icon: ImageVector,
    label: String,
    tint: Color,
    onClick: () -> Unit
) { /* ... */ }

fun sharePostIntent(context: Context, text: String) { /* ... */ }