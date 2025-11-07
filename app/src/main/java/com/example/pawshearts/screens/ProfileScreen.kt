// M ĐỂ Ý KỸ CÁI ĐÁM IMPORT NÈ
package com.example.pawshearts.screens

import android.net.Uri
import android.util.Log // T THÊM CÁI NÀY VÔ ĐỂ LOG CÁI AVATAR
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState // T THÊM CÁI NÀY
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll // T THÊM CÁI NÀY
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.*
import androidx.compose.runtime.*
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
import com.example.pawshearts.auth.AuthViewModel
import com.example.pawshearts.components.PostAdopt
import com.example.pawshearts.components.PostCard
import com.example.pawshearts.components.ProfileTopBar
import com.example.pawshearts.data.Adopt
import com.example.pawshearts.data.PetPost
import com.example.pawshearts.data.model.UserData // <-- CHỈ CÓ DUY NHẤT DÒNG NÀY LÀ CỦA USERDATA :@

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    nav: NavHostController,
    userData: UserData, // <-- Giờ nó hết đỏ rồi nè KKK
    outSignOut: () -> Unit,
    authViewModel: AuthViewModel // <-- LỖI 1: T ĐÃ XÓA = viewModel()
) {

    val user = authViewModel.currentUser // Lấy FirebaseUser (để dự phòng)

    // LỖI 2: M PHẢI XÀI 'userData' MÀ AppNav TRUYỀN VÔ :D
    val userName = userData.username ?: user?.displayName ?: "UserName"
    val userEmail = userData.email ?: user?.email ?: "NameEmail@gmail.com"
    val avatarUriString = userData.profilePictureUrl // Lấy URL ảnh từ 'userData'
    val address = userData.address ?: ""
    val phone = userData.phone ?: ""


    // (Mấy cái remember cho UI (Dialog, Tab) thì GIỮ NGUYÊN)
    var posts by remember { mutableStateOf(listOf<PetPost>()) } // (Tạm thời giữ)
    var adopts by remember { mutableStateOf(listOf<Adopt>()) } // (Tạm thời giữ)
    var selectedTab by remember { mutableStateOf(0) }
    var showEditDialog by remember { mutableStateOf(false) }
    var showEditPersonalDialog by remember { mutableStateOf(false) }

    // Tạm thời T giữ cái imagePicker
    val imagePicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            authViewModel.updateAvatar(uri) // Gọi hàm rỗng M thêm vô ViewModel
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            // T Thêm cái verticalScroll cho M lỡ M nhét nhiều bài post KKK
            .verticalScroll(rememberScrollState())
    ) {
        ProfileTopBar()

        // ====== THÔNG TIN NGƯT DÙNG ======
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp, bottom = 8.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp),
                contentAlignment = Alignment.Center
            ) {
                // Ảnh đại diện + nút thay ảnh
                Box(
                    modifier = Modifier.align(Alignment.Center),
                    contentAlignment = Alignment.BottomEnd
                ) {
                    Image(
                        painter = if (avatarUriString != null)
                            rememberAsyncImagePainter(avatarUriString) // <-- DÙNG URL TỪ 'userData'
                        else painterResource(id = R.drawable.avatardefault),
                        contentDescription = "Avatar",
                        modifier = Modifier
                            .size(100.dp)
                            .clip(CircleShape)
                            .border(2.dp, Color(0xFFE65100), CircleShape),
                        contentScale = ContentScale.Crop
                    )
                    IconButton(
                        onClick = { imagePicker.launch("image/*") },
                        modifier = Modifier
                            .size(28.dp)
                            .background(Color.White, CircleShape)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Đổi ảnh đại diện",
                            tint = Color(0xFFE65100)
                        )
                    }
                }

                // ✏️ Nút chỉnh sửa hồ sơ ở góc phải trên
                IconButton(
                    onClick = { showEditDialog = true },
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .size(30.dp)
                        .background(Color(0xFFFFF3E0), CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Chỉnh sửa hồ sơ",
                        tint = Color(0xFFE65100)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Thông tin cơ bản
            Text(
                text = userName, // <-- Xài biến đã sửa
                fontSize = 20.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.Black
            )
            Text(
                text = userEmail, // <-- Xài biến đã sửa
                fontSize = 14.sp,
                color = Color.Gray
            )
        }


        // ====== THÔNG TIN CÁ NHÂN ======
        Spacer(modifier = Modifier.height(8.dp))

        Card(
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Box(modifier = Modifier.fillMaxWidth()) {
                Column(
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Thông tin cá nhân", fontWeight = FontWeight.SemiBold, fontSize = 16.sp)

                        IconButton(
                            onClick = { showEditPersonalDialog = true },
                            modifier = Modifier
                                .size(28.dp)
                                .background(Color(0xFFFFF3E0), shape = CircleShape)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = "Chỉnh sửa",
                                tint = Color(0xFFE65100)
                            )
                        }
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Email,
                            contentDescription = null,
                            tint = Color(0xFFE65100),
                            modifier = Modifier.size(22.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Email: $userEmail") // <-- Xài biến đã sửa
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Phone,
                            contentDescription = null,
                            tint = Color(0xFFE65100),
                            modifier = Modifier.size(22.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("SĐT: ${if (phone.isBlank()) "..." else phone}") // <-- Xài biến đã sửa
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.LocationOn,
                            contentDescription = null,
                            tint = Color(0xFFE65100),
                            modifier = Modifier.size(22.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Địa chỉ: ${if (address.isBlank()) "..." else address}") // <-- Xài biến đã sửa
                    }
                }
            }
        }

        // LỖI 4: T THÊM NÚT ĐĂNG XUẤT CHO M :D
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = { outSignOut() }, // Gọi hàm lambda M đã truyền vào từ AppNav
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.errorContainer)
        ) {
            Text("Đăng xuất", color = MaterialTheme.colorScheme.onErrorContainer)
        }
        Spacer(modifier = Modifier.height(16.dp))


        // ====== HỘP THOẠI CHỈNH SỬA HỒ SƠ ======
        if (showEditDialog) {
            var newName by remember { mutableStateOf(userName) }
            var newEmail by remember { mutableStateOf(userEmail) }

            AlertDialog(
                onDismissRequest = { showEditDialog = false },
                title = { Text("✏️ Chỉnh sửa hồ sơ") },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = newName,
                            onValueChange = { newName = it },
                            label = { Text("Tên người dùng") },
                            singleLine = true
                        )
                        OutlinedTextField(
                            value = newEmail,
                            onValueChange = { newEmail = it },
                            label = { Text("Email") },
                            singleLine = true
                        )
                    }
                },
                confirmButton = {
                    TextButton(onClick = {
                        authViewModel.updateProfile(newName, newEmail) // <-- Gọi hàm M đã code
                        showEditDialog = false
                    }) { Text("Lưu") }
                },
                dismissButton = {
                    TextButton(onClick = { showEditDialog = false }) { Text("Hủy") }
                }
            )
        }

        // ====== HỘP THOẠI CHỈNH SỬA THÔNG TIN CÁ NHÂN ======
        if (showEditPersonalDialog) {
            var newEmail by remember { mutableStateOf(userEmail) }
            var newPhone by remember { mutableStateOf(phone) }
            var newAddress by remember { mutableStateOf(address) }

            AlertDialog(
                onDismissRequest = { showEditPersonalDialog = false },
                title = { Text("📋 Chỉnh sửa thông tin cá nhân") },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(value = newEmail, onValueChange = { newEmail = it }, label = { Text("Email (Tạm thời ko sửa đc)") }, readOnly = true)
                        OutlinedTextField(value = newPhone, onValueChange = { newPhone = it }, label = { Text("Số điện thoại") })
                        OutlinedTextField(value = newAddress, onValueChange = { newAddress = it }, label = { Text("Địa chỉ") })
                    }
                },
                confirmButton = {
                    TextButton(onClick = {
                        // LỖI 5: Hàm 'updateUserPersonalInfo' của M chỉ nhận phone và address
                        authViewModel.updateUserPersonalInfo(newPhone, newAddress)
                        showEditPersonalDialog = false
                    }) { Text("Lưu") }
                },
                dismissButton = {
                    TextButton(onClick = { showEditPersonalDialog = false }) { Text("Hủy") }
                }
            )
        }

        // ====== NÚT CHUYỂN TAB ======
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Button(
                onClick = { selectedTab = 0 },
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (selectedTab == 0) Color(0xFFE65100)
                    else MaterialTheme.colorScheme.surface
                ),
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    "Bài đăng",
                    color = if (selectedTab == 0) MaterialTheme.colorScheme.onPrimary
                    else MaterialTheme.colorScheme.onSurface
                )
            }

            Spacer(modifier = Modifier.width(4.dp))

            Button(
                onClick = { selectedTab = 1 },
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (selectedTab == 1) Color(0xFFE65100)
                    else MaterialTheme.colorScheme.surface
                ),
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    "Nhận nuôi",
                    color = if (selectedTab == 1) MaterialTheme.colorScheme.onPrimary
                    else MaterialTheme.colorScheme.onSurface
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // ====== TAB 1: BÀI ĐĂNG ======
        if (selectedTab == 0) {
            var showCreateDialog by remember { mutableStateOf(false) }

            Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                Button(
                    onClick = { showCreateDialog = true },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(45.dp)
                        .padding(bottom = 12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE65100)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("➕ Đăng bài mới", color = Color.White, fontSize = 16.sp)
                }

                // dùng items(posts) thay vì items(posts.size)
                LazyColumn(
                    // T Thêm cái này để LazyColumn nó tự tính chiều cao,
                    // chứ M lồng nó trong Column(verticalScroll) là nó crash á KKK
                    modifier = Modifier.heightIn(max = 500.dp), // M tự chỉnh chiều cao M muốn
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(posts) { petPost ->
                        PostCard(post = petPost, onClick = { /* mở chi tiết bài */ })
                    }
                }

                if (showCreateDialog) {
                    // ... (Code dialog đăng bài của M T giữ nguyên)
                }

            }
        }

        // ====== TAB 2: NHẬN NUÔI ======
        if (selectedTab == 1) {
            var showCreateDialog by remember { mutableStateOf(false) }

            Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                Button(
                    onClick = { showCreateDialog = true },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(45.dp)
                        .padding(bottom = 12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE65100)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("➕ Đăng nhận nuôi", color = Color.White, fontSize = 16.sp)
                }

                LazyColumn(
                    modifier = Modifier.heightIn(max = 500.dp), // Tương tự
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(adopts) { adopt ->
                        PostAdopt(post = adopt, onEditClick = { /* edit */ })
                    }
                }

                if (showCreateDialog) {
                    // ... (Code dialog nhận nuôi của M T giữ nguyên)
                }
            }
        }

        Spacer(modifier = Modifier.height(50.dp)) // Thêm tí đệm ở đít
    }
}