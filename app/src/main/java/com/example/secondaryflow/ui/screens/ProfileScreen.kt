package com.example.secondaryflow.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.LocationOn
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
import coil.compose.rememberAsyncImagePainter
import com.example.secondaryflow.R
import com.example.secondaryflow.data.Post
import com.example.secondaryflow.data.Adopt
import com.example.secondaryflow.ui.components.PostCard
import com.example.secondaryflow.ui.components.PostAdopt
import com.example.secondaryflow.ui.components.ProfileTopBar
import androidx.compose.material.icons.filled.Camera

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen() {
    // ========== USER INFO ==========
    var userName by remember { mutableStateOf(" UserName") }
    var userEmail by remember { mutableStateOf("NameEmail@gmail.com") }

    var posts by remember { mutableStateOf(listOf<Post>()) }
    var adopts by remember { mutableStateOf(listOf<Adopt>()) }

    var avatarUri by remember { mutableStateOf<Uri?>(null) }
    val imagePicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri -> if (uri != null) avatarUri = uri }

    var selectedTab by remember { mutableStateOf(0) }
    var showEditDialog by remember { mutableStateOf(false) }

    // ===== Thông tin cá nhân =====
    var phone by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }
    var showEditPersonalDialog by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        ProfileTopBar()

        // ====== THÔNG TIN NGƯỜI DÙNG ======
        // ====== HỒ SƠ NGƯỜI DÙNG ======
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
                        painter = if (avatarUri != null)
                            rememberAsyncImagePainter(avatarUri)
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
                            imageVector = Icons.Default.Edit, // ✅ Sửa lại
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
                text = userName,
                fontSize = 20.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.Black
            )
            Text(
                text = userEmail,
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
                        Text("Email: $userEmail")
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Phone,
                            contentDescription = null,
                            tint = Color(0xFFE65100),
                            modifier = Modifier.size(22.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("SĐT: ${if (phone.isBlank()) "..." else phone}")
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.LocationOn,
                            contentDescription = null,
                            tint = Color(0xFFE65100),
                            modifier = Modifier.size(22.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Địa chỉ: ${if (address.isBlank()) "..." else address}")
                    }
                }
            }
        }


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
                        userName = newName
                        userEmail = newEmail
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
                        OutlinedTextField(value = newEmail, onValueChange = { newEmail = it }, label = { Text("Email") })
                        OutlinedTextField(value = newPhone, onValueChange = { newPhone = it }, label = { Text("Số điện thoại") })
                        OutlinedTextField(value = newAddress, onValueChange = { newAddress = it }, label = { Text("Địa chỉ") })
                    }
                },
                confirmButton = {
                    TextButton(onClick = {
                        userEmail = newEmail
                        phone = newPhone
                        address = newAddress
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

                LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    items(posts.size) { index ->
                        PostCard(post = posts[index], onEditClick = {})
                    }
                }

                if (showCreateDialog) {
                    var title by remember { mutableStateOf("") }
                    var desc by remember { mutableStateOf("") }
                    var imgUri by remember { mutableStateOf<Uri?>(null) }

                    val pickImage = rememberLauncherForActivityResult(
                        contract = ActivityResultContracts.GetContent()
                    ) { uri -> if (uri != null) imgUri = uri }

                    AlertDialog(
                        onDismissRequest = { showCreateDialog = false },
                        title = { Text("📝 Bài đăng mới") },
                        text = {
                            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                OutlinedTextField(value = title, onValueChange = { title = it }, label = { Text("Tiêu đề") })
                                OutlinedTextField(value = desc, onValueChange = { desc = it }, label = { Text("Mô tả") })
                                if (imgUri != null) {
                                    Image(
                                        painter = rememberAsyncImagePainter(imgUri),
                                        contentDescription = null,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(180.dp)
                                            .clip(RoundedCornerShape(8.dp)),
                                        contentScale = ContentScale.Crop
                                    )
                                }
                                OutlinedButton(onClick = { pickImage.launch("image/*") }, modifier = Modifier.fillMaxWidth()) {
                                    Text("Chọn ảnh")
                                }
                            }
                        },
                        confirmButton = {
                            TextButton(onClick = {
                                if (title.isNotBlank() && desc.isNotBlank()) {
                                    posts = listOf(
                                        Post(posts.size + 1, title, desc, imgUri?.toString() ?: "")
                                    ) + posts
                                    showCreateDialog = false
                                }
                            }) { Text("Đăng") }
                        },
                        dismissButton = {
                            TextButton(onClick = { showCreateDialog = false }) { Text("Hủy") }
                        }
                    )
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

                LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    items(adopts.size) { index ->
                        PostAdopt(post = adopts[index], onEditClick = {})
                    }
                }

                if (showCreateDialog) {
                    var name by remember { mutableStateOf("") }
                    var desc by remember { mutableStateOf("") }
                    var imgUri by remember { mutableStateOf<Uri?>(null) }

                    val pickImage = rememberLauncherForActivityResult(
                        contract = ActivityResultContracts.GetContent()
                    ) { uri -> if (uri != null) imgUri = uri }

                    AlertDialog(
                        onDismissRequest = { showCreateDialog = false },
                        title = { Text("🐾 Đăng bài nhận nuôi") },
                        text = {
                            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Tên thú cưng") })
                                OutlinedTextField(value = desc, onValueChange = { desc = it }, label = { Text("Mô tả") })
                                if (imgUri != null) {
                                    Image(
                                        painter = rememberAsyncImagePainter(imgUri),
                                        contentDescription = null,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(180.dp)
                                            .clip(RoundedCornerShape(8.dp)),
                                        contentScale = ContentScale.Crop
                                    )
                                }
                                OutlinedButton(onClick = { pickImage.launch("image/*") }, modifier = Modifier.fillMaxWidth()) {
                                    Text("Chọn ảnh")
                                }
                            }
                        },
                        confirmButton = {
                            TextButton(onClick = {
                                if (name.isNotBlank() && desc.isNotBlank()) {
                                    adopts = listOf(
                                        Adopt(adopts.size + 1, name, desc, imgUri?.toString() ?: "")
                                    ) + adopts
                                    showCreateDialog = false
                                }
                            }) { Text("Đăng") }
                        },
                        dismissButton = {
                            TextButton(onClick = { showCreateDialog = false }) { Text("Hủy") }
                        }
                    )
                }
            }
        }
    }
}
