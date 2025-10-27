// AppIcons.kt
package com.example.pawshearts.ui.theme

// Nếu bạn muốn dùng Pets từ material-icons-extended, thêm dependency và uncomment dòng dưới
// import androidx.compose.material.icons.filled.Pets
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Pets
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

sealed class NavItem(
    val route: String,
    val icon: ImageVector,
    val label: String
) {
    object Home : NavItem("home", Icons.Filled.Home, "Trang chủ")
    object Donate : NavItem("donate", Icons.Filled.Favorite, "Quyên góp")
    object Adopt : NavItem("adopt", Icons.Filled.Pets, "Nhận nuôi")
    object Profile : NavItem("profile", Icons.Filled.Person, "Hồ sơ")
}

@Composable
fun AppBottomNav(
    items: List<NavItem> = listOf(
        NavItem.Home, NavItem.Donate, NavItem.Adopt, NavItem.Profile
    ),
    activeRoutes: Set<String> = setOf(NavItem.Home.route),
    onItemSelected: (NavItem) -> Unit = {}
) {
    Surface(
        tonalElevation = 4.dp,
        shadowElevation = 4.dp,
        color = MaterialTheme.colorScheme.surface
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp, horizontal = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            items.forEach { item ->
                val isActive = activeRoutes.contains(item.route)
                BottomNavItem(
                    modifier = Modifier.weight(1f), // <-- gọi weight trên Modifier
                    item = item,
                    isActive = isActive,
                    onClick = { onItemSelected(item) }
                )
            }
        }
    }
}

@Composable
private fun BottomNavItem(
    modifier: Modifier = Modifier,
    item: NavItem,
    isActive: Boolean,
    onClick: () -> Unit
) {
    val activeBg = Color(0xFFEA5600)
    val inactiveIconTint = Color(0xFF909090)
    val activeIconTint = Color.White
    val labelColor = if (isActive) Color.White else Color(0xFF6B6B6B)

    Column(
        modifier = modifier
            .padding(horizontal = 6.dp)
            .height(64.dp)
            .clickable { onClick() },
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .width(72.dp)
                .height(36.dp)
                .background(
                    color = if (isActive) activeBg else Color.Transparent,
                    shape = RoundedCornerShape(10.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = item.icon,
                contentDescription = item.label,
                tint = if (isActive) activeIconTint else inactiveIconTint,
                modifier = Modifier.size(20.dp)
            )
        }

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = item.label,
            fontSize = 12.sp,
            fontWeight = if (isActive) FontWeight.SemiBold else FontWeight.Normal,
            color = labelColor
        )
    }
}

@Preview(showBackground = true, widthDp = 360)
@Composable
private fun PreviewBottomNavSingleActive() {
    var active by remember { mutableStateOf(setOf(NavItem.Home.route)) }

    AppBottomNav(
        activeRoutes = active,
        onItemSelected = {
            active = setOf(it.route)
        }
    )
}

@Preview(showBackground = true, widthDp = 360)
@Composable
private fun PreviewBottomNavMultiActive() {
    var active by remember { mutableStateOf(setOf(NavItem.Home.route, NavItem.Profile.route)) }

    AppBottomNav(
        activeRoutes = active,
        onItemSelected = {
            active = if (active.contains(it.route)) active - it.route else active + it.route
        }
    )
}
