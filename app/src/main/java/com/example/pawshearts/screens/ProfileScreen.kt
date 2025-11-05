package com.example.pawshearts.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.pawshearts.components.PetGridItem
import com.example.pawshearts.components.ProfileHeader
import com.example.pawshearts.goPetDetail
import com.example.pawshearts.ui.theme.OrangeEA
import com.example.pawshearts.viewmodel.ProfileViewModel

@Composable
fun ProfileScreen(
    nav: NavHostController,
    profileViewModel: ProfileViewModel = viewModel()
) {
    val uiState by profileViewModel.uiState.collectAsState()
    val tabs = listOf("Bài đăng", "Đã nhận nuôi")

    Column(modifier = Modifier.fillMaxSize()) {
        uiState.userProfile?.let { user ->
            ProfileHeader(user = user)
        }

        TabRow(
            selectedTabIndex = uiState.selectedTabIndex,
            contentColor = OrangeEA
        ) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = uiState.selectedTabIndex == index,
                    onClick = { profileViewModel.onTabSelected(index) },
                    text = { Text(title) },
                    selectedContentColor = OrangeEA,
                    unselectedContentColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
            }
        }

        val postsToShow = if (uiState.selectedTabIndex == 0) {
            uiState.userPosts
        } else {
            uiState.adoptedPosts
        }

        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(postsToShow) { pet ->
                PetGridItem(post = pet, onClick = { nav.goPetDetail(pet.postId) })
            }
        }
    }
}
