package com.example.pawshearts.navmodel

import androidx.compose.ui.graphics.vector.ImageVector
import com.example.pawshearts.ui.theme.AppIcons

/**
 * NavItem mô tả từng tab trong bottom bar:
 * - route (điều hướng)
 * - icon (hình)
 * - label (text tiếng Việt hiển thị)
 */
sealed class NavItem(
    val route: String,
    val icon: ImageVector,
    val label: String
) {
    object Home    : NavItem(Routes.HOME,    AppIcons.Home,    "Trang chủ")
    object Donate  : NavItem(Routes.DONATE,  AppIcons.Donate,  "Quyên góp")
    object Adopt   : NavItem(Routes.ADOPT,   AppIcons.Adopt,   "Nhận nuôi")
    object Profile : NavItem(Routes.PROFILE, AppIcons.Profile, "Hồ sơ")
}
