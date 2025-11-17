package com.example.pawshearts.navmodel

import android.app.Application
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
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
import com.example.pawshearts.SplashScreen
import com.example.pawshearts.activities.*
import com.example.pawshearts.adopt.*
import com.example.pawshearts.adopt.components.AdoptCommentScreen
import com.example.pawshearts.adopt.components.AdoptScreen
import com.example.pawshearts.auth.*
import com.example.pawshearts.donate.*
import com.example.pawshearts.messages.ui.screens.ChatScreen
import com.example.pawshearts.messages.ui.screens.MessagesScreen
import com.example.pawshearts.notification.NotificationScreen
import com.example.pawshearts.post.*
import com.example.pawshearts.profile.ProfileScreen
import com.example.pawshearts.settings.*
import com.example.pawshearts.ui.theme.Theme

@Composable
fun AppRoot() {
    val context = LocalContext.current.applicationContext
    val themeViewModel: SettingViewModel = viewModel(
        factory = SettingViewModelFactory(SettingsRepository(context))
    )
    val isDarkMode by themeViewModel.isDarkMode.collectAsStateWithLifecycle(initialValue = false)

    Theme(darkTheme = isDarkMode) {
        AppContent(themeViewModel = themeViewModel)
    }
}

fun NavHostController.goPetDetail(id: String) {
    navigate(Routes.petDetail(id))
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AppContent(themeViewModel: SettingViewModel) {
    val nav = rememberNavController()
    val context = LocalContext.current.applicationContext as Application
    val authViewModel: AuthViewModel = viewModel(factory = AuthViewModelFactory(context))
    val isLoggedIn by authViewModel.isUserLoggedIn.collectAsStateWithLifecycle()

    // --- LOGIC TỰ ĐỘNG ĐIỀU HƯỚNG ---
    LaunchedEffect(isLoggedIn) {
        val currentRoute = nav.currentBackStackEntry?.destination?.route
        if (isLoggedIn && (currentRoute == Routes.LOGIN_SCREEN || currentRoute == Routes.REGISTER_SCREEN)) {
            nav.navigate(Routes.HOME) {
                popUpTo(nav.graph.id) { inclusive = true }
            }
        }
    }

    val currentRoute = nav.currentBackStackEntryAsState().value?.destination?.route
    val showBottomBar = isLoggedIn && currentRoute !in listOf(
        Routes.SPLASH_SCREEN,
        Routes.LOGIN_SCREEN,
        Routes.REGISTER_SCREEN,
        Routes.CHAT
    )

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
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
            composable(Routes.SPLASH_SCREEN) { SplashScreen(navController = nav) }
            composable(Routes.LOGIN_SCREEN) { AuthRootScreen(navController = nav) }

            if (isLoggedIn) {
                val currentUser = authViewModel.currentUser
                
                composable(Routes.HOME) { HomeScreen(nav) }
                composable(Routes.DONATE) { DonateScreen(nav) }
                composable(Routes.DONATE_BANK_SCREEN) { BankDonateScreen(nav = nav) }
                composable(Routes.CREATE_POST_SCREEN) { CreatePostScreen(navController = nav) }

                composable(Routes.MY_POSTS_SCREEN) {
                    val postViewModel: PostViewModel = viewModel(factory = PostViewModelFactory(context))
                    MyPostsScreen(nav = nav, authViewModel = authViewModel, postViewModel = postViewModel)
                }

                composable(Routes.ADOPT) {
                    val adoptViewModel: AdoptViewModel = viewModel(factory = AdoptViewModelFactory(context))
                    AdoptScreen(nav = nav, adoptViewModel = adoptViewModel, authViewModel = authViewModel)
                }

                composable(Routes.MY_ADOPT_POSTS_SCREEN) {
                    val adoptViewModel: AdoptViewModel = viewModel(factory = AdoptViewModelFactory(context))
                    MyAdoptPostsScreen(nav = nav, adoptViewModel = adoptViewModel, authViewModel = authViewModel)
                }

                composable(Routes.CREATE_ADOPT_POST_SCREEN) {
                    val adoptViewModel: AdoptViewModel = viewModel(factory = AdoptViewModelFactory(context))
                    CreateAdoptPostScreen(nav = nav, adoptViewModel = adoptViewModel)
                }

                composable(Routes.ACTIVITIES_LIST_SCREEN) {
                    val activityViewModel: ActivityViewModel = viewModel(factory = ActivityViewModelFactory(context))
                    ActivitiesScreen(nav = nav, authViewModel = authViewModel, activityViewModel = activityViewModel)
                }

                composable(Routes.CREATE_ACTIVITY_SCREEN) {
                    val activityViewModel: ActivityViewModel = viewModel(factory = ActivityViewModelFactory(context))
                    CreateActivityScreen(nav = nav, activityViewModel = activityViewModel)
                }

                composable(Routes.SETTINGS_SCREEN) { SettingsScreen(nav = nav, themeViewModel = themeViewModel) }
                
                composable(Routes.NOTIFICATION_SCREEN) { 
                    if(currentUser != null) NotificationScreen(userId = currentUser.uid) 
                }
                
                composable(Routes.MESSAGES) { MessagesScreen(onBackClick = { nav.popBackStack() }, onThreadClick = { threadId -> nav.navigate(Routes.chat(threadId)) }) }

                composable(
                    route = Routes.PET_DETAIL,
                    arguments = listOf(navArgument("id") { type = NavType.StringType })
                ) { backStack ->
                    val petId = backStack.arguments?.getString("id") ?: ""
                    PetDetailScreen(id = petId, onBack = { nav.popBackStack() })
                }

                composable(
                    route = "comment_screen/{postId}",
                    arguments = listOf(navArgument("postId") { type = NavType.StringType })
                ) { backStackEntry ->
                    val postId = backStackEntry.arguments?.getString("postId") ?: ""
                    CommentScreen(postId = postId, onBack = { nav.popBackStack() })
                }

                composable(
                    route = "${Routes.ADOPT_COMMENT_SCREEN}/{adoptPostId}",
                    arguments = listOf(navArgument("adoptPostId") { type = NavType.StringType })
                ) { backStackEntry ->
                    val adoptPostId = backStackEntry.arguments?.getString("adoptPostId") ?: ""
                    val adoptViewModel: AdoptViewModel = viewModel(factory = AdoptViewModelFactory(context))
                    AdoptCommentScreen(
                        adoptPostId = adoptPostId,
                        adoptViewModel = adoptViewModel,
                        authViewModel = authViewModel,
                        onBack = { nav.popBackStack() }
                    )
                }

                composable(
                    route = Routes.CHAT,
                    arguments = listOf(navArgument("threadId") { type = NavType.StringType })
                ) { backStackEntry ->
                    val threadId = backStackEntry.arguments?.getString("threadId") ?: ""
                    ChatScreen(threadId = threadId, onBackClick = { nav.popBackStack() })
                }

                composable(Routes.PROFILE) {
                    val postViewModel: PostViewModel = viewModel(factory = PostViewModelFactory(context))
                    val firestoreProfile by authViewModel.userProfile.collectAsStateWithLifecycle()
                    if (firestoreProfile != null) {
                        ProfileScreen(
                            nav = nav,
                            userData = firestoreProfile!!,
                            authViewModel = authViewModel,
                            postViewModel = postViewModel,
                            onSettingsClick = { nav.navigate(Routes.SETTINGS_SCREEN) }
                        )
                    } else {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator()
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun BottomBar(nav: NavHostController) {
    val items = listOf(NavItem.Home, NavItem.Donate, NavItem.Adopt, NavItem.Profile)
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
