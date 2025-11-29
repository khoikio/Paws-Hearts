package com.example.pawshearts.activities

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.example.pawshearts.auth.AuthResult
import com.example.pawshearts.data.model.Activity

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateActivityScreen(
    nav: NavHostController,
    activityViewModel: ActivityViewModel,
    activityId: String?
) {
    // Xác định chế độ: Nếu có activityId thì là Sửa, không thì là Tạo
    val isEditMode = activityId != null
    // 1. T VỚI M TẠO STATE (BIẾN NHỚ) CHO CÁI FORM
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var date by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("") }
    var contactLink by remember { mutableStateOf("") }
    var imageUrl by remember { mutableStateOf("") }

    // Lấy chi tiết hoạt động cần sửa từ ViewModel
    val activityToEdit by activityViewModel.selectedActivity.collectAsStateWithLifecycle()

    // 2. XỬ LÝ LOGIC CHO CHẾ ĐỘ SỬA
    // Lấy dữ liệu về nếu là chế độ Sửa
    LaunchedEffect(activityId) {
        if (isEditMode) {
            activityViewModel.getActivityById(activityId!!)
        }
    }

    // Tự động điền dữ liệu vào form khi có dữ liệu để sửa
    LaunchedEffect(activityToEdit) {
        if (isEditMode && activityToEdit != null) {
            title = activityToEdit!!.title
            description = activityToEdit!!.description
            date = activityToEdit!!.date
            location = activityToEdit!!.location
            contactLink = activityToEdit!!.contactLink
            imageUrl = activityToEdit!!.imageUrl
        }
    }

    // Dọn dẹp state trong ViewModel khi thoát khỏi màn hình
    DisposableEffect(Unit) {
        onDispose {
            activityViewModel.clearSelectedActivity()
        }
    }

    // 2. T VỚI M "NGHE" KẾT QUẢ ĐĂNG BÀI TỪ VM
    val createResult by activityViewModel.createResult.collectAsState()
    var showLoading by remember { mutableStateOf(false) }
    var showErrorDialog by remember { mutableStateOf<String?>(null) }

    // 3. T VỚI M XỬ LÝ KẾT QUẢ
    LaunchedEffect(createResult) {
        when (createResult) {
            is AuthResult.Loading -> showLoading = true
            is AuthResult.Success -> {
                showLoading = false
                nav.popBackStack() // Đăng thành công -> T với M "Back"
                activityViewModel.resetCreateResult()
            }
            is AuthResult.Error -> {
                showLoading = false
                showErrorDialog = (createResult as AuthResult.Error).message
                activityViewModel.resetCreateResult()
            }
            null -> showLoading = false
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (isEditMode) "Chỉnh sửa Hoạt động" else "Tạo Hoạt động Mới") },
                navigationIcon = {
                    IconButton(onClick = { nav.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Quay lại")
                    }
                },
                actions = {
                    // NÚT "ĐĂNG" XỊN VCL KKK
                    TextButton(
                        onClick = {
                            if (!showLoading && title.isNotBlank() && description.isNotBlank()) {
                                // M GỌI HÀM VM M ƠI KKK
                                val newActivity = Activity(
                                    id = if (isEditMode) activityId!! else "", // Giữ lại ID cũ nếu là chế độ Sửa
                                    title = title,
                                    description = description,
                                    date = date,
                                    location = location,
                                    contactLink = contactLink,
                                            imageUrl = imageUrl
                                )
                                // Gọi hàm tương ứng với chế độ
                                if (isEditMode) {
                                    activityViewModel.updateActivity(newActivity)
                                } else {
                                    activityViewModel.createActivity(newActivity)
                                }
                            }
                        },
                        enabled = !showLoading && title.isNotBlank() // Đang tải/thiếu title thì M "mờ" nút đi
                    ) {
                        Text(
                            text = if (isEditMode) "LƯU" else "TẠO",
                            fontWeight = FontWeight.Bold,
                            color = if (showLoading || title.isBlank()) Color.Gray else MaterialTheme.colorScheme.primary
                        )
                    }
                },
                // DÙNG MÀU TỪ THEME
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant,
                    titleContentColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    navigationIconContentColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    actionIconContentColor = MaterialTheme.colorScheme.primary
                )
            )
        }
    ) { paddingValues ->

        Box(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
            // 4. CÁI FORM Nhập liệu
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState()),
            ) {
                Text(
                    "Thông tin Hoạt động:",
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(bottom = 10.dp),
                    color = MaterialTheme.colorScheme.onBackground
                )

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
                FormTextField(
                    value = imageUrl,
                    onValueChange = { imageUrl = it },
                    label = "URL Ảnh bìa của hoạt động",
                    keyboardType = KeyboardType.Uri
                )
            }

            // M SỬ DỤNG LẠI CÁI LOADING VÀ DIALOG T VỚI M CODE LÚC NÃY KKK
            if (showLoading) {
                // Thêm một lớp nền mờ để không bấm được bên dưới
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.5f))
                        .clickable(enabled = false, onClick = {}),
                    contentAlignment = Alignment.Center
                ) {
                    // DÙNG MÀU TỪ THEME
                    CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                }
            }

            if (showErrorDialog != null) {
                AlertDialog(
                    onDismissRequest = { showErrorDialog = null },
                    // DÙNG MÀU TỪ THEME
                    icon = { Icon(Icons.Default.Warning, contentDescription = null, tint = MaterialTheme.colorScheme.error) },
                    title = { Text("Đã có lỗi xảy ra") },
                    text = { Text(showErrorDialog ?: "Lỗi khôngxacscs định") },
                    confirmButton = {
                        TextButton(onClick = { showErrorDialog = null }) { Text("OK M") }
                    }
                )
            }
        }
    }
}
// Composable phụ cho các ô nhập liệu

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
        // DÙNG MÀU TỪ THEME
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = MaterialTheme.colorScheme.primary,
            focusedLabelColor = MaterialTheme.colorScheme.primary,
            cursorColor = MaterialTheme.colorScheme.primary
        )
    )
}
