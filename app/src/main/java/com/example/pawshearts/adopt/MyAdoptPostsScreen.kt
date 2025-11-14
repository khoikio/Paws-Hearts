package com.example.pawshearts.adopt

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.pawshearts.adopt.components.PostAdopt
import com.example.pawshearts.auth.AuthViewModel
import com.example.pawshearts.navmodel.Routes

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyAdoptPostsScreen(
    nav: NavHostController,
    adoptViewModel: AdoptViewModel,
    authViewModel: AuthViewModel
) {

    // LẤY LIST TỪ NÃO MỚI
    val userProfileData by authViewModel.userProfile.collectAsState()
    val currentUserId = userProfileData?.userId ?: ""
    val myAdoptPosts by adoptViewModel.myAdoptPosts.collectAsState()
    // TẠM THỜI CHƯA FETCH
    LaunchedEffect(currentUserId) {
        if (currentUserId.isNotEmpty()) {
            adoptViewModel.fetchMyAdoptPosts(currentUserId)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Bài nhận nuôi của bạn") },
                navigationIcon = {
                    IconButton(onClick = { nav.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Quay lại")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFFFFF3E0),
                    titleContentColor = Color.Black,
                    navigationIconContentColor = Color.Black
                )
            )
        },
        floatingActionButton = { // <-- NÚT TẠO MỚI XỊN KKK
            FloatingActionButton(
                onClick = {
                    nav.navigate(Routes.CREATE_ADOPT_POST_SCREEN) // <-- BẤM VÔ NHẢY QUA TRANG TẠO
                },
                containerColor = Color(0xFFE65100),
                contentColor = Color.White
            ) {
                Icon(Icons.Default.Add, contentDescription = "Đăng nhận nuôi")
            }
        }
    ) { paddingValues ->

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            if (myAdoptPosts.isEmpty()) {
                item {
                    Text(
                        "M chưa đăng tìm chủ bé nào KKK :v",
                        modifier = Modifier.padding(vertical = 24.dp),
                        color = Color.Gray
                    )
                }
            } else {
                items(myAdoptPosts) { adoptPost ->
                    // M XÀI LẠI CÁI PostAdopt CỦA M KKK
                    PostAdopt(
                        post = adoptPost, onEditClick = { /* edit */ },
                        postUI = TODO(),
                        adoptViewModel = TODO(),
                        navController = TODO()
                    )
                }
            }
        }
    }
}