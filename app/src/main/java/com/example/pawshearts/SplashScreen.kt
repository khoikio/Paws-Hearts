package com.example.pawshearts

import android.app.Application
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.pawshearts.auth.AuthViewModel
import com.example.pawshearts.auth.AuthViewModelFactory
import com.example.pawshearts.navmodel.Routes
import kotlinx.coroutines.delay
import com.example.pawshearts.ui.theme.AppTypography
@Composable
fun SplashScreen(navController: NavHostController) {
    val context = LocalContext.current.applicationContext as Application
    val authViewModel: AuthViewModel = viewModel(factory = AuthViewModelFactory(context))

    LaunchedEffect(Unit) {
        delay(2000)

        val isLoggedIn = try {
            authViewModel.isUserLoggedIn.value
        } catch (e: Exception) {
            android.util.Log.e("SplashScreen", "🔥 Lỗi khi lấy trạng thái đăng nhập", e)
            false
        }

        android.util.Log.d("SplashScreen", "✅ isLoggedIn = $isLoggedIn")

        if (isLoggedIn) {
            navController.navigate(Routes.HOME) {
                popUpTo(Routes.SPLASH_SCREEN) { inclusive = true }
            }
        } else {
            navController.navigate(Routes.LOGIN_SCREEN) {
                popUpTo(Routes.SPLASH_SCREEN) { inclusive = true }
            }
        }
    }

    // Giao diện của Splash Screen (Đã được "nối điện lưới")
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background), // Dùng màu nền của Theme
        contentAlignment = Alignment.Center
    ) {
        // Logo của mày
        Text(
            "Paws & Hearts",
            style = MaterialTheme.typography.displayLarge,
            color = MaterialTheme.colorScheme.primary // Chữ màu cam cho nó nổi
        )
    }
}
//            text = "Paws & Hearts",
//            style = MaterialTheme.typography.displayLarge,
//            color = Color(0xFFEA5600)
@Preview
@Composable
fun SplashScreenPreview() {
    SplashScreen(navController = NavHostController(LocalContext.current))
}