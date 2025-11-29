package com.example.pawshearts.donate

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.pawshearts.R // M NHỚ IMPORT CÁI NÀY KKK

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BankDonateScreen(nav: NavHostController) {

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Thông tin quyên góp") },
                navigationIcon = {
                    IconButton(onClick = { nav.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Quay lại")
                    }
                },
                // DÙNG MÀU TỪ THEME
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface,
                    navigationIconContentColor = MaterialTheme.colorScheme.onSurface
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                // DÙNG MÀU TỪ THEME
                .background(MaterialTheme.colorScheme.background)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Text(
                "Quỹ Paw & Heart",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                // DÙNG MÀU TỪ THEME
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                "Cảm ơn M đã chung tay vì server và các bé KKK ❤️",
                modifier = Modifier.padding(bottom = 20.dp),
                // DÙNG MÀU TỪ THEME
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            // === 1. KHUNG QR MOMO/BANK KKK ===
            Card(
                modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp),
                shape = RoundedCornerShape(16.dp),
                // DÙNG MÀU TỪ THEME
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp).fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        "Bạn nào có lòng hảo tâm thì chuyển khoản thử nó ra không nhé-.-:)))",
                        fontWeight = FontWeight.SemiBold,
                        // DÙNG MÀU TỪ THEME
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    Image(
                        painter = painterResource(id = R.drawable.qr_momo), // <-- M PHẢI CÓ FILE NÀY
                        contentDescription = "Mã QR Momo",
                        modifier = Modifier.size(250.dp).clip(RoundedCornerShape(8.dp))
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        "Tên: Paw & Heart",
                        // DÙNG MÀU TỪ THEME
                        color = MaterialTheme.colorScheme.onSurface,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            // === 2. THÔNG TIN BANK CHI TIẾT KKK ===
            Text(
                "Hoặc chuyển khoản trực tiếp",
                modifier = Modifier.padding(vertical = 10.dp),
                fontWeight = FontWeight.Bold,
                // DÙNG MÀU TỪ THEME
                color = MaterialTheme.colorScheme.onBackground
            )

            BankDetailRow("Ngân hàng", "VPBank")
            BankDetailRow("Số tài khoản", "01MMTTT0065698677")
            BankDetailRow("Chủ tài khoản", "LE VAN KHOI")
            BankDetailRow("Nội dung", "QuyenGop")
        }
    }
}

@Composable
fun BankDetailRow(label: String, value: String) {
    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // DÙNG MÀU TỪ THEME
            Text(label, color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 14.sp)
            // DÙNG MÀU TỪ THEME
            Text(value, color = MaterialTheme.colorScheme.onSurface, fontWeight = FontWeight.Medium)
        }
        // DÙNG MÀU TỪ THEME
        Divider(thickness = 1.dp, color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
    }
}