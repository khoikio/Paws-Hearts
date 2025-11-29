package com.example.pawshearts.auth

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterTabScreen(
    viewModel: AuthViewModel,
    isLoading: Boolean,
    onSwitchToLogin: () -> Unit
) {
    // --- CÁC BIẾN NHỚ TRẠNG THÁI CỦA FORM ---
    var registerFullName by remember { mutableStateOf("") }
    var registerEmail by remember { mutableStateOf("") }
    var registerPassword by remember { mutableStateOf("") }
    var registerConfirmPassword by remember { mutableStateOf("") }
    var showRegisterPassword by remember { mutableStateOf(false) }
    var showConfirmPassword by remember { mutableStateOf(false) }
    var agreeToTerms by remember { mutableStateOf(false) }
    val context = LocalContext.current

    Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {

        // --- Ô HỌ VÀ TÊN ---
        OutlinedTextField(
            value = registerFullName,
            onValueChange = { registerFullName = it },
            label = { Text("Họ và tên") },
            leadingIcon = { Icon(Icons.Default.Person, contentDescription = "Full Name") },
            modifier = Modifier.fillMaxWidth(),
            // DÙNG MÀU TỪ THEME
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                focusedLabelColor = MaterialTheme.colorScheme.primary,
                cursorColor = MaterialTheme.colorScheme.primary
            )
        )
        Spacer(modifier = Modifier.height(16.dp))

        // --- Ô EMAIL ---
        val isEmailValid = registerEmail.isBlank() || android.util.Patterns.EMAIL_ADDRESS.matcher(registerEmail).matches()
        OutlinedTextField(
            value = registerEmail,
            onValueChange = { registerEmail = it },
            label = { Text("Email") },
            leadingIcon = { Icon(Icons.Default.Email, contentDescription = "Email") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            modifier = Modifier.fillMaxWidth(),
            isError = !isEmailValid,
            // DÙNG MÀU TỪ THEME
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                focusedLabelColor = MaterialTheme.colorScheme.primary,
                cursorColor = MaterialTheme.colorScheme.primary
            )
        )
        if (!isEmailValid) {
            Text("Email không hợp lệ", color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall, modifier = Modifier.padding(start = 16.dp, top = 4.dp).align(Alignment.Start))
        }
        Spacer(modifier = Modifier.height(12.dp))

        // --- Ô MẬT KHẨU ---
        OutlinedTextField(
            value = registerPassword,
            onValueChange = { registerPassword = it },
            label = { Text("Mật khẩu") },
            leadingIcon = { Icon(Icons.Default.Lock, contentDescription = "Password") },
            trailingIcon = {
                IconButton(onClick = { showRegisterPassword = !showRegisterPassword }) {
                    Icon(imageVector = if (showRegisterPassword) Icons.Default.Visibility else Icons.Default.VisibilityOff, contentDescription = if (showRegisterPassword) "ẩn" else "hiện")
                }
            },
            visualTransformation = if (showRegisterPassword) VisualTransformation.None else PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            modifier = Modifier.fillMaxWidth(),
            // DÙNG MÀU TỪ THEME
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                focusedLabelColor = MaterialTheme.colorScheme.primary,
                cursorColor = MaterialTheme.colorScheme.primary
            )
        )
        Spacer(modifier = Modifier.height(12.dp))

        // --- Ô XÁC MINH MẬT KHẨU ---
        val isPasswordMismatch = registerConfirmPassword.isNotBlank() && registerPassword != registerConfirmPassword
        OutlinedTextField(
            value = registerConfirmPassword,
            onValueChange = { registerConfirmPassword = it },
            label = { Text("Xác minh mật khẩu") },
            leadingIcon = { Icon(Icons.Default.Lock, contentDescription = "Confirm Password") },
            trailingIcon = {
                IconButton(onClick = { showConfirmPassword = !showConfirmPassword }) {
                    Icon(imageVector = if (showConfirmPassword) Icons.Default.Visibility else Icons.Default.VisibilityOff, contentDescription = if (showConfirmPassword) "ẩn" else "hiện")
                }
            },
            visualTransformation = if (showConfirmPassword) VisualTransformation.None else PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            modifier = Modifier.fillMaxWidth(),
            isError = isPasswordMismatch,
            // DÙNG MÀU TỪ THEME
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                focusedLabelColor = MaterialTheme.colorScheme.primary,
                cursorColor = MaterialTheme.colorScheme.primary
            )
        )
        if (isPasswordMismatch) {
            Text("Mật khẩu không khớp", color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall, modifier = Modifier.padding(start = 16.dp, top = 4.dp).align(Alignment.Start))
        }
        Spacer(modifier = Modifier.height(12.dp))

        // --- CHECKBOX ĐIỀU KHOẢN ---
        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Checkbox(
                checked = agreeToTerms,
                onCheckedChange = { agreeToTerms = it },
                // DÙNG MÀU TỪ THEME
                colors = CheckboxDefaults.colors(checkedColor = MaterialTheme.colorScheme.primary)
            )

            val annotatedString = buildAnnotatedString {
                append("Tôi đồng ý với ")
                pushStringAnnotation(tag = "TERMS", annotation = "terms")
                withStyle(style = SpanStyle(color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)) {
                    append("Điều khoản & Điều kiện")
                }
                pop()
            }

            ClickableText(
                text = annotatedString,
                onClick = { offset ->
                    annotatedString.getStringAnnotations(tag = "TERMS", start = offset, end = offset)
                        .firstOrNull()?.let {
                            Log.d("Register", "Điều khoản ")
                        }
                }
            )
        }
        Spacer(modifier = Modifier.height(20.dp))

        // --- NÚT ĐĂNG KÝ ---
        Button(
            onClick = {
                if (!isLoading) {
                    when {
                        registerFullName.isBlank() -> viewModel.setAuthError("Vui lòng nhập họ tên")
                        registerEmail.isBlank() -> viewModel.setAuthError("Vui lòng nhập email")
                        !android.util.Patterns.EMAIL_ADDRESS.matcher(registerEmail).matches() -> viewModel.setAuthError("Email không hợp lệ")
                        registerPassword.isBlank() -> viewModel.setAuthError("Vui lòng nhập mật khẩu")
                        registerPassword.length < 6 -> viewModel.setAuthError("Mật khẩu phải có ít nhất 6 ký tự")
                        registerPassword != registerConfirmPassword -> viewModel.setAuthError("Mật khẩu nhập lại không khớp")
                        !agreeToTerms -> viewModel.setAuthError("Bạn phải đồng ý với điều khoản")
                        else -> {
                            viewModel.registerWithEmail(
                                registerEmail.trim(),
                                registerPassword,
                                registerFullName.trim()
                            )
                        }
                    }
                }
            },
            enabled = !isLoading,
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
                Text("Đăng ký", style = MaterialTheme.typography.titleMedium)
            }
        }
    }
}
