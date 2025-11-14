package com.example.pawshearts


import android.app.Application
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
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

    // Lắng nghe trạng thái đăng nhập
    val isLoggedIn by authViewModel.isUserLoggedIn.collectAsStateWithLifecycle()

    // Dùng LaunchedEffect để kiểm tra 1 lần duy nhất
    LaunchedEffect(key1 = isLoggedIn) {
        // Thêm một độ trễ nhỏ để user thấy logo
        delay(1500)

        // Sau khi kiểm tra xong, điều hướng và xóa Splash khỏi back stack
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

    // Giao diện của Splash Screen
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        // Logo của mày
        Text(
            text = "Paws & Hearts",
            style = MaterialTheme.typography.displayLarge,
            color = Color(0xFFEA5600)
        )
    }

}