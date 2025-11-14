package com.example.pawshearts.post

import android.app.Application
import android.net.Uri
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Warning // Cần cho AlertDialog
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color // Cần cho AlertDialog
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import coil.compose.rememberAsyncImagePainter
import com.example.pawshearts.auth.AuthResult
import com.example.pawshearts.auth.AuthViewModel
import com.example.pawshearts.auth.AuthViewModelFactory
// ⚠️ Cần import PostViewModelFactory

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreatePostScreen(
    navController: NavHostController // M Bấm "Đăng" (Success) nó tự Back
) {
    val context = LocalContext.current.applicationContext as Application

    // 1. KHỞI TẠO VM (SỬA DÙNG PostViewModelFactory)
    val postViewModel: PostViewModel = viewModel(factory = PostViewModelFactory(context))
    val authViewModel: AuthViewModel = viewModel(factory = AuthViewModelFactory(context))

    // 2. LẤY DATA
    val userData by authViewModel.userProfile.collectAsStateWithLifecycle(null)
    // ⬇️ Tên biến này là createPostState
    val createPostState by postViewModel.createPostState.collectAsStateWithLifecycle(initialValue = AuthResult.Idle)


    // 3. STATE CHO FORM VÀ DIALOG
    var name by remember { mutableStateOf("") }
    var breed by remember { mutableStateOf("") }
    var ageMonth by remember { mutableStateOf("") }
    var weightKg by remember { mutableStateOf("") }
    var gender by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("") }
    var desc by remember { mutableStateOf("") }
    var imgUri by remember { mutableStateOf<Uri?>(null) }
    var showErrorDialog by remember { mutableStateOf<String?>(null) } // Thêm state cho dialog

    val pickImage = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri -> if (uri != null) imgUri = uri }

    // 4. XỬ LÝ KẾT QUẢ VÀ TỰ BACK (SỬA LỖI UNRESOLVED REFERENCE)
    LaunchedEffect(createPostState) {
        when (createPostState) {
            is AuthResult.Idle -> { /* Do nothing */ }

            is AuthResult.Loading -> { /* Do nothing, UI đã xử lý */ }

            is AuthResult.Success -> {
                // Đăng thành công -> Tự động Back
                navController.popBackStack()
                postViewModel.clearCreatePostState() // SỬA: Dùng postViewModel
            }
            is AuthResult.Error -> {
                // Hiển thị dialog lỗi
                showErrorDialog = (createPostState as AuthResult.Error).message
                postViewModel.clearCreatePostState() // SỬA: Dùng postViewModel
            }
            null -> {}
        }
    }


    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Đăng bài mới") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    TextButton(
                        onClick = {
                            if (createPostState !is AuthResult.Loading && userData != null) {
                                // ⚠️ Sửa: Dùng safe call và Elvis operator cho non-nullable fields
                                val currentData = userData
                                if (currentData != null) {
                                    postViewModel.createPost(
                                        userId = currentData.userId,
                                        username = currentData.username.orEmpty(),
                                        userAvatarUrl = currentData.profilePictureUrl,
                                        petName = name,
                                        petBreed = breed,
                                        petAge = ageMonth.toIntOrNull() ?: 0,
                                        petGender = gender,
                                        location = location,
                                        weightKg = weightKg.toDoubleOrNull() ?: 0.0,
                                        imageUri = imgUri,
                                        description = desc
                                    )
                                } else {
                                    showErrorDialog = "Dữ liệu người dùng chưa tải xong. Vui lòng thử lại."
                                }
                            }
                        },
                        // Chỉ cho phép đăng khi không Loading và có UserData
                        enabled = (createPostState !is AuthResult.Loading && userData != null && name.isNotBlank())
                    ) {
                        if (createPostState is AuthResult.Loading) {
                            CircularProgressIndicator(modifier = Modifier.size(24.dp))
                        } else {
                            Text("Đăng", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            )
        }
    ) { innerPadding ->

        // FORM
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp, vertical = 24.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Tên thú cưng") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = breed, onValueChange = { breed = it }, label = { Text("Giống") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = ageMonth, onValueChange = { ageMonth = it }, label = { Text("Tuổi (tháng)") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = weightKg, onValueChange = { weightKg = it }, label = { Text("Cân nặng (kg)") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = gender, onValueChange = { gender = it }, label = { Text("Giới tính") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = location, onValueChange = { location = it }, label = { Text("Khu vực") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = desc, onValueChange = { desc = it }, label = { Text("Mô tả") }, modifier = Modifier.fillMaxWidth(), minLines = 4)

            OutlinedButton(onClick = { pickImage.launch("image/*") }, modifier = Modifier.fillMaxWidth()) {
                Text(if (imgUri == null) "Chọn ảnh" else "Đổi ảnh khác")
            }
            if (imgUri != null) {
                Image(
                    painter = rememberAsyncImagePainter(imgUri),
                    contentDescription = "Ảnh M chọn",
                    modifier = Modifier.fillMaxWidth().height(250.dp).align(Alignment.CenterHorizontally),
                    contentScale = ContentScale.Crop
                )
            }
        }

        // DIALOG HIỂN THỊ LỖI
        if (showErrorDialog != null) {
            AlertDialog(
                onDismissRequest = { showErrorDialog = null },
                icon = { Icon(Icons.Default.Warning, contentDescription = null, tint = Color.Red) },
                title = { Text("Lỗi Đăng bài") },
                text = { Text(showErrorDialog ?: "Lỗi không xác định") },
                confirmButton = {
                    TextButton(onClick = { showErrorDialog = null }) { Text("OK") }
                }
            )
        }
    }
}

