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
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import coil.compose.rememberAsyncImagePainter
import com.example.pawshearts.Utils.uriToFile
import com.example.pawshearts.auth.AuthResult
import com.example.pawshearts.auth.AuthViewModel
import com.example.pawshearts.auth.AuthViewModelFactory
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreatePostScreen(
    navController: NavHostController // M Bấm "Đăng" (Success) nó tự Back
) {
    val context = LocalContext.current.applicationContext as Application

    val postViewModel: PostViewModel = viewModel(factory = PostViewModelFactory(context)) // <-- HẾT LỖI
    val authViewModel: AuthViewModel = viewModel(factory = AuthViewModelFactory(context))

    // 2. LẤY DATA (Info của user)
    val userData by authViewModel.userProfile.collectAsStateWithLifecycle(null)
    val createPostState by postViewModel.createPostState.collectAsStateWithLifecycle()


    // 4. MẤY CÁI STATE  "BÊ" TỪ DIALOG QUA
    var name by remember { mutableStateOf("") }
    var breed by remember { mutableStateOf("") }
    var ageMonth by remember { mutableStateOf("") }
    var weightKg by remember { mutableStateOf("") }
    var gender by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("") }
    var desc by remember { mutableStateOf("") }
    var imgUri by remember { mutableStateOf<Uri?>(null) }

    val pickImage = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri -> if (uri != null) imgUri = uri }

    // 5. CÁI LAUNCHEDEFFECT T CŨNG "BÊ" QUA LUÔN
    // (Nó tự Back khi M đăng xong)
    LaunchedEffect(createPostState) {
        when (createPostState) {
            is AuthResult.Success -> {
                Log.d("CreatePostScreen", "Đăng bài thành công!")
                postViewModel.clearCreatePostState()
                navController.popBackStack() // <-- TỰ ĐỘNG BACK
            }
            is AuthResult.Error -> {
                Log.e("CreatePostScreen", "Lỗi đăng bài: ${(createPostState as AuthResult.Error).message}")
                postViewModel.clearCreatePostState() // T reset lỗi (M nên hiện Toast)
            }
            is AuthResult.Loading -> {
                Log.d("CreatePostScreen", "Đang đăng bài...")
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
                // NÚT "ĐĂNG" XỊN NÈ KKK
                actions = {
                    TextButton(
                        onClick = {
                            if (createPostState !is AuthResult.Loading && userData != null) {
                                val fileAnh: File? = if (imgUri != null) {
                                    uriToFile(imgUri!!, context) // Dùng hàm Utils
                                } else {
                                    null
                                }
                                postViewModel.createPost(
                                    userId = userData!!.userId, // Lấy ID xịn
                                    username = userData!!.username,
                                    userAvatarUrl = userData!!.profilePictureUrl,
                                    petName = name,
                                    petBreed = breed,
                                    petAge = ageMonth.toIntOrNull() ?: 0,
                                    petGender = gender,
                                    location = location,
                                    weightKg = weightKg.toDoubleOrNull() ?: 0.0,
                                    imageFile = fileAnh,
                                    description = desc
                                )
                            }
                        },
                        enabled = (createPostState !is AuthResult.Loading && userData != null)
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

        // 6. CÁI FORM T "BÊ" TỪ DIALOG QUA
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp, vertical = 24.dp)
                .verticalScroll(rememberScrollState()), // M cuộn tẹt ga
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
    }
}