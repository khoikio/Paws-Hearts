package com.example.pawshearts.profile

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
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
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import coil.compose.rememberAsyncImagePainter
import com.example.pawshearts.R
import com.example.pawshearts.auth.AuthViewModel
import com.example.pawshearts.messages.model.createThreadId
import com.example.pawshearts.messages.model.generateThreadId
import com.example.pawshearts.navmodel.Routes

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    nav: NavHostController,
    authViewModel: AuthViewModel,
    profileViewModel: ProfileViewModel,
) {
    val userProfile by profileViewModel.userProfile.collectAsStateWithLifecycle()
    val currentUser = authViewModel.currentUser
    val myProfileData by authViewModel.userProfile.collectAsStateWithLifecycle()

    val isMyProfile = userProfile?.userId == currentUser?.uid
    var showEditDialog by remember { mutableStateOf(false) }

    // --- LOGIC "LẠC QUAN" CHO NÚT FOLLOW ---
    val isFollowing = myProfileData?.following?.contains(userProfile?.userId) == true


    val imagePicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let { authViewModel.updateAvatar(it) }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (isMyProfile) "Hồ sơ của tôi" else userProfile?.username ?: "Hồ sơ") },
                navigationIcon = {
                    if (!isMyProfile) {
                        IconButton(onClick = { nav.popBackStack() }) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                        }
                    }
                },
                actions = {
                    if (isMyProfile) {
                        IconButton(onClick = { nav.navigate(Routes.SETTINGS_SCREEN) }) {
                            Icon(Icons.Default.Settings, contentDescription = "Settings")
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
            )
        }
    ) { paddingValues ->
        userProfile?.let { userData ->
             Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Spacer(modifier = Modifier.height(16.dp))

                Image(
                    painter = if (userData.profilePictureUrl != null) rememberAsyncImagePainter(userData.profilePictureUrl) else painterResource(id = R.drawable.avatardefault),
                    contentDescription = "Avatar",
                    modifier = Modifier
                        .size(120.dp)
                        .clip(CircleShape)
                        .border(3.dp, MaterialTheme.colorScheme.primary, CircleShape)
                        .clickable {
                            if (isMyProfile) {
                                imagePicker.launch("image/*")
                            }
                        },
                    contentScale = ContentScale.Crop
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(text = userData.username ?: "Chưa cập nhật", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                Text(text = userData.email ?: "Chưa có email", style = MaterialTheme.typography.bodyMedium, color = Color.Gray)

                Spacer(modifier = Modifier.height(24.dp))

                 Row(
                     modifier = Modifier.fillMaxWidth(),
                     horizontalArrangement = Arrangement.SpaceEvenly,
                 ) {
                     ProfileStat("Theo dõi", userData.following.size.toString())
                     ProfileStat("Follower", userData.followers.size.toString())
                 }
                
                Spacer(modifier = Modifier.height(24.dp))

                if (isMyProfile) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("Thông tin cá nhân", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                                IconButton(onClick = { showEditDialog = true }) {
                                    Icon(Icons.Default.Edit, contentDescription = "Chỉnh sửa thông tin")
                                }
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("SĐT: ${userData.phone ?: "Chưa cập nhật"}")
                            Spacer(modifier = Modifier.height(4.dp))
                            Text("Địa chỉ: ${userData.address ?: "Chưa cập nhật"}")
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))
                    
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        FunctionButton(text = "Bài đăng", onClick = { nav.navigate(Routes.MY_POSTS_SCREEN) }, modifier = Modifier.weight(1f))
                        FunctionButton(text = "Nhận nuôi", onClick = { nav.navigate(Routes.ADOPT) }, modifier = Modifier.weight(1f))
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                    
                    Button(
                        onClick = { authViewModel.logoutAndNavigate(nav) },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.errorContainer)
                    ) {
                        Text("Đăng xuất", color = MaterialTheme.colorScheme.onErrorContainer)
                    }

                }else {
                    // userProfile = thằng đang được xem
                    // myProfileData = profile của user đang đăng nhập (current user)
                    val currentUserId = authViewModel.currentUser?.uid
                    val isFollowing = myProfileData?.following
                        ?.contains(userProfile?.userId ?: "") == true

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Button(
                            onClick = {
                                // 1. Cập nhật followers/following trên Firestore
                                profileViewModel.toggleFollow()
                                // 2. Refresh lại profile của current user để myProfileData.following đổi
                                authViewModel.refreshUserProfile()
                            },
                            modifier = Modifier.weight(1f),
                            colors = if (isFollowing) {
                                ButtonDefaults.buttonColors()
                            } else {

                                ButtonDefaults.outlinedButtonColors()
                            }
                        ) {
                            Text(if (!isFollowing) "Đang theo dõi" else "Theo dõi")
                        }

                        OutlinedButton(
                            onClick = {
                                val myId = authViewModel.currentUser?.uid ?: return@OutlinedButton
                                val otherId = userProfile?.userId ?: return@OutlinedButton

                                val threadId = createThreadId(myId, otherId)

                                nav.navigate("chat/$threadId")
                            },
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(Icons.Default.ChatBubbleOutline, contentDescription = null)
                            Spacer(Modifier.size(ButtonDefaults.IconSpacing))
                            Text("Nhắn tin")
                        }
                    }
                }
            }
        } ?: Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }

        if (showEditDialog && userProfile != null) {
            var newName by remember { mutableStateOf(userProfile!!.username ?: "") }
            var newPhone by remember { mutableStateOf(userProfile!!.phone ?: "") }
            var newAddress by remember { mutableStateOf(userProfile!!.address ?: "") }

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
                        authViewModel.updateProfile(newName, userProfile!!.email ?: "")
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
}

@Composable
private fun ProfileStat(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = value, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
        Text(text = label, style = MaterialTheme.typography.bodySmall, color = Color.Gray)
    }
}

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
