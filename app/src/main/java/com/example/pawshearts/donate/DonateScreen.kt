package com.example.pawshearts.donate // <-- M check package xịn

// === M IMPORT MẤY CÁI NÀY VÔ KKK ===
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalUriHandler // Tí T với M mở link Momo KKK
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.pawshearts.R // M phải có 3 cái icon này trong drawable nha KKK
import com.example.pawshearts.navmodel.Routes

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DonateScreen(nav: NavHostController) {
    // Tí T với M xài cái này để mở link Momo/Google Form KKK
    val uriHandler = LocalUriHandler.current

    Scaffold(
        topBar = {
            // M GIỮ CÁI TOPBAR M NÓI NÈ KKK
            TopAppBar(
                title = {
                    Text(
                        "Quyên Góp & Hoạt Động", // T SỬA LẠI TÊN XỊN KKK
                        fontWeight = FontWeight.Bold
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFFFFF3E0), // Màu cam lợt M xài
                    titleContentColor = Color(0xFFE65100) // Màu cam đậm
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Color(0xFFFFFBF5)) // Màu nền kem
                .padding(horizontal = 16.dp, vertical = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp) // Cách nhau 16dp
        ) {

            // TÊN APP M
            Text(
                "Paw & Heart 💖",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFFE65100)
            )
            Text(
                "Chung tay vì các bé 🐾",
                fontSize = 16.sp,
                color = Color.Gray
            )

            Spacer(modifier = Modifier.height(20.dp))

            // === 1. NÚT "ĐỚP" (MOMO/BANK)  ===
            DonateButton(
                iconResId = R.drawable.money,
                iconColor = Color(0xFFE65100), // Màu hường Momo
                title = "Quyên góp tài chính",
                subtitle = "Duy trì server và quỹ cứu trợ",
                onClick = {
                    nav.navigate(Routes.DONATE_BANK_SCREEN)
                }
            )

            // === 2. NÚT M ĐỔI TÊN NÈ KKK ===
            DonateButton(
                iconResId = R.drawable.hoat_dong, // M TỰ THÊM ICON NÀY KKK
                iconColor = Color(0xFFE65100), // Màu xanh
                title = "Hoạt động",
                subtitle = "Tham gia các chiến dịch, sự kiện",
                onClick = {
                    nav.navigate(Routes.ACTIVITIES_LIST_SCREEN)
                }
            )

            // === T XÓA MẸ NÚT "VẬT PHẨM" M CHÊ "LỎ" RỒI KKK ===
        }
    }
}

// === T TÁCH CÁI NÚT XỊN RA ĐÂY KKK ===
@Composable
fun DonateButton(
    iconResId: Int,
    iconColor: Color,
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 20.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // CÁI ICON TRÒN KKK
            Box(
                modifier = Modifier
                    .size(50.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(iconColor.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(id = iconResId),
                    contentDescription = title,
                    tint = iconColor,
                    modifier = Modifier.size(28.dp)
                )
            }
            // CÁI CHỮ KKK
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    title,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    subtitle,
                    fontSize = 14.sp,
                    color = Color.Gray
                )
            }
        }
    }
}