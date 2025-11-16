package com.example.pawshearts.navmodel

import android.app.Application
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.pawshearts.activities.*
import com.example.pawshearts.adopt.*
import com.example.pawshearts.adopt.components.AdoptScreen
import com.example.pawshearts.auth.*
import com.example.pawshearts.donate.*
import com.example.pawshearts.post.*
import com.example.pawshearts.profile.ProfileScreen
import com.example.pawshearts.settings.*
import com.example.pawshearts.SplashScreen
import com.example.pawshearts.notification.NotificationScreen
import com.example.pawshearts.ui.theme.LightBackground
import com.example.pawshearts.ui.theme.Theme
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

// Cấu trúc mới: AppRoot là hàm cha chứa tất cả ٩(^‿^)۶
@Composable
fun AppRoot() {
    val context = LocalContext.current.applicationContext
    val themeViewModel: SettingViewModel = viewModel(
        factory = SettingViewModelFactory(SettingsRepository(context))
    )
    val isDarkMode by themeViewModel.isDarkMode.collectAsStateWithLifecycle()

    // Áp dụng theme cho toàn bộ ứng dụng
    Theme(darkTheme = isDarkMode) {
        // Gọi phần nội dung chính, bây giờ nó nằm gọn bên trong AppRoot
        AppContent(themeViewModel = themeViewModel)
    }
}

// Composable này và các hàm con của nó bây giờ nằm BÊN TRONG AppRoot
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AppContent(themeViewModel: SettingViewModel) {
    val nav = rememberNavController() // <-- "Bản đồ" được tạo ở đây
    val context = LocalContext.current.applicationContext as Application

//    // Khởi tạo tất cả ViewModel ở đây


    val currentRoute = nav.currentBackStackEntryAsState().value?.destination?.route
    val showBottomBar = currentRoute !in listOf(Routes.LOGIN_SCREEN, Routes.REGISTER_SCREEN, Routes.SPLASH_SCREEN)

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                // Bây giờ BottomBar có thể thấy `nav` vì chúng ở cùng một "nhà" (─‿‿─)
                BottomBar(nav = nav)
            }
        },
        contentWindowInsets = WindowInsets(0.dp, 0.dp, 0.dp, 0.dp),
    ) { innerPadding ->
        NavHost(
            navController = nav,
            startDestination = Routes.SPLASH_SCREEN,
            modifier = Modifier.padding(innerPadding)
        ) {
            // === CÁC MÀN HÌNH CỦA APP ===
            composable(Routes.SPLASH_SCREEN) { SplashScreen(navController = nav) }
            composable(Routes.LOGIN_SCREEN) { AuthRootScreen(navController = nav) }
            composable(Routes.HOME) { HomeScreen(nav) }
            // ... (Tất cả các composable khác của mày giữ nguyên y hệt) ...
            composable(Routes.DONATE) { DonateScreen(nav) }
            composable(Routes.DONATE_BANK_SCREEN) { BankDonateScreen(nav = nav) }

            composable(Routes.CREATE_POST_SCREEN) { CreatePostScreen(navController = nav) }
            composable(Routes.MY_POSTS_SCREEN) {
                val authViewModel: AuthViewModel = viewModel(factory = AuthViewModelFactory(context))
                val postViewModel: PostViewModel = viewModel(factory = PostViewModelFactory(context))
                MyPostsScreen(
                    nav = nav,
                    authViewModel = authViewModel,
                    postViewModel = postViewModel
                )
            }

            composable(Routes.ADOPT) {
                val authViewModel: AuthViewModel = viewModel(factory = AuthViewModelFactory(context))
                val adoptViewModel: AdoptViewModel = viewModel(factory = AdoptViewModelFactory(context))
                val activityViewModel: ActivityViewModel = viewModel(factory = ActivityViewModelFactory(context))

                AdoptScreen(
                    nav = nav,
                    adoptViewModel = adoptViewModel,
                    authViewModel = authViewModel
                )
            }
            composable(Routes.MY_ADOPT_POSTS_SCREEN) {
                val authViewModel: AuthViewModel = viewModel(factory = AuthViewModelFactory(context))
                val adoptViewModel: AdoptViewModel = viewModel(factory = AdoptViewModelFactory(context))


                MyAdoptPostsScreen(
                    nav = nav,
                    adoptViewModel = adoptViewModel,
                    authViewModel = authViewModel
                )
            }
            composable(Routes.CREATE_ADOPT_POST_SCREEN) {
                val adoptViewModel: AdoptViewModel = viewModel(factory = AdoptViewModelFactory(context))
                CreateAdoptPostScreen(
                    nav = nav,
                    adoptViewModel = adoptViewModel
                )
            }

            composable(Routes.ACTIVITIES_LIST_SCREEN) {
                val authViewModel: AuthViewModel = viewModel(factory = AuthViewModelFactory(context))
                val activityViewModel: ActivityViewModel = viewModel(factory = ActivityViewModelFactory(context))
                ActivitiesScreen(
                    nav = nav,
                    authViewModel = authViewModel,
                    activityViewModel = activityViewModel
                )
            }
            composable(Routes.CREATE_ACTIVITY_SCREEN) {
                val activityViewModel: ActivityViewModel = viewModel(factory = ActivityViewModelFactory(context))
                CreateActivityScreen(
                    nav = nav,
                    activityViewModel = activityViewModel
                )
            }

            composable(
                route = Routes.PET_DETAIL,
                arguments = listOf(navArgument("id") { type = NavType.StringType })
            ) { backStack ->
                val petId = backStack.arguments?.getString("id") ?: ""
                PetDetailScreen(id = petId, onBack = { nav.popBackStack() })
            }

            composable(
                route = "${Routes.COMMENT_SCREEN}/{postId}",
                arguments = listOf(navArgument("postId") { type = NavType.StringType })
            ) { backStackEntry ->
                val postId = backStackEntry.arguments?.getString("postId") ?: ""
                CommentScreen(postId = postId, onBack = { nav.popBackStack() })
            }

            // MÀN HÌNH PROFILE
            composable(Routes.PROFILE) {
                val context = LocalContext.current.applicationContext as Application
                val authViewModel: AuthViewModel = viewModel(factory = AuthViewModelFactory(context))
                val postViewModel: PostViewModel = viewModel(factory = PostViewModelFactory(context))
                val scope = rememberCoroutineScope()
                val firestoreProfile by authViewModel.userProfile.collectAsStateWithLifecycle(null)

                if (firestoreProfile != null) {
                    ProfileScreen(
                        nav = nav,
                        userData = firestoreProfile!!,
                        outSignOut = {

                            scope.launch {
                                nav.navigate(Routes.LOGIN_SCREEN) {
                                    popUpTo(nav.graph.findStartDestination().id){
                                        inclusive = true
                                    }
                                    launchSingleTop = true
                                }
                                delay(50L)
                                authViewModel.logout()
                            }

                        },
                        authViewModel = authViewModel,
                        postViewModel = postViewModel,
                        onSettingsClick = {nav.navigate(Routes.SETTINGS_SCREEN)}
                    )
                } else {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }
            }

            // MÀN HÌNH CÀI ĐẶT MỚI
            composable(Routes.SETTINGS_SCREEN) {
                SettingsScreen(
                    nav = nav,
                    themeViewModel = themeViewModel
                )
            }
            composable (Routes.NOTIFICATION_SCREEN ) {
                val userId = FirebaseAuth.getInstance().currentUser?.uid
                if (userId != null){
                    NotificationScreen( userId = userId)
                }else{
                    // chua dang nhap thi chuyen ve phan dang nhap
                    nav.navigate(Routes.LOGIN_SCREEN){
                        popUpTo(nav.graph.id){
                            inclusive = true
                        }
                    }
                }

            }
        }
    }
}

@Composable
private fun BottomBar(nav: NavHostController) {
    // ... code của BottomBar giữ nguyên y hệt, nó đã đúng rồi ...
    val items = listOf(
        NavItem.Home,
        NavItem.Donate,
        NavItem.Adopt,
        NavItem.Profile
    )
    val currentRoute = nav.currentBackStackEntryAsState().value?.destination?.route

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp)
            .clip(RoundedCornerShape(28.dp))
            .background(MaterialTheme.colorScheme.surface)
            .padding(horizontal = 12.dp, vertical = 10.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically

        ) {
            items.forEach { item ->
                val selected = currentRoute == item.route
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(24.dp))
                        .clickable {
                            nav.navigate(item.route) {
                                popUpTo(nav.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(if (selected) 36.dp else 28.dp)
                            .clip(CircleShape)
                            .background(if (selected) MaterialTheme.colorScheme.primary else Color.Transparent),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = item.icon,
                            contentDescription = item.label,
                            tint = if (selected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(Modifier.height(6.dp))
                    if (selected) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(16.dp))
                                .background(MaterialTheme.colorScheme.primaryContainer)
                        ) {
                            Text(
                                text = item.label,
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                            )
                        }
                    } else {
                        Text(
                            text = item.label,
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.7f)
                        )
                    }
                }
            }
        }
    }
}
fun NavHostController.goPetDetail(petId: String) {
    this.navigate("${Routes.PET_DETAIL_SCREEN_BASE}/$petId")
}