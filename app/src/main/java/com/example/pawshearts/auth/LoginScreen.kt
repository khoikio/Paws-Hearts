package com.example.pawshearts.auth

import android.content.Intent
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.result.ActivityResult
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.example.pawshearts.R
import com.google.android.gms.auth.api.signin.GoogleSignInClient

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
    var showPassword by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxWidth()) {
        // --- Ô NHẬP EMAIL ---
        OutlinedTextField(
            value = prefillEmail,
            onValueChange = onEmailOrPhoneChange,
            label = { Text("Email") },
            leadingIcon = { Icon(Icons.Default.Email, contentDescription = "Email") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            // DÙNG MÀU TỪ THEME
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                focusedLabelColor = MaterialTheme.colorScheme.primary,
                cursorColor = MaterialTheme.colorScheme.primary
            )
        )
        Spacer(modifier = Modifier.height(16.dp))

        // --- Ô NHẬP MẬT KHẨU ---
        OutlinedTextField(
            value = prefillPassword,
            onValueChange = onPasswordChange,
            label = { Text("Nhập mật khẩu") },
            leadingIcon = { Icon(Icons.Default.Lock, contentDescription = "Password") },
            trailingIcon = {
                IconButton(onClick = { showPassword = !showPassword }) {
                    Icon(
                        imageVector = if (showPassword) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                        contentDescription = if (showPassword) "Ẩn mật khẩu" else "Hiện mật khẩu"
                    )
                }
            },
            visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            // DÙNG MÀU TỪ THEME
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                focusedLabelColor = MaterialTheme.colorScheme.primary,
                cursorColor = MaterialTheme.colorScheme.primary
            )
        )
        Spacer(modifier = Modifier.height(12.dp))

        // --- GHI NHỚ & QUÊN MẬT KHẨU ---
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                var rememberMe by remember { mutableStateOf(false) }
                Checkbox(
                    checked = rememberMe,
                    onCheckedChange = { rememberMe = it },
                    // DÙNG MÀU TỪ THEME
                    colors = CheckboxDefaults.colors(checkedColor = MaterialTheme.colorScheme.primary)
                )
                Text(
                    "Ghi Nhớ tôi",
                    color = MaterialTheme.colorScheme.onBackground // Dùng màu từ theme
                )
            }
            Text(
                "Quên mật khẩu?",
                color = MaterialTheme.colorScheme.primary, // Dùng màu từ theme
                modifier = Modifier.clickable { /* TODO: Forgot password */ }
            )
        }
        Spacer(modifier = Modifier.height(20.dp))

        // --- NÚT ĐĂNG NHẬP ---
        Button(
            onClick = {
                if (!isLoading) {
                    viewModel.loginWithEmail(prefillEmail.trim(), prefillPassword)
                }
            },
            enabled = !isLoading && prefillEmail.isNotBlank() && prefillPassword.isNotBlank(),
            modifier = Modifier.fillMaxWidth().height(50.dp),
            shape = RoundedCornerShape(8.dp)
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    // DÙNG MÀU TỪ THEME
                    color = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.size(20.dp),
                    strokeWidth = 2.dp
                )
            } else {
                Text("Đăng nhập", style = MaterialTheme.typography.titleMedium)
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // --- ĐƯỜNG KẺ VÀ CHỮ ---
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // DÙNG MÀU TỪ THEME
            Divider(modifier = Modifier.weight(1f), color = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f))
            Text(
                "Hoặc đăng nhập với",
                style = MaterialTheme.typography.labelMedium,
                // DÙNG MÀU TỪ THEME
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold
            )
            Divider(modifier = Modifier.weight(1f), color = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f))
        }
        Spacer(modifier = Modifier.height(16.dp))

        // --- NÚT ĐĂNG NHẬP VỚI GOOGLE ---
        OutlinedButton(
            onClick = {
                if (!isLoading) {
                    val signInIntent = googleSignInClient.signInIntent
                    googleSignInLauncher.launch(signInIntent)
                }
            },
            enabled = !isLoading,
            modifier = Modifier.fillMaxWidth().height(50.dp),
            shape = RoundedCornerShape(8.dp),
            // DÙNG MÀU TỪ THEME
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f))
        ) {
            Image(
                painter = painterResource(id = R.drawable.gg),
                contentDescription = "Google",
                modifier = Modifier.size(24.dp)
            )
            Spacer(Modifier.width(8.dp))
            Text(
                "Đăng nhập với Google",
                style = MaterialTheme.typography.titleMedium,
                // DÙNG MÀU TỪ THEME
                color = MaterialTheme.colorScheme.onBackground
            )
        }
    }
}
