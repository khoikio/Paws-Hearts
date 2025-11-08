package com.example.pawshearts

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.pawshearts.components.CustomBottomNavigation
import com.example.pawshearts.components.PetDetailScreen
import com.example.pawshearts.navmodel.NavItem
import com.example.pawshearts.navmodel.Routes
import com.example.pawshearts.screens.AdoptScreen
import com.example.pawshearts.screens.DonateScreen
import com.example.pawshearts.screens.HomeScreen
import com.example.pawshearts.screens.LoginScreen
import com.example.pawshearts.screens.ProfileScreen
import com.example.pawshearts.screens.RegisterScreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppRoot() {
    val nav = rememberNavController()
    val items = remember {
        listOf(
            NavItem.Home,
            NavItem.Donate,
            NavItem.Adopt,
            NavItem.Profile
        )
    }
    val authRepository = remember { AuthRepository() }
    val startDestination = if (authRepository.isUserLoggedIn()) Routes.HOME else Routes.LOGIN

    val currentRoute by nav.currentBackStackEntryAsState()
    val showBottomBar = currentRoute?.destination?.route !in listOf(Routes.LOGIN, Routes.REGISTER, Routes.PET_DETAIL)

    Scaffold(
        topBar = { TopAppBar(title = { Text("Paws & Hearts") }) },
        bottomBar = {
            if (showBottomBar) {
                CustomBottomNavigation(
                    items = items,
                    selectedRoute = currentRoute?.destination?.route ?: Routes.HOME,
                    onItemSelected = {
                        nav.navigate(it.route) {
                            popUpTo(Routes.HOME) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = nav,
            startDestination = startDestination,
            modifier = Modifier.padding(innerPadding)
        ) {
            // Main App
            composable(Routes.HOME) { HomeScreen(nav) }
            composable(Routes.DONATE) { DonateScreen(nav) }
            composable(Routes.ADOPT) { AdoptScreen(nav) }
            composable(Routes.PROFILE) { ProfileScreen(nav) }
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

            // Auth Flow
            composable(Routes.LOGIN) { LoginScreen(nav) } 
            composable(Routes.REGISTER) { RegisterScreen(nav) } 
        }
    }
}

fun NavHostController.goPetDetail(id: String) {
    navigate(Routes.petDetail(id))
}
