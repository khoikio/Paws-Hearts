package com.example.pawshearts.screens

import android.app.Application
import android.net.Uri
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import coil.compose.rememberAsyncImagePainter
import com.example.pawshearts.R
import com.example.pawshearts.auth.AuthViewModel
import com.example.pawshearts.auth.AuthViewModelFactory
import com.example.pawshearts.components.PostAdopt
import com.example.pawshearts.components.PostCard
import com.example.pawshearts.components.ProfileTopBar
import com.example.pawshearts.data.Adopt
import com.example.pawshearts.data.model.UserData
import com.example.pawshearts.post.PostViewModel
import com.example.pawshearts.post.PostViewModelFactory
import androidx.compose.runtime.getValue
import com.example.pawshearts.goPetDetail
import com.example.pawshearts.navmodel.Routes

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    nav: NavHostController,
    userData: UserData,
    outSignOut: () -> Unit,
    authViewModel: AuthViewModel
) {

    val user = authViewModel.currentUser
    val userName = userData.username ?: user?.displayName ?: "UserName"
    val userEmail = userData.email ?: user?.email ?: "NameEmail@gmail.com"
    val avatarUriString = userData.profilePictureUrl
    val address = userData.address ?: ""
    val phone = userData.phone ?: ""

    // (T GIỮ MẤY CÁI STATE CẦN THIẾT)
    var adopts by remember { mutableStateOf(listOf<Adopt>()) }
    var selectedTab by remember { mutableStateOf(0) }
    var showEditDialog by remember { mutableStateOf(false) }
    var showEditPersonalDialog by remember { mutableStateOf(false) }

    // SỬA CÁCH M LẤY POSTVIEWMODEL (FIX LỖI CRASH KKK)
    // 1. M LẤY CONTEXT
    val context = LocalContext.current.applicationContext as Application
    // 2. M TRUYỀN CONTEXT VÔ CÁI FACTORY M VỪA SỬA
    val postViewModel: PostViewModel = viewModel(factory = PostViewModelFactory(context))

    // T THÊM LẠI CÁI imagePicker M CẦN (ĐỂ HẾT LỖI)
    val imagePicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            authViewModel.updateAvatar(uri) // <-- GIỜ NÓ GỌI HÀM XỊN RỒI
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState()) // M GIỮ CÁI NÀY
    ) {
        ProfileTopBar()

        // ====== THÔNG TIN NGƯỜI DÙNG ======
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
                            rememberAsyncImagePainter(avatarUriString)
                        else painterResource(id = R.drawable.avatardefault),
                        contentDescription = "Avatar",
                        modifier = Modifier
                            .size(100.dp)
                            .clip(CircleShape)
                            .border(2.dp, Color(0xFFE65100), CircleShape),
                        contentScale = ContentScale.Crop
                    )

                    // NÚT MỞ LẠI (HẾT LỖI 'imagePicker')
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
            Text(text = userName, fontSize = 20.sp, fontWeight = FontWeight.SemiBold, color = Color.Black)
            Text(text = userEmail, fontSize = 14.sp, color = Color.Gray)
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
                        Icon(Icons.Default.Email, contentDescription = null, tint = Color(0xFFE65100), modifier = Modifier.size(22.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Email: $userEmail")
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Phone, contentDescription = null, tint = Color(0xFFE65100), modifier = Modifier.size(22.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("SĐT: ${if (phone.isBlank()) "..." else phone}")
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.LocationOn, contentDescription = null, tint = Color(0xFFE65100), modifier = Modifier.size(22.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Địa chỉ: ${if (address.isBlank()) "..." else address}")
                    }
                }
            }
        }

        // ====== NÚT ĐĂNG XUẤT ======
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = { outSignOut() },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.errorContainer)
        ) {
            Text("Đăng xuất", color = MaterialTheme.colorScheme.onErrorContainer)
        }
        Spacer(modifier = Modifier.height(16.dp))


        // ====== MẤY CÁI HỘP THOẠI (DIALOG) CHỈNH SỬA ======
        if (showEditDialog) {
            var newName by remember { mutableStateOf(userName) }
            var newEmail by remember { mutableStateOf(userEmail) }
            AlertDialog(
                onDismissRequest = { showEditDialog = false },
                title = { Text("✏️ Chỉnh sửa hồ sơ") },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(value = newName, onValueChange = { newName = it }, label = { Text("Tên người dùng") }, singleLine = true)
                        OutlinedTextField(value = newEmail, onValueChange = { newEmail = it }, label = { Text("Email") }, singleLine = true)
                    }
                },
                confirmButton = {
                    TextButton(onClick = {
                        authViewModel.updateProfile(newName, newEmail)
                        showEditDialog = false
                    }) { Text("Lưu") }
                },
                dismissButton = {
                    TextButton(onClick = { showEditDialog = false }) { Text("Hủy") }
                }
            )
        }
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
                        authViewModel.updateUserPersonalInfo(newPhone, newAddress)
                        showEditPersonalDialog = false
                    }) { Text("Lưu") }
                },
                dismissButton = {
                    TextButton(onClick = { showEditPersonalDialog = false }) { Text("Hủy") }
                }
            )
        }

        // ====== NÚT CHUYỂN TAB vào các bài đăng ======
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Button(
                onClick = {nav.navigate(Routes.MY_POSTS_SCREEN) },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFE65100)
                ),
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    "Bài đăng",
                    color = MaterialTheme.colorScheme.onPrimary
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
                    modifier = Modifier.heightIn(max = 500.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(adopts) { adopt ->
                        PostAdopt(post = adopt, onEditClick = { /* edit */ })
                    }
                }

                if (showCreateDialog) {
                    // ... (M TỰ CODE CÁI TAB NHẬN NUÔI NÀY SAU NHA KKK :v)
                }
            }
        }

        Spacer(modifier = Modifier.height(50.dp)) // Thêm tí đệm ở đít
    }
}