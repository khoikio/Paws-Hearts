package com.example.pawshearts.auth

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.example.pawshearts.R
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.result.ActivityResult
import androidx.compose.foundation.Image
import androidx.compose.foundation.shape.RoundedCornerShape
import android.content.Intent
import androidx.compose.ui.text.font.FontWeight // <-- T THÊM CÁI NÀY

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginTabScreen(
    viewModel: AuthViewModel,
    isLoading: Boolean,
    googleSignInLauncher: ManagedActivityResultLauncher<Intent, ActivityResult>,
    googleSignInClient: GoogleSignInClient,
    prefillEmail: String,
    prefillPassword: String,
    onEmailOrPhoneChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit
) {
    // Local state for visibility (Mắt)
    var showPassword by remember { mutableStateOf(false) }
    val emailOrPhone = prefillEmail
    val password = prefillPassword

    // LOGIN FORM UI
    Column(modifier = Modifier.fillMaxWidth()) {
        OutlinedTextField(
            value = emailOrPhone,
            onValueChange = onEmailOrPhoneChange,
            label = { Text("Email") },
            leadingIcon = { Icon(Icons.Default.Email, contentDescription = "Email") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = password,
            onValueChange = onPasswordChange,
            label = { Text("Nhập mật khẩu") },
            leadingIcon = { Icon(Icons.Default.Lock, contentDescription = "Password") },
            trailingIcon = {
                IconButton(onClick = { showPassword = !showPassword }) {
                    Icon(
                        imageVector = if (showPassword) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                        contentDescription = if (showPassword) "Hide password" else "Show password"
                    )
                }
            },
            visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(12.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                // Checkbox Remember me
                var rememberMe by remember { mutableStateOf(false) }
                Checkbox(checked = rememberMe, onCheckedChange = { rememberMe = it })
                Text("Ghi Nhớ tôi")
            }
            Text(
                "Quên mật khẩu?",
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.clickable { /* TODO: Forgot password */ }
            )
        }
        Spacer(modifier = Modifier.height(20.dp))

        // Sign in button
        Button(
            onClick = {
                if (!isLoading) {
                    viewModel.loginWithEmail(emailOrPhone.trim(), password)
                }
            },
            enabled = !isLoading && emailOrPhone.isNotBlank() && password.isNotBlank(),
            modifier = Modifier.fillMaxWidth().height(50.dp),
            shape = RoundedCornerShape(8.dp)
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    color = Color.White,
                    modifier = Modifier.size(20.dp),
                    strokeWidth = 2.dp
                )
            } else {
                Text("Đăng nhập", style = MaterialTheme.typography.titleMedium)
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // --- SOCIAL BUTTONS AND DIVIDER ---
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Divider(modifier = Modifier.weight(1f), color = Color.LightGray)

            Text(
                "Hoặc đăng nhập với",
                style = MaterialTheme.typography.labelMedium,
                color = Color(0xFFE65100), // Màu cam
                fontWeight = FontWeight.Bold
            )
            Divider(modifier = Modifier.weight(1f), color = Color.LightGray)
        }
        Spacer(modifier = Modifier.height(16.dp))

        // Google Sign-In button
        Button(
            onClick = {
                if (!isLoading) {
                    val signInIntent = googleSignInClient.signInIntent
                    googleSignInLauncher.launch(signInIntent)
                }
            },
            enabled = !isLoading,
            modifier = Modifier.fillMaxWidth().height(50.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF0F0F0)),
            shape = RoundedCornerShape(8.dp)
        ) {
            Image(
                painter = painterResource(id = R.drawable.gg),
                contentDescription = "Google",
                modifier = Modifier.size(24.dp)
            )
            Spacer(Modifier.width(8.dp))
            Text(
                "Đăng nhập với Google",
                color = Color.Black,
                style = MaterialTheme.typography.titleMedium
            )
        }


    }
}