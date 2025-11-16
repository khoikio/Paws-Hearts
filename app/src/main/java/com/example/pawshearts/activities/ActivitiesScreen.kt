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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ActivitiesScreen(
    nav: NavHostController,
    authViewModel: AuthViewModel,
    activityViewModel: ActivityViewModel
) {
    LaunchedEffect(key1 = Unit) {
        Log.d("ActivitiesScreen", "Màn hình được hiển thị, đang refresh lại profile user...")
        authViewModel.refreshProfile() // TẠO HÀM NÀY TRONG VIEWMODEL
    }
    // Lấy profile của user đang đăng nhập để kiểm tra quyền admin
    val currentUserProfile by authViewModel.userProfile.collectAsStateWithLifecycle()

    // Lấy danh sách các hoạt động từ ViewModel
    val activities by activityViewModel.activities.collectAsStateWithLifecycle()

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
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp) // Tăng khoảng cách
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
                    ActivityCard(
                        activity = activity,
                        isAdmin = currentUserProfile?.isAdmin == true,
                        onDeleteClick = {
                            // Gọi hàm xóa từ ViewModel
                            activityViewModel.deleteActivity(activity.id)
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
    isAdmin: Boolean, // Thêm biến để biết có phải admin không
    onDeleteClick: () -> Unit // Thêm callback để xử lý khi bấm nút xóa
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
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
                        color = MaterialTheme.colorScheme.secondary
                    )
                )
                Spacer(Modifier.height(4.dp))
                Text("🗓️ ${activity.date}", style = MaterialTheme.typography.bodyMedium)
                Text("📍 ${activity.location}", style = MaterialTheme.typography.bodyMedium)
            }

            // --- PHÂN QUYỀN NÚT XÓA ---
            // Chỉ hiển thị nút xóa nếu là admin
            if (isAdmin) {
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
