package com.example.pawshearts.activities

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.example.pawshearts.auth.AuthViewModel
import com.example.pawshearts.navmodel.Routes
import com.example.pawshearts.data.model.Activity
import androidx.compose.foundation.clickable
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ActivitiesScreen(
    nav: NavHostController,
    authViewModel: AuthViewModel,
    activityViewModel: ActivityViewModel
) {
    LaunchedEffect(key1 = Unit) {
        Log.d("ActivitiesScreen", "Màn hình được hiển thị, đang refresh lại profile user...")
        authViewModel.refreshUserProfile() // TẠO HÀM NÀY TRONG VIEWMODEL
    }
    // Lấy danh sách các hoạt động từ ViewModel
    val activities by activityViewModel.activities.collectAsStateWithLifecycle()

    // Lấy profile của user đang đăng nhập để kiểm tra quyền admin
    // Tôi đổi tên biến để tránh xung đột với code cũ của bạn
    val currentUserProfile by authViewModel.userProfile.collectAsStateWithLifecycle()
    val isAdmin = currentUserProfile?.isAdmin ?: false


    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Danh sách Hoạt động") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant,
                    titleContentColor = MaterialTheme.colorScheme.primary,
                    navigationIconContentColor = MaterialTheme.colorScheme.primary
                ),
                navigationIcon = {
                    IconButton(onClick = { nav.navigateUp() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Quay lại"
                        )
                    }
                }
                // BỎ NÚT ADD Ở ĐÂY, CHUYỂN XUỐNG DÙNG FLOATINGACTIONBUTTON CHO ĐẸP
            )
        },
        // --- PHÂN QUYỀN NÚT TẠO HOẠT ĐỘNG ---
        floatingActionButton = {
            // Chỉ hiển thị nút này nếu user là admin
            if (currentUserProfile?.isAdmin == true) {
                FloatingActionButton(
                    onClick = { nav.navigate(Routes.CREATE_ACTIVITY_SCREEN) },
                    containerColor = MaterialTheme.colorScheme.primary
                ) {
                    Icon(Icons.Filled.Add, "Tạo hoạt động mới", tint =  MaterialTheme.colorScheme.onPrimary)
                }
            }
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(MaterialTheme.colorScheme.background)
                .padding(horizontal = 16.dp), // Chỉ padding ngang
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(vertical = 16.dp) // Thêm padding dọc cho content
        ) {
            if (activities.isEmpty()) {
                item {
                    Box(modifier = Modifier.fillParentMaxSize(), contentAlignment = Alignment.Center) {
                        Text("Chưa có hoạt động nào.", modifier = Modifier.padding(16.dp))
                    }
                }
            } else {
                items(activities, key = { it.id }) { activity -> // Dùng key để tối ưu hiệu suất
                    // --- TRUYỀN QUYỀN ADMIN VÀ HÀM XÓA VÀO CARD ---
                    ActivityCard( // <<== TÊN ĐÚNG LÀ "ActivityCard"
                        activity = activity,
                        isAdmin = isAdmin,
                        onDeleteClick = {
                            activityViewModel.deleteActivity(activity.id)
                        },
                        onCardClick = {
                            // Điều hướng đến màn hình chi tiết, truyền ID của hoạt động
                            nav.navigate("${Routes.ACTIVITY_DETAIL_SCREEN}/${activity.id}")
                        }
                    )
                }
            }
        }
    }
}
    @Composable
    fun ActivityCard(
        activity: Activity,
        isAdmin: Boolean,
        onDeleteClick: () -> Unit,
        onCardClick: () -> Unit // <<== THÊM HÀNH ĐỘNG BẤM VÀO CARD
    ) {
        Card(
            // *** LÀM CHO NGUYÊN CÁI CARD CÓ THỂ BẤM VÀO ĐƯỢC ***
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = onCardClick),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface), // Dùng màu từ theme
            elevation = CardDefaults.cardElevation(2.dp)
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Phần nội dung (chiếm hết không gian còn lại)
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = activity.title,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            color = MaterialTheme.colorScheme.onSurface // Dùng màu từ theme
                        )
                    )
                    Spacer(Modifier.height(4.dp))
                    // Dùng màu phụ cho các dòng text này
                    Text("🗓️ ${activity.date}", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text("📍 ${activity.location}", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }

                // Chỉ hiển thị nút xóa nếu là admin
                if (isAdmin) {
                    // Đặt IconButton trong một Box để nó không ảnh hưởng đến vị trí của text
                    Box(modifier = Modifier.padding(start = 8.dp)) {
                        IconButton(onClick = onDeleteClick) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "Xóa hoạt động",
                                tint = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                }
            }
        }
    }