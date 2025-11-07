package com.example.pawshearts

import android.app.Application
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.pawshearts.auth.AuthRootScreen
import com.example.pawshearts.auth.AuthViewModel
import com.example.pawshearts.auth.AuthViewModelFactory

import com.example.pawshearts.navmodel.NavItem
import com.example.pawshearts.navmodel.Routes
import com.example.pawshearts.screens.AdoptScreen
import com.example.pawshearts.screens.DonateScreen
import com.example.pawshearts.screens.HomeScreen
import com.example.pawshearts.components.PetDetailScreen
import com.example.pawshearts.screens.ProfileScreen
import com.example.pawshearts.ui.theme.LightBackground
import com.google.firebase.auth.FirebaseAuth

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppRoot() {
    val nav = rememberNavController()
    // khai bao dung firebaseauth
    val auth = remember { FirebaseAuth.getInstance() }

    // Logic hiển thị BottomBar (Giữ nguyên)
    val currentRoute = nav.currentBackStackEntryAsState().value?.destination?.route
    val showBottomBar = currentRoute != Routes.LOGIN_SCREEN && currentRoute != Routes.REGISTER_SCREEN
    Scaffold(
        topBar = {
            if (showBottomBar) {
                TopAppBar(title = { Text("Paws & Hearts") })
            }
        },
        bottomBar = { if (showBottomBar) {
            BottomBar(nav)
        } }
    ) { innerPadding ->
        NavHost(
            navController = nav,
            startDestination = Routes.LOGIN_SCREEN,// CHAY TRANG LOGIN DAU TIEN dung lai startRoute
            modifier = Modifier.padding(innerPadding)
        ) {
            // 4 tab chính
            composable(Routes.LOGIN_SCREEN) { // <-- GIỮ NGUYÊN ROUTE
                AuthRootScreen(navController = nav) // <-- GỌI MÀN HÌNH KHUNG MỚI
            }
            composable(Routes.HOME)    { HomeScreen(nav) }
            composable(Routes.DONATE)  { DonateScreen(nav) }
            composable(Routes.ADOPT)   { AdoptScreen(nav) }

            composable(Routes.PROFILE) {
                // 1. Lấy Application Context (CÁCH ĐÚNG NÈ)
                val context = LocalContext.current.applicationContext as Application

                val authViewModelFactory = AuthViewModelFactory(context)

                val authViewModel: AuthViewModel = viewModel(factory = authViewModelFactory)

                // 4. Lấy dữ liệu người dùng TỪ ROOM (qua ViewModel)
                val firestoreProfile by authViewModel.userProfile.collectAsStateWithLifecycle(null)

                // 5. Bắt đầu gọi tải profile (nếu chưa có)
                LaunchedEffect(authViewModel.currentUser) {
                    if(authViewModel.currentUser != null) {
                        authViewModel.fetchUserProfile(authViewModel.currentUser!!.uid)
                    }
                }

                // 6. Kiểm tra và gọi ProfileScreen
                if (firestoreProfile != null) { // Giờ M check 'firestoreProfile' từ Room
                    ProfileScreen(
                        nav = nav,
                        userData = firestoreProfile!!, // Truyền UserData từ Room
                        outSignOut = {
                            authViewModel.logout()
                            nav.navigate(Routes.LOGIN_SCREEN) {
                                popUpTo(nav.graph.id) { inclusive = true }
                            }
                        },
                        authViewModel = authViewModel
                    )
                } else {
                    // Xử lý trường hợp đang tải hoặc ko có info
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator() // Hiển thị vòng xoay tải
                    }
                }
            }


            // màn chi tiết thú cưng (để thành viên 1 hiển thị thông tin chi tiết)
            composable(
                route = Routes.PET_DETAIL,
                arguments = listOf(navArgument("id") { type = NavType.StringType })
            ) { backStack ->
                val petId = backStack.arguments?.getString("id") ?: ""
                PetDetailScreen(
                    id = petId,
                    onBack = { nav.popBackStack() }
                )
            }
        }
    }
}


fun NavHostController.goPetDetail(id: String) {
    navigate(Routes.petDetail(id))
}

@Composable
private fun BottomBar(nav: NavHostController) {
    val items = listOf(
        NavItem.Home,
        NavItem.Donate,
        NavItem.Adopt,
        NavItem.Profile
    )

    // route hiện tại để biết tab nào đang active
    val currentRoute = nav.currentBackStackEntryAsState().value?.destination?.route

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp)
            .clip(RoundedCornerShape(28.dp))
            .background(LightBackground)
            .padding(horizontal = 12.dp, vertical = 10.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            items.forEachIndexed { index, item ->
                val selected = currentRoute == item.route

                Column(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(24.dp))
                        .clickable {
                            if (currentRoute != item.route) {
                                nav.navigate(item.route) {
                                    popUpTo(Routes.HOME) { saveState = true }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        },
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // icon với nền tròn khi active
                    Box(
                        modifier = Modifier
                            .size(if (selected) 36.dp else 28.dp)
                            .clip(CircleShape)
                            .background(
                                if (selected) MaterialTheme.colorScheme.primary
                                else Color.Transparent
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = item.icon,
                            contentDescription = item.label,
                            tint = if (selected)
                                MaterialTheme.colorScheme.onPrimary
                            else
                                LocalContentColor.current,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Spacer(Modifier.height(6.dp))

                    if (selected) {
                        // label nổi bật dạng chip cam
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(16.dp))
                                .background(MaterialTheme.colorScheme.primary)
                                .padding(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = item.label,
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onPrimary,
                                textAlign = TextAlign.Center
                            )
                        }
                    } else {
                        // label xám nhạt khi inactive
                        Text(
                            text = item.label,
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(horizontal = 4.dp)
                        )
                    }
                }

                // spacing giữa item, trừ item cuối cùng
                if (index != items.lastIndex) {
                    Spacer(modifier = Modifier.width(4.dp))
                }
            }
        }
    }
}
