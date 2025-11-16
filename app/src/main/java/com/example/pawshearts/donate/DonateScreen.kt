package com.example.pawshearts.donate

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.pawshearts.R
import com.example.pawshearts.navmodel.Routes

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DonateScreen(nav: NavHostController) {
    val uriHandler = LocalUriHandler.current
    val primaryColor = MaterialTheme.colorScheme.primary // Lấy màu primary một lần

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Quyên Góp & Hoạt Động", fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant,
                    titleContentColor = primaryColor
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(MaterialTheme.colorScheme.background)
                .padding(horizontal = 16.dp, vertical = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "🐾 Paw & Heart 💖",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = primaryColor
            )
            Text(
                "Chung tay vì các bé 🐾",
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(20.dp))

            DonateButton(
                iconResId = R.drawable.money,
                iconColor = primaryColor, // Dùng màu theme
                title = "Quyên góp tài chính",
                subtitle = "Duy trì server và quỹ cứu trợ",
                onClick = { nav.navigate(Routes.DONATE_BANK_SCREEN) }
            )

            DonateButton(
                iconResId = R.drawable.hoat_dong,
                iconColor = primaryColor, // Dùng màu theme
                title = "Hoạt động",
                subtitle = "Tham gia các chiến dịch, sự kiện",
                onClick = { nav.navigate(Routes.ACTIVITIES_LIST_SCREEN) }
            )
        }
    }
}

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
        // DÙNG MÀU TỪ THEME
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 20.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
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
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    title,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    // DÙNG MÀU TỪ THEME
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    subtitle,
                    fontSize = 14.sp,
                    // DÙNG MÀU TỪ THEME
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
