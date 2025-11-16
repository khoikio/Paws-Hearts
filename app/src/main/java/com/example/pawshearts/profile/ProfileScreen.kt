package com.example.pawshearts.profile

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import coil.compose.rememberAsyncImagePainter
import com.example.pawshearts.R
import com.example.pawshearts.auth.AuthViewModel
import com.example.pawshearts.data.model.UserData
import com.example.pawshearts.navmodel.Routes
import com.example.pawshearts.post.PostViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    nav: NavHostController,
    userData: UserData,
    authViewModel: AuthViewModel,
    postViewModel: PostViewModel,
    onSettingsClick: () -> Unit
) {
    // --- STATE VÀ SETUP ---
    val userName = userData.username ?: "Chưa cập nhật"
    val userEmail = userData.email ?: "Chưa có email"
    val avatarUriString = userData.profilePictureUrl
    val address = userData.address ?: ""
    val phone = userData.phone ?: ""

    var showEditDialog by remember { mutableStateOf(false) }

    val imagePicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let { authViewModel.updateAvatar(it) }
    }

    // --- GIAO DIỆN ---
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Hồ sơ") },
                actions = {
                    IconButton(onClick = { onSettingsClick() }) {
                        Icon(imageVector = Icons.Default.Settings, contentDescription = "Cài đặt")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        // DÙNG MỘT COLUMN DUY NHẤT LÀM GỐC VÀ ÁP DỤNG PADDING
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues) // <-- ÁP DỤNG PADDING Ở ĐÂY
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Spacer(modifier = Modifier.height(16.dp))

            // AVATAR CÓ THỂ CLICK
            Image(
                painter = if (avatarUriString != null) rememberAsyncImagePainter(avatarUriString) else painterResource(id = R.drawable.avatardefault),
                contentDescription = "Avatar",
                modifier = Modifier
                    .size(120.dp)
                    .clip(CircleShape)
                    .border(3.dp, MaterialTheme.colorScheme.primary, CircleShape)
                    .clickable { imagePicker.launch("image/*") },
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.height(16.dp))

            // TÊN VÀ TAG ADMIN
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(text = userName, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                if (userData.isAdmin) {
                    Card(shape = RoundedCornerShape(8.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary) ){
                        Text("Admin", color = Color.White, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp), fontSize = 12.sp)
                    }
                }
            }
            Text(text = userEmail, style = MaterialTheme.typography.bodyLarge, color = Color.Gray)

            // THÔNG TIN CÁ NHÂN VÀ NÚT SỬA
            Spacer(modifier = Modifier.height(24.dp))
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text("Thông tin cá nhân", fontWeight = FontWeight.SemiBold, fontSize = 18.sp)
                    InfoRow(icon = Icons.Default.Phone, text = "SĐT: ${if (phone.isBlank()) "..." else phone}")
                    InfoRow(icon = Icons.Default.LocationOn, text = "Địa chỉ: ${if (address.isBlank()) "..." else address}")
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedButton(
                        onClick = { showEditDialog = true },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp),
                        border = BorderStroke(width = 1.dp, color = MaterialTheme.colorScheme.primary)
                    ) {
                        Text("Chỉnh sửa thông tin", color = MaterialTheme.colorScheme.onBackground)
                    }
                }
            }

            // CÁC NÚT CHỨC NĂNG
            Spacer(modifier = Modifier.height(24.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                FunctionButton(text = "Bài đăng", onClick = { nav.navigate(Routes.MY_POSTS_SCREEN) }, modifier = Modifier.weight(1f))
                FunctionButton(text = "Nhận nuôi", onClick = { nav.navigate(Routes.MY_ADOPT_POSTS_SCREEN) }, modifier = Modifier.weight(1f))
            }

            // NÚT ĐĂNG XUẤT
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = { authViewModel.logout() }, // CHỈ GỌI LOGOUT
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.errorContainer)
            ) {
                Text("Đăng xuất", color = MaterialTheme.colorScheme.onErrorContainer)
            }

            Spacer(modifier = Modifier.height(16.dp)) // Đệm dưới cùng
        }
    }

    // HỘP THOẠI (DIALOG) CHỈNH SỬA
    if (showEditDialog) {
        var newName by remember { mutableStateOf(userName) }
        var newPhone by remember { mutableStateOf(phone) }
        var newAddress by remember { mutableStateOf(address) }

        AlertDialog(
            onDismissRequest = { showEditDialog = false },
            title = { Text("Chỉnh sửa thông tin") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(value = newName, onValueChange = { newName = it }, label = { Text("Tên hiển thị") })
                    OutlinedTextField(value = newPhone, onValueChange = { newPhone = it }, label = { Text("Số điện thoại") })
                    OutlinedTextField(value = newAddress, onValueChange = { newAddress = it }, label = { Text("Địa chỉ") })
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    authViewModel.updateProfile(newName, userData.email ?: "")
                    authViewModel.updateUserPersonalInfo(newPhone, newAddress)
                    showEditDialog = false
                }) { Text("Lưu") }
            },
            dismissButton = {
                TextButton(onClick = { showEditDialog = false }) { Text("Hủy") }
            }
        )
    }
}

// Composable phụ để code gọn hơn
@Composable
private fun InfoRow(icon: androidx.compose.ui.graphics.vector.ImageVector, text: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(icon, contentDescription = null, tint =  MaterialTheme.colorScheme.primary, modifier = Modifier.size(22.dp))
        Spacer(modifier = Modifier.width(12.dp))
        Text(text)
    }
}

// Composable phụ cho các nút chức năng
@Composable
private fun FunctionButton(text: String, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(containerColor =  MaterialTheme.colorScheme.primary),
        modifier = modifier.height(48.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Text(text, color = Color.White)
    }
}
