package com.example.pawshearts.adopt.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Pets
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import coil.compose.rememberAsyncImagePainter
import com.example.pawshearts.R
import com.example.pawshearts.adopt.Adopt
import com.example.pawshearts.adopt.AdoptViewModel
import com.example.pawshearts.auth.AuthViewModel
import com.example.pawshearts.navmodel.Routes
import androidx.compose.material.icons.filled.Add


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdoptScreen(
    nav: NavHostController,
    adoptViewModel: AdoptViewModel,
    authViewModel: AuthViewModel,
) {

    val allAdoptPosts by adoptViewModel.allAdoptPosts.collectAsStateWithLifecycle()
    val filterState by adoptViewModel.filterState.collectAsStateWithLifecycle()

    val OrangeColor = Color(0xFFE65100)

    var searchText by remember { mutableStateOf("") }

    var showSpeciesDialog by remember { mutableStateOf(false) }
    var showAgeDialog by remember { mutableStateOf(false) }
    var showLocationDialog by remember { mutableStateOf(false) }


    Scaffold(
        topBar = {
            Column(modifier = Modifier.background(MaterialTheme.colorScheme.surface)) {
                CenterAlignedTopAppBar(
                    title = {
                        Text(
                            "Vòng Tay Yêu Thương",
                            fontWeight = FontWeight.Bold,
                            color = OrangeColor
                        )
                    },
                    actions = {
                        IconButton(
                            onClick = {
                                // Khi bấm vào icon, điều hướng đến trang tạo bài đăng mới
                                nav.navigate(Routes.CREATE_ADOPT_POST_SCREEN)
                            }
                        ) {
                            Icon(
                                Icons.Default.Add, // Icon dấu cộng
                                contentDescription = "Thêm bài đăng nhận nuôi",
                                tint = OrangeColor // Màu của icon
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
                )

                // --- Thanh Tìm kiếm ---
                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp).padding(bottom = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = searchText,
                        onValueChange = { searchText = it },
                        placeholder = { Text("Tìm kiếm tên, giống loài...") },
                        leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Tìm kiếm") },
                        modifier = Modifier.weight(1f),
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = OrangeColor,
                            unfocusedBorderColor = Color.LightGray
                        )
                    )
                }

                // --- Nút Lọc (Filter) - SỬ DỤNG OutlinedButton MỞ DIALOG ---
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .padding(bottom = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    // 1. NÚT LOÀI (Mở Dialog)
                    OutlinedButton(
                        onClick = { showSpeciesDialog = true },
                        colors = ButtonDefaults.outlinedButtonColors(
                            containerColor = if (filterState.species != null) OrangeColor else Color.Transparent,
                            // ĐÃ SỬA: Dùng onSurface để tự động thích ứng với Dark Mode
                            contentColor = if (filterState.species != null) Color.White else MaterialTheme.colorScheme.onSurface
                        ),
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 6.dp),
                        modifier = Modifier.weight(1f).padding(end = 4.dp)
                    ) {
                        Icon(Icons.Default.Pets, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(Modifier.width(4.dp))
                        Text(
                            text = filterState.species ?: "Loài",
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    // 2. NÚT ĐỘ TUỔI (Mở Dialog)
                    val isAgeFiltered = filterState.minAge != null || filterState.maxAge != null
                    OutlinedButton(
                        onClick = { showAgeDialog = true },
                        colors = ButtonDefaults.outlinedButtonColors(
                            containerColor = if (isAgeFiltered) OrangeColor else Color.Transparent,
                            // ĐÃ SỬA: Dùng onSurface để tự động thích ứng với Dark Mode
                            contentColor = if (isAgeFiltered) Color.White else MaterialTheme.colorScheme.onSurface
                        ),
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 6.dp),
                        modifier = Modifier.weight(1f).padding(horizontal = 4.dp)
                    ) {
                        Icon(Icons.Default.Star, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(Modifier.width(4.dp))
                        Text(
                            text = if (isAgeFiltered) "Đã lọc tuổi" else "Độ tuổi",
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    // 3. NÚT VỊ TRÍ (Mở Dialog)
                    OutlinedButton(
                        onClick = { showLocationDialog = true },
                        colors = ButtonDefaults.outlinedButtonColors(
                            containerColor = if (filterState.location != null) OrangeColor else Color.Transparent,
                            // ĐÃ SỬA: Dùng onSurface để tự động thích ứng với Dark Mode
                            contentColor = if (filterState.location != null) Color.White else MaterialTheme.colorScheme.onSurface
                        ),
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 6.dp),
                        modifier = Modifier.weight(1f).padding(start = 4.dp)
                    ) {
                        Icon(Icons.Default.LocationOn, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(Modifier.width(4.dp))
                        Text(
                            text = filterState.location ?: "Vị trí",
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
        },
                floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    nav.navigate(Routes.CREATE_POST_SCREEN) // Khi bấm, sẽ điều hướng đến màn hình tạo bài đăng mới
                },
                containerColor = MaterialTheme.colorScheme.primary, // Màu nền của nút
                contentColor = MaterialTheme.colorScheme.onPrimary // Màu của icon bên trong
            ) {
                Icon(Icons.Default.Add, contentDescription = "Đăng bài mới") // Icon dấu cộng
            }
        }
    ) { paddingValues ->

        val filteredPosts = if (searchText.isBlank()) {
            allAdoptPosts
        } else {
            allAdoptPosts.filter { it ->
                (it.petName?: "").contains(searchText, ignoreCase = true) ||
                        (it.petBreed?: "").contains(searchText, ignoreCase = true)
            }
        }

        if (filteredPosts.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize().padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Text("Không tìm thấy thú cưng nào phù hợp.", color = Color.Gray)
            }
            return@Scaffold
        }

        // --- LOGIC GỌI DIALOGS ---

        if (showSpeciesDialog) {
            TextFilterDialog(
                title = "Tìm kiếm Loài",
                label = "Loài (ví dụ: Mèo Anh, Golden Retriever)",
                currentValue = filterState.species ?: "",
                onDismiss = { showSpeciesDialog = false },
                onApply = { newSpecies ->
                    adoptViewModel.updateFilter(species = newSpecies.trim().ifEmpty { null })
                    showSpeciesDialog = false
                }
            )
        }

        if (showAgeDialog) {
            AgeFilterDialog(
                minAge = filterState.minAge,
                maxAge = filterState.maxAge,
                onDismiss = { showAgeDialog = false },
                onApply = { newMin, newMax ->
                    adoptViewModel.updateFilter(minAge = newMin, maxAge = newMax)
                    showAgeDialog = false
                }
            )
        }

        if (showLocationDialog) {
            TextFilterDialog(
                title = "Tìm kiếm Vị trí",
                label = "Vị trí (ví dụ: TP.HCM, Hà Nội)",
                currentValue = filterState.location ?: "",
                onDismiss = { showLocationDialog = false },
                onApply = { newLocation ->
                    adoptViewModel.updateFilter(location = newLocation.trim().ifEmpty { null })
                    showLocationDialog = false
                }
            )
        }

        // --- Giao diện Lưới (Grid) ---
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(MaterialTheme.colorScheme.background)
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(vertical = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(filteredPosts) { adoptPost ->
                PetGridItem(
                    post = adoptPost,
                    onDetailClick = { postId ->
                        // ******************************************************
                        // 🔑 LOGIC CHUYỂN ĐẾN TRANG CHI TIẾT ĐÃ Ở ĐÂY RỒI!
                        // ******************************************************
                        nav.navigate("${Routes.PET_DETAIL_SCREEN}/${postId}")
                    }
                )
            }
        }
    }
}

// ==========================================================
// COMPONENT: PetGridItem
// ==========================================================

@Composable
fun PetGridItem(
    post: Adopt,
    onDetailClick: (String) -> Unit
) {
    val OrangeColor = Color(0xFFE65100)

    Card(
        shape = RoundedCornerShape(12.dp),
        // Sử dụng màu nền Surface để thích ứng với Dark Mode
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        // ******************************************************
        // 🔑 CLICK CẢ CARD CŨNG DẪN ĐẾN TRANG CHI TIẾT
        // ******************************************************
        modifier = Modifier.fillMaxWidth().clickable { onDetailClick(post.id ?:"") }
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {

            // 1. HÌNH ẢNH PET
            val painter = if (post.imageUrl != null && post.imageUrl.isNotEmpty())
                rememberAsyncImagePainter(post.imageUrl)
            else
                painterResource(id = R.drawable.avatardefault)

            Image(
                painter = painter,
                contentDescription = post.petName,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp)
                    .clip(RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp)),
                contentScale = ContentScale.Crop
            )

            // 2. THÔNG TIN
            Column(modifier = Modifier.padding(8.dp)) {
                // Tên Pet
                Text(
                    text = post.petName ?:" Tên thú cưng không rõ",
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                // Tuổi và Giống
                Text(
                    text = "${post.petAge} tháng, ${post.petBreed}",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                // Mô tả ngắn
                Text(
                    text = post.description ?:"Không có mô tả",
                    style = MaterialTheme.typography.bodySmall,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.heightIn(min = 30.dp)
                )

                Spacer(modifier = Modifier.height(8.dp))

                // 3. NÚT TÌM HIỂU THÊM (TRÊN CODE LÀ 'Chi tiết')
                Button(
                    // ******************************************************
                    // 🔑 CLICK NÚT NÀY CŨNG DẪN ĐẾN TRANG CHI TIẾT
                    // ******************************************************
                    onClick = { onDetailClick(post.id ?:"") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = OrangeColor),
                    contentPadding = PaddingValues(vertical = 4.dp)
                ) {
                    Text("Chi tiết", style = MaterialTheme.typography.labelMedium, color = Color.White)
                }
            }
        }
    }
}

// ==========================================================
// COMPONENT: TextFilterDialog
// ==========================================================

@Composable
fun TextFilterDialog(
    title: String,
    label: String,
    currentValue: String,
    onDismiss: () -> Unit,
    onApply: (String) -> Unit
) {
    var text by rememberSaveable { mutableStateOf(currentValue) }
    val OrangeColor = Color(0xFFE65100)

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title) },
        text = {
            OutlinedTextField(
                value = text,
                onValueChange = { text = it },
                label = { Text(label) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
        },
        confirmButton = {
            Button(
                onClick = { onApply(text) },
                colors = ButtonDefaults.buttonColors(containerColor = OrangeColor)
            ) {
                Text("Áp dụng", color = Color.White)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Hủy")
            }
        }
    )
}

// ==========================================================
// COMPONENT: AgeFilterDialog
// ==========================================================

@Composable
fun AgeFilterDialog(
    minAge: Int?,
    maxAge: Int?,
    onDismiss: () -> Unit,
    onApply: (Int?, Int?) -> Unit
) {
    var minAgeText by rememberSaveable { mutableStateOf(minAge?.toString() ?: "") }
    var maxAgeText by rememberSaveable { mutableStateOf(maxAge?.toString() ?: "") }
    val OrangeColor = Color(0xFFE65100)

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Lọc Độ tuổi (tháng)") },
        text = {
            Column {
                OutlinedTextField(
                    value = minAgeText,
                    onValueChange = { minAgeText = it.filter { char -> char.isDigit() } },
                    label = { Text("Từ (tháng)") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(8.dp))
                OutlinedTextField(
                    value = maxAgeText,
                    onValueChange = { maxAgeText = it.filter { char -> char.isDigit() } },
                    label = { Text("Đến (tháng)") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val newMin = minAgeText.toIntOrNull()
                    val newMax = maxAgeText.toIntOrNull()
                    onApply(newMin, newMax)
                },
                colors = ButtonDefaults.buttonColors(containerColor = OrangeColor)
            ) {
                Text("Áp dụng", color = Color.White)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Hủy")
            }
        }
    )
}