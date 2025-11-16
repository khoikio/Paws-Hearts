package com.example.pawshearts.ui.theme

import android.app.Activity // ⚠️ dùng Activity của Android, không phải model của m
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

// LIGHT THEME 🌞
private val LightColors = lightColorScheme(
    primary      = OrangeEA,      // nút chính, icon nổi bật
    secondary    = LightOrange,   // màu phụ (chip, thanh nhỏ, vv)
    tertiary     = Orange,        // tùy m dùng thêm
    background   = LightBackground,
    surface      = Color.White,
    onPrimary    = Color.White,   // chữ trên nút cam → trắng
    onSecondary  = DarkText,      // chữ trên màu secondary
    onTertiary   = DarkText,
    onBackground = DarkText,      // chữ trên nền màn chính
    onSurface    = DarkText       // chữ trong Card / Surface
)

// DARK THEME 🌚
private val DarkColors = darkColorScheme(
    primary      = OrangeEA,      // nút cam vẫn nổi
    secondary    = LightOrange,   // cam nhạt
    tertiary     = Orange,
    background   = DarkBackground,
    surface      = DarkSurface,
    onPrimary    = Color.Black,   // chữ trên nút cam → đen (cam khá sáng)
    onSecondary  = Color.Black,
    onTertiary   = Color.Black,
    onBackground = Color.White,   // chữ trên nền tối
    onSurface    = Color.White    // chữ trong card tối
)

@Composable
fun Theme( // hoặc đổi tên thành PawsHeartsTheme cho dễ nhớ
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    // ✅ đúng tên biến
    val colorScheme = if (darkTheme) DarkColors else LightColors

    // đổi màu status bar theo theme
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.background.toArgb()
            WindowCompat.getInsetsController(window, view)
                .isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = AppTypography,
        content = content
    )
}
