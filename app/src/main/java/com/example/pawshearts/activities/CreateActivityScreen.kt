package com.example.pawshearts.activities

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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.pawshearts.auth.AuthResult
import com.example.pawshearts.data.model.Activity

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateActivityScreen(
    nav: NavHostController,
    activityViewModel: ActivityViewModel
) {
    // 1. T VỚI M TẠO STATE (BIẾN NHỚ) CHO CÁI FORM
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var date by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("") }
    var contactLink by remember { mutableStateOf("") }

    // 2. T VỚI M "NGHE" KẾT QUẢ ĐĂNG BÀI TỪ VM
    val createResult by activityViewModel.createResult.collectAsState()
    var showLoading by remember { mutableStateOf(false) }
    var showErrorDialog by remember { mutableStateOf<String?>(null) }

    // 3. T VỚI M XỬ LÝ KẾT QUẢ
    LaunchedEffect(createResult) {
        when (createResult) {
            is AuthResult.Loading -> showLoading = true

            // ✅ BỔ SUNG TRẠNG THÁI IDLE
            is AuthResult.Idle -> showLoading = false

            is AuthResult.Success -> {
                showLoading = false
                nav.popBackStack()
                activityViewModel.resetCreateResult()
            }
            is AuthResult.Error -> {
                showLoading = false
                showErrorDialog = (createResult as AuthResult.Error).message
                activityViewModel.resetCreateResult()
            }
            // ✅ Xử lý trường hợp null (khi biến StateFlow ban đầu là null)
            null -> showLoading = false
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Tạo Hoạt động Mới (Admin)") },
                navigationIcon = {
                    IconButton(onClick = { nav.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Quay lại")
                    }
                },
                actions = {
                    // NÚT "ĐĂNG" XỊN VCL KKK
                    TextButton(
                        onClick = {
                            if (!showLoading && title.isNotBlank() && description.isNotBlank()) {
                                // M GỌI HÀM VM M ƠI KKK
                                val newActivity = Activity(
                                    title = title,
                                    description = description,
                                    date = date,
                                    location = location,
                                    contactLink = contactLink
                                )
                                activityViewModel.createActivity(newActivity)
                            }
                        },
                        enabled = !showLoading && title.isNotBlank() // Đang tải/thiếu title thì M "mờ" nút đi
                    ) {
                        Text(
                            "TẠO",
                            fontWeight = FontWeight.Bold,
                            color = if (showLoading || title.isBlank()) Color.Gray else Color(0xFFE65100)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFFFFF3E0),
                    titleContentColor = Color.Black,
                    navigationIconContentColor = Color.Black,
                    actionIconContentColor = Color(0xFFE65100)
                )
            )
        }
    ) { paddingValues ->

        // 4. CÁI FORM KKK
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
        ) {
            Text("Thông tin Hoạt động:", fontWeight = FontWeight.SemiBold, modifier = Modifier.padding(bottom = 10.dp))

            FormTextField(
                value = title,
                onValueChange = { title = it },
                label = "Tiêu đề Hoạt động",
            )
            FormTextField(
                value = description,
                onValueChange = { description = it },
                label = "Mô tả chi tiết",
                modifier = Modifier.height(120.dp),
                singleLine = false
            )
            FormTextField(
                value = date,
                onValueChange = { date = it },
                label = "Ngày & Thời gian",
            )
            FormTextField(
                value = location,
                onValueChange = { location = it },
                label = "Địa điểm",
            )
            FormTextField(
                value = contactLink,
                onValueChange = { contactLink = it },
                label = "Link Đăng ký/Liên hệ",
                keyboardType = KeyboardType.Uri
            )
        }

        // M SỬ DỤNG LẠI CÁI LOADING VÀ DIALOG T VỚI M CODE LÚC NÃY KKK
        if (showLoading) {
            CircularProgressIndicator(color = Color(0xFFE65100), modifier = Modifier.fillMaxSize().wrapContentSize(
                Alignment.Center))
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

// T VỚI M PHẢI TẠO LẠI CÁI TEXTFIELD NÀY VÌ NÓ KHÔNG PHẢI COMPONENTS CHUNG
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