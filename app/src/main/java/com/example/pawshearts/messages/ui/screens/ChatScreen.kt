package com.example.pawshearts.messages.ui.screens

import android.Manifest
import android.app.Application
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInHorizontally
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Send
import androidx.compose.material.icons.outlined.ArrowBackIosNew
import androidx.compose.material.icons.outlined.AttachFile
import androidx.compose.material.icons.rounded.CameraAlt
import androidx.compose.material.icons.rounded.Description
import androidx.compose.material.icons.rounded.Headphones
import androidx.compose.material.icons.rounded.Image
import androidx.compose.material.icons.rounded.LocationOn
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.example.pawshearts.R
import com.example.pawshearts.messages.model.ChatMessageUiModel
import com.example.pawshearts.messages.model.GLOBAL_THREAD_ID
import com.example.pawshearts.messages.model.MessageStatus
import com.example.pawshearts.messages.presentation.ChatViewModel
import com.example.pawshearts.messages.presentation.ChatViewModelFactory
import com.example.pawshearts.messages.ui.components.ChatOrange
import com.example.pawshearts.messages.ui.components.ChatOuterBackground
import com.google.android.gms.location.LocationServices
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(
    threadId: String,
    nav: NavHostController
) {
    val context = LocalContext.current
    val app = context.applicationContext as Application
    val currentUser = FirebaseAuth.getInstance().currentUser

    if (currentUser == null) {
        Box(
            modifier = Modifier.fillMaxSize().background(ChatOuterBackground),
            contentAlignment = Alignment.Center
        ) { Text("Bạn chưa đăng nhập") }
        return
    }

    val chatViewModel: ChatViewModel = viewModel(
        factory = ChatViewModelFactory(
            app = app,
            currentUserId = currentUser.uid,
            currentUserName = currentUser.displayName
        )
    )

    // --- State & Launchers ---
    var showSheet by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState()

    // 1. Launcher chọn Ảnh
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) {
            showSheet = false
            chatViewModel.sendImage(context, uri)
        }
    }

    // 2. Launcher Camera
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview()
    ) { bitmap ->
        if (bitmap != null) {
            showSheet = false
            Toast.makeText(context, "Tính năng đang phát triển (cần convert bitmap -> file)", Toast.LENGTH_SHORT).show()
        }
    }

    // 3. Launcher chọn Tài liệu (PDF, Word...) - MỚI
    val documentLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri ->
        if (uri != null) {
            showSheet = false
            chatViewModel.sendFile(context, uri) // Gọi hàm gửi file
        }
    }

    // 4. Launcher xin quyền & lấy Vị trí - MỚI
    val fusedLocationClient = remember { LocationServices.getFusedLocationProviderClient(context) }
    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            // Check lại quyền lần nữa cho chắc
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                    if (location != null) {
                        showSheet = false
                        chatViewModel.sendLocation(location.latitude, location.longitude)
                    } else {
                        Toast.makeText(context, "Không lấy được vị trí, hãy bật GPS", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        } else {
            Toast.makeText(context, "Cần quyền vị trí để gửi", Toast.LENGTH_SHORT).show()
        }
    }

    // --- Lifecycle & Effects ---
    DisposableEffect(Unit) { onDispose { chatViewModel.stopAllListeners() } }
    LaunchedEffect(threadId) { chatViewModel.loadThread(threadId) }

    val messages by chatViewModel.messages.collectAsState()
    val isTyping by chatViewModel.isTyping.collectAsState()
    val toastMessage by chatViewModel.toastMessage.collectAsState()
    val isSendDisabled by chatViewModel.isSendDisabled.collectAsState()
    val headerTitle by chatViewModel.headerTitle.collectAsState()
    val isGlobal = threadId == GLOBAL_THREAD_ID
    val headerAvatarRes = if (isGlobal) R.drawable.ic_app else R.drawable.avatardefault

    val listState = rememberLazyListState()
    val scope = rememberCoroutineScope()

    LaunchedEffect(toastMessage) {
        toastMessage?.let { msg ->
            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
            chatViewModel.clearToastMessage()
        }
    }

    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            scope.launch { listState.animateScrollToItem(messages.lastIndex) }
        }
    }

    // --- UI Content ---
    if (showSheet) {
        AttachmentBottomSheet(
            onDismiss = { showSheet = false },
            onCameraClick = { cameraLauncher.launch(null) },
            onGalleryClick = {
                galleryLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageAndVideo))
            },
            // 👇 Xử lý click Tài liệu
            onDocumentClick = {
                // Mở chọn file (PDF, Word, Text)
                documentLauncher.launch(arrayOf("application/pdf", "application/msword", "text/plain"))
            },
            // 👇 Xử lý click Vị trí
            onLocationClick = {
                locationPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
            },
            sheetState = sheetState
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .imePadding()
    ) {
        ChatHeader(
            title = headerTitle,
            avatarRes = headerAvatarRes,
            onBackClick = { nav.popBackStack() }
        )

        HorizontalDivider(color = Color(0xFFF0F0F0))

        Card(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                LazyColumn(
                    state = listState,
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 12.dp, vertical = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(items = messages, key = { it.id }) { msg ->
                        ChatBubble(message = msg)
                    }
                    item { Spacer(modifier = Modifier.height(8.dp)) }
                }

                AnimatedVisibility(visible = isTyping) {
                    Text(
                        text = "Đang nhập...",
                        modifier = Modifier.padding(start = 12.dp, bottom = 6.dp),
                        style = LocalTextStyle.current.copy(color = Color.Gray, fontSize = 12.sp)
                    )
                }

                ChatInputBar(
                    isDisabled = isSendDisabled,
                    onSend = { text ->
                        val trimmed = text.trim()
                        if (trimmed.isNotEmpty()) {
                            chatViewModel.sendMessage(trimmed)
                        }
                    },
                    onAttach = {
                        if (!isSendDisabled) {
                            showSheet = true
                        } else {
                            Toast.makeText(context, "Chờ phản hồi để gửi tệp", Toast.LENGTH_SHORT).show()
                        }
                    }
                )
            }
        }
    }
}

@Composable
private fun ChatHeader(
    title: String,
    avatarRes: Int,
    onBackClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(WindowInsets.statusBars.asPaddingValues()),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onBackClick) {
            Icon(Icons.Outlined.ArrowBackIosNew, contentDescription = "Back",
                tint = MaterialTheme.colorScheme.onBackground    )
        }
        Image(
            painter = painterResource(id = avatarRes),
            contentDescription = title,
            modifier = Modifier.size(40.dp).clip(CircleShape)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = title,
            color = MaterialTheme.colorScheme.onBackground,
            fontWeight = FontWeight.SemiBold,
            fontSize = 18.sp,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

// 👇 ChatBubble ĐÃ ĐƯỢC NÂNG CẤP (Text, Image, File, Location)
@Composable
private fun ChatBubble(message: ChatMessageUiModel) {
    val textColor = if (message.isMine) Color.White else MaterialTheme.colorScheme.onSurface
    val bubbleColor = if (message.isMine) ChatOrange else MaterialTheme.colorScheme.surfaceVariant
    val context = LocalContext.current

    AnimatedVisibility(
        visible = true,
        enter = fadeIn() + slideInHorizontally(initialOffsetX = { if (message.isMine) 200 else -200 })
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = if (message.isMine) Arrangement.End else Arrangement.Start
        ) {
            if (!message.isMine) {
                Image(
                    painter = painterResource(id = R.drawable.avatardefault),
                    contentDescription = null,
                    modifier = Modifier.size(28.dp).clip(CircleShape).align(Alignment.Bottom)
                )
                Spacer(Modifier.width(6.dp))
            }

            Column(
                horizontalAlignment = if (message.isMine) Alignment.End else Alignment.Start
            ) {
                Box(
                    modifier = Modifier
                        .widthIn(max = 260.dp)
                        .background(
                            bubbleColor,
                            RoundedCornerShape(
                                topStart = 20.dp, topEnd = 20.dp,
                                bottomStart = if (message.isMine) 20.dp else 4.dp,
                                bottomEnd = if (message.isMine) 4.dp else 20.dp
                            )
                        )
                        .padding(if (message.type == "image") 4.dp else 14.dp)
                ) {
                    Column {
                        // --- Xử lý hiển thị theo loại ---
                        when (message.type) {
                            "image" -> {
                                AsyncImage(
                                    model = message.text,
                                    contentDescription = "Sent image",
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .heightIn(max = 300.dp)
                                        .clip(RoundedCornerShape(16.dp)),
                                    contentScale = ContentScale.Crop,
                                    placeholder = painterResource(R.drawable.ic_app),
                                    error = painterResource(R.drawable.ic_app)
                                )
                            }
                            "file" -> {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.clickable {
                                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(message.text))
                                        try { context.startActivity(intent) } catch (e: Exception) {
                                            Toast.makeText(context, "Không mở được file", Toast.LENGTH_SHORT).show()
                                        }
                                    }
                                ) {
                                    Icon(Icons.Rounded.Description, contentDescription = null, tint = textColor)
                                    Spacer(Modifier.width(8.dp))
                                    Text(
                                        text = "File đính kèm",
                                        color = textColor,
                                        fontWeight = FontWeight.Bold,
                                        textDecoration = TextDecoration.Underline
                                    )
                                }
                            }
                            "location" -> {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.clickable {
                                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(message.text))
                                        try { context.startActivity(intent) } catch (e: Exception) {
                                            Toast.makeText(context, "Không mở được bản đồ", Toast.LENGTH_SHORT).show()
                                        }
                                    }
                                ) {
                                    Icon(Icons.Rounded.LocationOn, contentDescription = null, tint = if(message.isMine) Color.White else Color.Red)
                                    Spacer(Modifier.width(8.dp))
                                    Text(
                                        text = "Vị trí hiện tại",
                                        color = textColor,
                                        fontWeight = FontWeight.Bold,
                                        textDecoration = TextDecoration.Underline
                                    )
                                }
                            }
                            else -> { // Text thường
                                Text(message.text, color = textColor)
                            }
                        }

                        // Hiển thị trạng thái (Đã gửi/Đang gửi)
                        if (message.isMine && message.type != "image") {
                            Spacer(modifier = Modifier.height(4.dp))
                        }

                        if (message.isMine) {
                            val statusText = when (message.status) {
                                MessageStatus.SENDING -> "Đang gửi..."
                                MessageStatus.SENT -> "Đã gửi"
                                MessageStatus.FAILED -> "Gửi thất bại"
                                else -> ""
                            }
                            if (statusText.isNotEmpty()) {
                                Text(
                                    statusText,
                                    color = textColor.copy(alpha = 0.8f),
                                    style = MaterialTheme.typography.labelSmall,
                                    modifier = Modifier.padding(start = 2.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

// ChatInputBar và AttachmentBottomSheet giữ nguyên
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ChatInputBar(
    isDisabled: Boolean,
    onSend: (String) -> Unit,
    onAttach: () -> Unit
) {
    var text by remember { mutableStateOf("") }
    val canSend = text.isNotBlank() && !isDisabled

    Surface(tonalElevation = 4.dp, color = MaterialTheme.colorScheme.surface) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onAttach, enabled = !isDisabled) {
                Icon(
                    imageVector = Icons.Outlined.AttachFile,
                    contentDescription = "attach",
                    tint = if (isDisabled) Color.Gray else MaterialTheme.colorScheme.onSurface
                )
            }
            TextField(
                value = text,
                onValueChange = { text = it },
                enabled = !isDisabled,
                modifier = Modifier.weight(1f).height(52.dp),
                placeholder = {
                    Text(
                        if (isDisabled) "Đang chờ phản hồi..." else "Nhập tin nhắn...",
                        color = if (isDisabled) Color.Red.copy(alpha = 0.6f) else Color.Gray
                    )
                },
                singleLine = true,
                shape = RoundedCornerShape(24.dp),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                    unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                    disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                    focusedTextColor = MaterialTheme.colorScheme.onSurface,
                    unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    disabledIndicatorColor = Color.Transparent
                ),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
                keyboardActions = KeyboardActions(onSend = { if (canSend) { onSend(text); text = "" } })
            )
            Spacer(Modifier.width(8.dp))
            IconButton(
                onClick = { if (canSend) { onSend(text); text = "" } },
                enabled = canSend,
                modifier = Modifier.size(46.dp).clip(CircleShape).background(if (canSend) ChatOrange else Color(0xFFCCCCCC))
            ) {
                Icon(Icons.AutoMirrored.Outlined.Send, contentDescription = "send", tint = Color.White)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AttachmentBottomSheet(
    onDismiss: () -> Unit,
    onCameraClick: () -> Unit,
    onGalleryClick: () -> Unit,
    onDocumentClick: () -> Unit,
    onLocationClick: () -> Unit,
    sheetState: SheetState
) {
    val options = listOf(
        AttachmentOption("Tài liệu", Icons.Rounded.Description, Color(0xFF9C27B0), onDocumentClick),
        AttachmentOption("Camera", Icons.Rounded.CameraAlt, Color(0xFFE91E63), onCameraClick),
        AttachmentOption("Thư viện", Icons.Rounded.Image, Color(0xFF9C27B0), onGalleryClick),
        AttachmentOption("Âm thanh", Icons.Rounded.Headphones, Color(0xFFFF9800), {}),
        AttachmentOption("Vị trí", Icons.Rounded.LocationOn, Color(0xFF4CAF50), onLocationClick),
        AttachmentOption("Liên hệ", Icons.Rounded.Person, Color(0xFF2196F3), {})
    )

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.surface,
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(bottom = 40.dp)
        ) {
            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(24.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(options) { option -> AttachmentItem(option) }
            }
        }
    }
}

@Composable
fun AttachmentItem(option: AttachmentOption) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable { option.action() }
    ) {
        Box(
            modifier = Modifier.size(60.dp).background(option.color, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(imageVector = option.icon, contentDescription = option.title, tint = Color.White, modifier = Modifier.size(30.dp))
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = option.title, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurface)
    }
}

// Data class cho Menu
data class AttachmentOption(
    val title: String,
    val icon: ImageVector,
    val color: Color,
    val action: () -> Unit
)