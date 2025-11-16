package com.example.pawshearts.adopt

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import coil.compose.rememberAsyncImagePainter
import com.example.pawshearts.auth.AuthResult
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateAdoptPostScreen(
    nav: NavHostController,
    adoptViewModel: AdoptViewModel
) {
    // 1. T VỚI M TẠO STATE (BIẾN NHỚ) CHO CÁI FORM
    var petName by remember { mutableStateOf("") }
    var petBreed by remember { mutableStateOf("") }
    var petAge by remember { mutableStateOf("") } // Tuổi (String)
    var petWeight by remember { mutableStateOf("") } // Cân nặng (String)
    var petGender by remember { mutableStateOf("") }
    var petLocation by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var imageUri by remember { mutableStateOf<Uri?>(null) }

    // 2. LAUNCHER ĐỂ CHỌN ẢNH
    val imagePicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        imageUri = uri
    }

    // 3. T VỚI M "NGHE" KẾT QUẢ ĐĂNG BÀI TỪ VM
    val postResult by adoptViewModel.postResult.collectAsState()
    val scope = rememberCoroutineScope()
    var showLoading by remember { mutableStateOf(false) }
    var showErrorDialog by remember { mutableStateOf<String?>(null) }

    // 4. T VỚI M XỬ LÝ KẾT QUẢ
    LaunchedEffect(postResult) {
        when (postResult) {
            is AuthResult.Loading -> showLoading = true
            is AuthResult.Success -> {
                showLoading = false
                nav.popBackStack() // Đăng thành công -> T với M "Back"
                adoptViewModel.resetPostResult()
            }
            is AuthResult.Error -> {
                showLoading = false
                showErrorDialog = (postResult as AuthResult.Error).message
                adoptViewModel.resetPostResult()
            }
            null -> showLoading = false
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Đăng tìm chủ :D") },
                navigationIcon = {
                    IconButton(onClick = { nav.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Quay lại")
                    }
                },
                actions = {
                    // NÚT "ĐĂNG" XỊN VCL KKK
                    TextButton(
                        onClick = {
                            if (!showLoading) { // Nếu đéo đang tải...
                                // M GỌI HÀM VM M ƠI KKK
                                adoptViewModel.createAdoptPost(
                                    petName, petBreed, petAge, petWeight,
                                    petGender, petLocation, description, imageUri
                                )
                            }
                        },
                        enabled = !showLoading // Đang tải thì M "mờ" nút đi
                    ) {
                        Text(
                            "ĐĂNG",
                            fontWeight = FontWeight.Bold,
                            color = if (showLoading) Color.Gray else MaterialTheme.colorScheme.primary                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant, // <-- Sửa ở đây
                    titleContentColor = MaterialTheme.colorScheme.onSurfaceVariant, // <-- Sửa ở đây
                    navigationIconContentColor = MaterialTheme.colorScheme.onSurfaceVariant, // <-- Sửa ở đây
                    actionIconContentColor = MaterialTheme.colorScheme.primary // <-- Sửa ở đây
                )
            )
        }
    ) { paddingValues ->

        // 5. CÁI FORM KKK
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Ô CHỌN ẢNH
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .border(
                        1.dp,
                        MaterialTheme.colorScheme.outline.copy(alpha = 0.5f), // <-- Sửa ở đây
                        RoundedCornerShape(12.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                if (imageUri == null) {
                    TextButton(onClick = { imagePicker.launch("image/*") }) {
                        Text("🖼️ Chọn ảnh pet KKK")
                    }
                } else {
                    Image(
                        painter = rememberAsyncImagePainter(imageUri),
                        contentDescription = "Ảnh pet",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // MẤY CÁI Ô NHẬP LIỆU
            FormTextField(
                value = petName,
                onValueChange = { petName = it },
                label = "Tên thú cưng"
            )
            FormTextField(
                value = petBreed,
                onValueChange = { petBreed = it },
                label = "Giống"
            )
            FormTextField(
                value = petAge,
                onValueChange = { petAge = it },
                label = "Tuổi (tháng)",
                keyboardType = KeyboardType.Number
            )
            FormTextField(
                value = petWeight,
                onValueChange = { petWeight = it },
                label = "Cân nặng (kg)",
                keyboardType = KeyboardType.Number
            )
            FormTextField(
                value = petGender,
                onValueChange = { petGender = it },
                label = "Giới tính (Đực/Cái)"
            )
            FormTextField(
                value = petLocation,
                onValueChange = { petLocation = it },
                label = "Khu vực (Quận/Thành phố)"
            )
            FormTextField(
                value = description,
                onValueChange = { description = it },
                label = "Mô tả (Tính cách, tình trạng...)",
                modifier = Modifier.height(120.dp),
                singleLine = false
            )
        }

        // 6. CÁI LOADING VÀ DIALOG BÁO LỖI
        if (showLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.3f)),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = Color(0xFFE65100))
            }
        }

        if (showErrorDialog != null) {
            AlertDialog(
                onDismissRequest = { showErrorDialog = null },
                icon = { Icon(Icons.Default.Warning, contentDescription = null, tint = Color.Red) },
                title = { Text("Lỗi vcl M ơi :@") },
                text = { Text(showErrorDialog ?: "Lỗi đéo biết KKK") },
                confirmButton = {
                    TextButton(onClick = { showErrorDialog = null }) { Text("OK M") }
                }
            )
        }
    }
}

// T TÁCH CÁI TEXTFIELD RA CHO GỌN KKK
@Composable
fun FormTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    singleLine: Boolean = true,
    keyboardType: KeyboardType = KeyboardType.Text
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        modifier = modifier
            .fillMaxWidth()
            .padding(bottom = 10.dp),
        singleLine = singleLine,
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = Color(0xFFE65100),
            focusedLabelColor = Color(0xFFE65100)
        )
    )
}