package com.example.pawshearts


import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.navigation.NavHostController
import com.example.pawshearts.navmodel.Routes
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(navController: NavHostController) {
    LaunchedEffect(key1 = true) {
        delay(2000L)

        navController.navigate(Routes.LOGIN_SCREEN) {
            popUpTo(Routes.SPLASH_SCREEN) { inclusive = true }
        }
    }

    // GIAO DIỆN NÈ M (CÁI ẢNH CỦA M)
    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(id = R.drawable.bia), // TÊN ẢNH M VỪA NÉM VÔ
            contentDescription = "Splash Screen",
            modifier = Modifier.fillMaxSize(),
            // T XÀI 'FillBounds' (KÉO DÃN) THAY VÌ 'Crop' (CẮT)
            contentScale = ContentScale.FillBounds
        )
    }
}