package com.example.pawshearts.adopt.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Cake
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Male
import androidx.compose.material.icons.filled.MonitorWeight
import androidx.compose.material.icons.filled.Pets
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import coil.compose.rememberAsyncImagePainter
import com.example.pawshearts.R
import com.example.pawshearts.adopt.AdoptViewModel
import com.example.pawshearts.navmodel.Routes
import java.text.SimpleDateFormat
import java.util.Locale
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.graphics.vector.ImageVector

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PetDetailScreen(
    id: String,
    onBack: () -> Unit,
    nav: NavHostController,
    adoptViewModel: AdoptViewModel // Thêm ViewModel
) {
    val petDetail by adoptViewModel.adoptPostDetail.collectAsState()
    val OrangeColor = Color(0xFFE65100)
    val containerColor = MaterialTheme.colorScheme.surface

    LaunchedEffect(id) {
        adoptViewModel.fetchAdoptPostDetail(id)
    }

    DisposableEffect(Unit) {
        onDispose {
            adoptViewModel.resetAdoptPostDetail()
        }
    }

    // Xử lý khi loading
    if (petDetail == null) {
        Scaffold(
            topBar = { TopAppBar(
                title = { Text("Chi tiết thú cưng") },
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, "Quay lại") } },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = containerColor)
            )},
            containerColor = containerColor
        ) { paddingValues ->
            Box(modifier = Modifier.fillMaxSize().padding(paddingValues), contentAlignment = Alignment.Center) {
                CircularProgressIndicator() // Hiển thị vòng xoay loading
            }
        }
        return
    }

    // Sau khi đảm bảo petDetail không null, chúng ta vẫn cần xử lý các thuộc tính CỦA NÓ có thể null
    val pet = petDetail!! // Đảm bảo petDetail đã có dữ liệu

    val dateFormatter = remember { SimpleDateFormat("dd/M/yyyy", Locale.getDefault()) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Chi Tiết Thú Cưng",
                        color = OrangeColor,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Quay lại")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = containerColor)
            )
        },
        containerColor = containerColor
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
        ) {
            // --- 1. Ảnh thú cưng ---
            Image(
                // SỬA: Kiểm tra null an toàn cho pet.imageUrl
                painter = if (!pet.imageUrl.isNullOrEmpty()) rememberAsyncImagePainter(pet.imageUrl) else painterResource(id = R.drawable.avatardefault),
                contentDescription = pet.petName ?: "Hình ảnh thú cưng", // SỬA: Cung cấp contentDescription mặc định
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp),
                contentScale = ContentScale.Crop
            )

            // --- 2. Nội dung chi tiết ---
            Column(modifier = Modifier.padding(16.dp)) {

                // Tên và Trạng thái
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        pet.petName ?: "Tên không xác định", // SỬA: Cung cấp giá trị mặc định
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Card(
                        colors = CardDefaults.cardColors(containerColor = OrangeColor),
                        shape = RoundedCornerShape(4.dp)
                    ) {
                        // SỬA: Bạn có thể muốn kiểm tra trạng thái "Mới" thực tế từ dữ liệu
                        Text("Mới", color = Color.White, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp), fontSize = 12.sp)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Mô tả
                Text("Mô tả", fontWeight = FontWeight.SemiBold, fontSize = 18.sp)
                Text(pet.description ?: "Không có mô tả.", style = MaterialTheme.typography.bodyMedium) // SỬA: Cung cấp giá trị mặc định

                Spacer(modifier = Modifier.height(16.dp))

                // Thông tin chi tiết
                Text("Thông tin chi tiết", fontWeight = FontWeight.SemiBold, fontSize = 18.sp)
                Card(
                    modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.2f)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                            // SỬA: Cung cấp giá trị mặc định cho từng thuộc tính
                            DetailItem(icon = Icons.Default.Pets, label = "Giống", value = pet.petBreed ?: "Không rõ", modifier = Modifier.weight(1f))
                            DetailItem(icon = Icons.Default.Cake, label = "Tuổi", value = "${pet.petAge ?: 0} tháng", modifier = Modifier.weight(1f))
                        }
                        Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                            DetailItem(icon = Icons.Default.Male, label = "Giới tính", value = pet.petGender ?: "Không rõ", modifier = Modifier.weight(1f))
                            DetailItem(icon = Icons.Default.MonitorWeight, label = "Cân nặng", value = "${pet.petWeight ?: 0} kg", modifier = Modifier.weight(1f))
                        }
                        Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                            DetailItem(icon = Icons.Default.Favorite, label = "Sức khỏe", value = pet.petHealthStatus ?: "Chưa cập nhật", modifier = Modifier.weight(1f))
                            DetailItem(icon = Icons.Default.LocationOn, label = "Địa điểm", value = pet.petLocation ?: "Chưa xác định", modifier = Modifier.weight(1f))
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Điều kiện nhận nuôi
                Text("Điều kiện nhận nuôi", fontWeight = FontWeight.SemiBold, fontSize = 18.sp)
                Text(pet.adoptionRequirements ?: "Không có điều kiện nhận nuôi.", style = MaterialTheme.typography.bodyMedium) // SỬA: Cung cấp giá trị mặc định

                Spacer(modifier = Modifier.height(16.dp))

                // Ngày đăng
                val formattedDate = pet.createdAt?.toDate()?.let { dateFormatter.format(it) } ?: "Không rõ"
                Text("Ngày đăng: $formattedDate", style = MaterialTheme.typography.bodySmall, color = Color.Gray)

                Spacer(modifier = Modifier.height(24.dp))

                // --- 3. Thông tin người đăng ---
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = containerColor),
                    shape = RoundedCornerShape(16.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Thông tin người đăng", fontWeight = FontWeight.SemiBold, fontSize = 18.sp)
                        Spacer(modifier = Modifier.height(12.dp))

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Image(
                                // SỬA: Kiểm tra null an toàn cho pet.userAvatarUrl
                                painter = if (!pet.userAvatarUrl.isNullOrEmpty()) rememberAsyncImagePainter(pet.userAvatarUrl) else painterResource(id = R.drawable.avatardefault),
                                contentDescription = "Avatar",
                                modifier = Modifier
                                    .size(50.dp)
                                    .clip(CircleShape),
                                contentScale = ContentScale.Crop
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(pet.userName ?: "Người dùng ẩn danh", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium) // SỬA: Cung cấp giá trị mặc định
                            }

                            // Nút Xem Hồ sơ
                            TextButton(
                                onClick = {
                                    // SỬA: Chỉ điều hướng nếu userId không null
                                    pet.userId?.let { userId ->
                                        nav.navigate(Routes.userProfile(userId))
                                    } ?: run {
                                        // Xử lý khi userId là null, ví dụ: hiển thị Toast
                                        // Hoặc không làm gì cả nếu userId là bắt buộc
                                        // Ví dụ: Toast.makeText(context, "Không có thông tin người dùng", Toast.LENGTH_SHORT).show()
                                    }
                                },
                                colors = ButtonDefaults.textButtonColors(contentColor = OrangeColor)
                            ) {
                                Text("Xem hồ sơ", fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(16.dp)) // Padding dưới cùng
            }
        }
    }
}

@Composable
fun DetailItem(icon: ImageVector, label: String, value: String, modifier: Modifier = Modifier) {
    Row(modifier = modifier, verticalAlignment = Alignment.CenterVertically) {
        Icon(icon, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(20.dp))
        Spacer(modifier = Modifier.width(4.dp))
        Column {
            Text(label, style = MaterialTheme.typography.labelSmall, color = Color.Gray)
            Text(value, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold)
        }
    }
}
