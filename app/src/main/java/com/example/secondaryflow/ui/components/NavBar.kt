package com.example.secondaryflow.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.draw.clip
import androidx.compose.foundation.shape.RoundedCornerShape
import com.example.secondaryflow.ui.screens.ProfileScreen
import com.example.secondaryflow.ui.screens.DonateScreen
import com.example.secondaryflow.ui.screens.HomeScreen   // ✅ Import màn Trang chủ thật

@Composable
fun BottomNavigationBarExample() {
    var selectedItem by remember { mutableStateOf(0) } // ✅ Mặc định Trang chủ

    val items = listOf(
        NavItem("Trang chủ", Icons.Default.Home),
        NavItem("Quyên góp", Icons.Default.Favorite),
        NavItem("Nhận nuôi", Icons.Default.Pets),
        NavItem("Hồ sơ", Icons.Default.Person)
    )

    Box(modifier = Modifier.fillMaxSize()) {

        // 👉 Nội dung hiển thị thay đổi theo tab
        when (selectedItem) {
            0 -> HomeScreen() // ✅ Trang chủ thật (hiển thị danh sách thú cưng)
            1 -> DonateScreen() // ✅ Quyên góp
            2 -> Text("Nhận nuôi", modifier = Modifier.align(Alignment.Center))
            3 -> ProfileScreen() // ✅ Hồ sơ
        }

        // 👉 Thanh điều hướng ở dưới
        NavigationBar(
            containerColor = Color(0xFFFFE7D3),
            tonalElevation = 10.dp,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .height(100.dp)
                .clip(RoundedCornerShape(40.dp))
        ) {
            items.forEachIndexed { index, item ->
                NavigationBarItem(
                    selected = selectedItem == index,
                    onClick = { selectedItem = index },
                    icon = {
                        Icon(
                            imageVector = item.icon,
                            contentDescription = item.title,
                            tint = if (selectedItem == index) Color.White else Color.Black
                        )
                    },
                    label = {
                        Text(
                            text = item.title,
                            fontSize = 12.sp,
                            color = if (selectedItem == index) Color.White else Color.Black
                        )
                    },
                    colors = NavigationBarItemDefaults.colors(
                        indicatorColor = if (selectedItem == index)
                            Color(0xFFE65100)
                        else
                            Color.Transparent
                    )
                )
            }
        }
    }
}

data class NavItem(val title: String, val icon: androidx.compose.ui.graphics.vector.ImageVector)
