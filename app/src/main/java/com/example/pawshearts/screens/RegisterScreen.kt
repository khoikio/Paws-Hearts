package com.example.pawshearts.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue

import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.pawshearts.navmodel.Routes
import com.example.pawshearts.viewmodel.AuthViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(
    navController: NavHostController,
    authViewModel: AuthViewModel = viewModel()
) {
    val uiState by authViewModel.uiState.collectAsState()

    // Lắng nghe trạng thái loginSuccess để điều hướng
    LaunchedEffect(uiState.loginSuccess) {
        if (uiState.loginSuccess) {
            navController.navigate(Routes.HOME) {
                // Xóa tất cả các màn hình trước đó khỏi back stack
                popUpTo(navController.graph.startDestinationId) { inclusive = true }
            }
            // Reset lại trạng thái trong ViewModel sau khi đã điều hướng
            authViewModel.navigationComplete()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Create an Account")
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(
            value = uiState.email,
            onValueChange = { authViewModel.onEmailChange(it) },
            label = { Text("Email") },
            isError = uiState.error != null
        )
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = uiState.pass,
            onValueChange = { authViewModel.onPasswordChange(it) },
            label = { Text("Password") },
            visualTransformation = PasswordVisualTransformation(),
            isError = uiState.error != null
        )
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = uiState.confirmPass,
            onValueChange = { authViewModel.onConfirmPasswordChange(it) },
            label = { Text("Confirm Password") },
            visualTransformation = PasswordVisualTransformation(),
            isError = uiState.error != null
        )

        uiState.error?.let {
            Spacer(modifier = Modifier.height(8.dp))
            Text(color = androidx.compose.ui.graphics.Color.Red, text = it)
        }

        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = { authViewModel.register() },
            enabled = !uiState.isLoading
        ) {
            Text("Register")
        }
        Spacer(modifier = Modifier.height(8.dp))
        Button(onClick = { navController.popBackStack() }) {
            Text("Back to Login")
        }
    }
}
