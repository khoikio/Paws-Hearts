package com.example.pawshearts.navmodel

import android.app.Application
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
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
import androidx.compose.runtime.Composable
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
import com.example.pawshearts.adopt.AdoptViewModel
import com.example.pawshearts.adopt.AdoptViewModelFactory
import com.example.pawshearts.adopt.CreateAdoptPostScreen
import com.example.pawshearts.adopt.MyAdoptPostsScreen
import com.example.pawshearts.adopt.components.AdoptScreen
import com.example.pawshearts.activities.ActivitiesScreen
import com.example.pawshearts.activities.ActivityViewModel
import com.example.pawshearts.activities.ActivityViewModelFactory
import com.example.pawshearts.activities.CreateActivityScreen
import com.example.pawshearts.donate.BankDonateScreen
import com.example.pawshearts.donate.DonateScreen
import com.example.pawshearts.post.CommentScreen
import com.example.pawshearts.post.CreatePostScreen
import com.example.pawshearts.post.HomeScreen
import com.example.pawshearts.post.MyPostsScreen
import com.example.pawshearts.post.PetDetailScreen
import com.example.pawshearts.post.PostViewModel
import com.example.pawshearts.post.PostViewModelFactory
import com.example.pawshearts.profile.ProfileScreen
import com.example.pawshearts.SplashScreen
import com.example.pawshearts.ui.theme.LightBackground
import com.google.firebase.auth.FirebaseAuth

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppRoot() {
    val nav = rememberNavController()
    // KHAI BÁO CÁC VM CẦN CHUNG (TẠO 1 LẦN DUY NHẤT Ở ĐÂY KKK)
    val context = LocalContext.current.applicationContext as Application
    val authViewModel: AuthViewModel = viewModel(factory = AuthViewModelFactory(context))
    val postViewModel: PostViewModel = viewModel(factory = PostViewModelFactory(context))
    val adoptViewModel: AdoptViewModel = viewModel(factory = AdoptViewModelFactory(context))
    val activityViewModel: ActivityViewModel = viewModel(factory = ActivityViewModelFactory(context))
    val currentUser = authViewModel.currentUser
    val isLoggedIn by authViewModel.isUserLoggedIn.collectAsStateWithLifecycle()

    // Logic hiển thị BottomBar (Giữ nguyên)
    val currentRoute = nav.currentBackStackEntryAsState().value?.destination?.route

    // THÊM ĐIỀU KIỆN NÈ M KKK :D
    val showBottomBar = currentRoute != Routes.LOGIN_SCREEN &&
            currentRoute != Routes.REGISTER_SCREEN &&
            currentRoute != Routes.SPLASH_SCREEN

    Scaffold(
        topBar = {},
        bottomBar = { if (showBottomBar) {
            BottomBar(nav)
        }
        },
        contentWindowInsets = WindowInsets(0.dp, 0.dp, 0.dp, 0.dp), // Fix tràn viền

    ) { innerPadding ->
        NavHost(
            navController = nav,
            startDestination = Routes.SPLASH_SCREEN,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Routes.SPLASH_SCREEN) {
                SplashScreen(navController = nav)
            }
            composable(Routes.CREATE_POST_SCREEN) {
                CreatePostScreen(navController = nav)
            }

            // MY_POSTS_SCREEN: Dùng lại VM đã tạo ở ngoài
            composable (Routes.MY_POSTS_SCREEN){
                MyPostsScreen(
                    nav = nav,
                    authViewModel = authViewModel,
                    postViewModel = postViewModel
                )
            }

            composable(Routes.LOGIN_SCREEN) {
                AuthRootScreen(navController = nav)
            }

            composable(Routes.HOME)    { HomeScreen(nav) }
            composable(Routes.DONATE)  { DonateScreen(nav) }

            // ADOPT: Dùng lại VM đã tạo ở ngoài
            composable(Routes.ADOPT)   {
                AdoptScreen(
                    nav = nav,
                    adoptViewModel = adoptViewModel,
                    authViewModel = authViewModel
                )
            }

            composable(Routes.DONATE_BANK_SCREEN) {
                BankDonateScreen(nav = nav)
            }

            // PROFILE: Dùng lại AuthVM đã tạo ở ngoài
            composable(Routes.PROFILE) {
                // T VỚI M KHÔNG CẦN FACTORY Ở ĐÂY NỮA

                // Lấy dữ liệu người dùng từ AuthVM đã tạo ở ngoài
                val firestoreProfile by authViewModel.userProfile.collectAsStateWithLifecycle(null)

                if (firestoreProfile != null) {
                    ProfileScreen(
                        nav = nav,
                        userData = firestoreProfile!!,
                        outSignOut = {
                            authViewModel.logout()
                            nav.navigate(Routes.LOGIN_SCREEN) {
                                popUpTo(nav.graph.id) { inclusive = true }
                            }
                        },
                        authViewModel = authViewModel,
                        postViewModel = postViewModel

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


            // MÀN CHI TIẾT
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

            // MÀN COMMENT
            composable(
                route = "${Routes.COMMENT_SCREEN}/{postId}",
                arguments = listOf(navArgument("postId") { type = NavType.StringType })
            ) { backStackEntry ->
                val postId = backStackEntry.arguments?.getString("postId") ?: ""

                CommentScreen(
                    postId = postId,
                    onBack = { nav.popBackStack() }
                )
            }

            // MY_ADOPT_POSTS_SCREEN: Dùng lại VM đã tạo ở ngoài
            composable(Routes.MY_ADOPT_POSTS_SCREEN) {
                MyAdoptPostsScreen(
                    nav = nav,
                    adoptViewModel = adoptViewModel,
                    authViewModel = authViewModel
                )
            }

            // CREATE_ADOPT_POST_SCREEN: Dùng lại AdoptVM đã tạo ở ngoài
            composable(Routes.CREATE_ADOPT_POST_SCREEN) {
                CreateAdoptPostScreen(
                    nav = nav,
                    adoptViewModel = adoptViewModel
                )
            }

            // ACTIVITIES_LIST_SCREEN: Dùng lại VM đã tạo ở ngoài
            composable(Routes.ACTIVITIES_LIST_SCREEN) {
                ActivitiesScreen(
                    nav = nav,
                    authViewModel = authViewModel,
                    activityViewModel = activityViewModel
                )
            }

            // CREATE_ACTIVITY_SCREEN: Dùng lại ActivityVM đã tạo ở ngoài
            composable(Routes.CREATE_ACTIVITY_SCREEN) {
                CreateActivityScreen(
                    nav = nav,
                    activityViewModel = activityViewModel
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
    // Code BottomBar giữ nguyên KKK
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