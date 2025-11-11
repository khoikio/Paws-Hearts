package com.example.pawshearts.activities

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.pawshearts.auth.AuthViewModel // M SẼ CẦN CÁI NÀY
import com.example.pawshearts.navmodel.Routes
import com.example.pawshearts.data.model.Activity // KKK

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ActivitiesScreen(
    nav: NavHostController,
    authViewModel: AuthViewModel = viewModel(), // M LẤY VIEWMODEL RA ĐÂY KKK
    activityViewModel: ActivityViewModel = viewModel() // M TỰ TẠO CÁI NÀY NHA :v
) {
    val userData by authViewModel.userProfile.collectAsState(initial = null)
    val isAdmin = userData?.isAdmin ?: false
    // ------------------------------------

    // Tạm thời dùng data giả cho UI
    val activities = listOf(
        Activity(title = "Dọn dẹp Bãi biển", date = "20/12/2025", location = "Vũng Tàu"),
        Activity(title = "Hội chợ nhận nuôi", date = "01/01/2026", location = "TP.HCM"),
        Activity(title = "Tình nguyện viên nuôi dưỡng", date = "Liên tục", location = "Online")
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Danh sách Hoạt động", fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFFFFF3E0),
                    titleContentColor = Color(0xFFE65100)
                )
            )
        },
        // === 2. NÚT TẠO CHO ADMIN NÈ KKK ===
        floatingActionButton = {
            if (isAdmin) { // CHỈ HIỆN KHI LÀ ADMIN
                FloatingActionButton(
                    onClick = { nav.navigate(Routes.CREATE_ACTIVITY_SCREEN) },
                    containerColor = Color(0xFFE65100),
                    contentColor = Color.White
                ) {
                    Icon(Icons.Filled.Add, "Tạo Hoạt động")
                }
            }
        },
        // ===================================
        content = { paddingValues ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(top = 16.dp, bottom = 80.dp)
            ) {
                items(activities) { activity ->
                    ActivityCard(activity = activity) // M TỰ CODE CÁI NÀY NHA KKK
                }
            }
        }
    )
}

// TẠO THẺ HOẠT ĐỘNG GIẢ ĐỂ M TEST KKK
@Composable
fun ActivityCard(activity: Activity) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = activity.title,
                // M dùng style.copy để M set màu, font xịn KKK
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = Color(0xFFE65100)
                )
            )
            Spacer(Modifier.height(4.dp))
            Text("🗓️ ${activity.date}", style = MaterialTheme.typography.bodyMedium)
            Text("📍 ${activity.location}", style = MaterialTheme.typography.bodyMedium)
        }
    }
}