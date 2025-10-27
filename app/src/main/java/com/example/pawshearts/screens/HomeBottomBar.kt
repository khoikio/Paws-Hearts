package com.example.pawshearts.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Pets
import androidx.compose.material.icons.filled.Person
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun HomeBottomBar(selectedIndex: Int = 0, onItemSelected: (Int) -> Unit = {}) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFFFFF3E8))
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceAround,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Trang chủ
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .background(if (selectedIndex == 0) Color(0xFFEA5600) else Color.Transparent)
                .padding(horizontal = 12.dp, vertical = 4.dp)
                .clickable { onItemSelected(0) }
        ) {
            Icon(
                imageVector = Icons.Filled.Home,
                contentDescription = "Trang chủ",
                tint = if (selectedIndex == 0) Color.White else Color(0xFFEA5600)
            )
            Text(
                "Trang chủ",
                color = if (selectedIndex == 0) Color.White else Color(0xFFEA5600),
                style = MaterialTheme.typography.labelSmall
            )
        }
        // Quyên góp
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .clickable { onItemSelected(1) }
                .padding(horizontal = 10.dp, vertical = 4.dp)
        ) {
            Icon(
                imageVector = Icons.Filled.Favorite,
                contentDescription = "Quyên góp",
                tint = Color(0xFF444444)
            )
            Text("Quyên góp", color = Color(0xFF444444), style = MaterialTheme.typography.labelSmall)
        }
        // Nhận nuôi
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .clickable { onItemSelected(2) }
                .padding(horizontal = 10.dp, vertical = 4.dp)
        ) {
            Icon(
                imageVector = Icons.Filled.Pets,
                contentDescription = "Nhận nuôi",
                tint = Color(0xFF444444)
            )
            Text("Nhận nuôi", color = Color(0xFF444444), style = MaterialTheme.typography.labelSmall)
        }
        // Hồ sơ
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .clickable { onItemSelected(3) }
                .padding(horizontal = 10.dp, vertical = 4.dp)
        ) {
            Icon(
                imageVector = Icons.Filled.Person,
                contentDescription = "Hồ sơ",
                tint = Color(0xFF444444)
            )
            Text("Hồ sơ", color = Color(0xFF444444), style = MaterialTheme.typography.labelSmall)
        }
    }
}