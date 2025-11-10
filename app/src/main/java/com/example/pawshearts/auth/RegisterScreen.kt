package com.example.pawshearts.auth

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.ui.platform.LocalContext // <-- T THÊM CÁI NÀY
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterTabScreen(
    viewModel: AuthViewModel,
    isLoading: Boolean,
    onSwitchToLogin: () -> Unit
) {
    // --- REGISTER FIELDS LOCAL STAT
    var registerFullName by remember { mutableStateOf("") }
    var registerEmail by remember { mutableStateOf("") }
    var registerPassword by remember { mutableStateOf("") }
    var registerConfirmPassword by remember { mutableStateOf("") }
    var showRegisterPassword by remember { mutableStateOf(false) }
    var showConfirmPassword by remember { mutableStateOf(false) }
    var agreeToTerms by remember { mutableStateOf(false) }

    // CÁI NÀY ĐỂ CHECK EMAIL HỢP LỆ
    val context = LocalContext.current

    // REGISTER FORM UI
    Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
        OutlinedTextField(
            value = registerFullName,
            onValueChange = { registerFullName = it },
            label = { Text("Họ và tên") },
            leadingIcon = { Icon(Icons.Default.Person, contentDescription = "Full Name") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))
        val isEmailValid = registerEmail.isBlank() || android.util.Patterns.EMAIL_ADDRESS.matcher(registerEmail).matches()
        OutlinedTextField(
            value = registerEmail,
            onValueChange = { registerEmail = it },
            label = { Text("Email") },
            leadingIcon = { Icon(Icons.Default.Email, contentDescription = "Email") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            modifier = Modifier.fillMaxWidth(),
            isError = !isEmailValid
        )
        if (!isEmailValid) {
            Text(
                "Email không hợp lệ",
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(start = 16.dp, top = 4.dp).align(Alignment.Start)
            )
        }
        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = registerPassword,
            onValueChange = { registerPassword = it },
            label = { Text("Mật khẩu") },
            leadingIcon = { Icon(Icons.Default.Lock, contentDescription = "Password") },
            trailingIcon = {
                IconButton(onClick = { showRegisterPassword = !showRegisterPassword }) {
                    Icon(
                        imageVector = if (showRegisterPassword) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                        contentDescription = if (showRegisterPassword) "ẩn" else "hiện"
                    )
                }
            },
            visualTransformation = if (showRegisterPassword) VisualTransformation.None else PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(12.dp))

        // T SỬA LẠI CÁCH CHECK PASS CỦA M 1 TÍ
        val isPasswordMismatch = registerConfirmPassword.isNotBlank() && registerPassword != registerConfirmPassword
        OutlinedTextField(
            value = registerConfirmPassword,
            onValueChange = { registerConfirmPassword = it },
            label = { Text("Xác minh mật khẩu") },
            leadingIcon = { Icon(Icons.Default.Lock, contentDescription = "Confirm Password") },
            trailingIcon = {
                IconButton(onClick = { showConfirmPassword = !showConfirmPassword }) {
                    Icon(
                        imageVector = if (showConfirmPassword) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                        contentDescription = if (showConfirmPassword) "ẩn" else "hiện"
                    )
                }
            },
            visualTransformation = if (showConfirmPassword) VisualTransformation.None else PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            modifier = Modifier.fillMaxWidth(),
            isError = isPasswordMismatch
        )
        if (isPasswordMismatch) {
            Text(
                "Mật khẩu không khớp",
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(start = 16.dp, top = 4.dp).align(Alignment.Start)
            )
        }
        Spacer(modifier = Modifier.height(12.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(checked = agreeToTerms, onCheckedChange = { agreeToTerms = it })

            // T GỘP 2 CÁI TEXT LẠI LÀM 1 CHO NÓ XỊN KKK :D
            val annotatedString = buildAnnotatedString {
                append("Tôi đồng ý với ")
                pushStringAnnotation(tag = "TERMS", annotation = "terms") // Đánh dấu
                withStyle(style = SpanStyle(
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )
                ) {
                    append("Điều khoản & Điều kiện")
                }
                pop()
            }

            ClickableText(
                text = annotatedString,
                onClick = { offset ->
                    annotatedString.getStringAnnotations(tag = "TERMS", start = offset, end = offset)
                        .firstOrNull()?.let {
                            //  code  mở link web ở đây
                            Log.d("Register", "Điều khoản ")
                        }
                }
            )
        }
        Spacer(modifier = Modifier.height(20.dp))

        // Nút Sign Up
        Button(
            onClick = {
                if (!isLoading) {
                    // LỖI 2: T SỬA LẠI LOGIC NÚT ĐĂNG KÝ
                    when {
                        registerFullName.isBlank() -> viewModel.setAuthError("Vui lòng nhập họ tên")
                        registerEmail.isBlank() -> viewModel.setAuthError("Vui lòng nhập email")
                        !android.util.Patterns.EMAIL_ADDRESS.matcher(registerEmail).matches() ->
                            viewModel.setAuthError("Email không hợp lệ")
                        registerPassword.isBlank() -> viewModel.setAuthError("Vui lòng nhập mật khẩu")
                        registerPassword.length < 6 -> viewModel.setAuthError("Mật khẩu phải có ít nhất 6 ký tự")
                        registerPassword != registerConfirmPassword ->
                            viewModel.setAuthError("Mật khẩu nhập lại không khớp")
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
                    color = Color.White,
                    modifier = Modifier.size(20.dp),
                    strokeWidth = 2.dp
                )
            } else {
                Text("Đăng ký", style = MaterialTheme.typography.titleMedium)
            }
        }
    }
}