package com.example.pawshearts.auth

import android.app.Activity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.pawshearts.R
import com.example.pawshearts.navmodel.Routes
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException

@Composable
fun AuthViewModelProvider(content: @Composable (AuthViewModel) -> Unit) {
    val repository = remember { AuthRepositoryImpl() }
    val authViewModel: AuthViewModel = viewModel(factory = object : androidx.lifecycle.ViewModelProvider.Factory {
        override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(AuthViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return AuthViewModel(repository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    })
    content(authViewModel)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(navController: NavController) {
    AuthViewModelProvider { viewModel ->
        val context = LocalContext.current
        // authState is nullable: null = idle
        val authState by viewModel.authState.collectAsStateWithLifecycle()
        val isLoggedIn by viewModel.isUserLoggedIn.collectAsStateWithLifecycle()

        // UI states
        var isLoginMode by remember { mutableStateOf(true) }

        // Login fields
        var emailOrPhone by remember { mutableStateOf("") }
        var password by remember { mutableStateOf("") }
        var rememberMe by remember { mutableStateOf(false) }
        var showPassword by remember { mutableStateOf(false) }

        // Register fields
        var registerFullName by remember { mutableStateOf("") }
        var registerEmail by remember { mutableStateOf("") }
        var registerPassword by remember { mutableStateOf("") }
        var registerConfirmPassword by remember { mutableStateOf("") }
        var showRegisterPassword by remember { mutableStateOf(false) }
        var showConfirmPassword by remember { mutableStateOf(false) }
        var agreeToTerms by remember { mutableStateOf(false) }

        // Loading when authState is Loading
        val isLoading = authState is AuthResult.Loading

        // Navigate when logged in
        LaunchedEffect(isLoggedIn) {
            if (isLoggedIn) {
                navController.navigate(Routes.HOME) {
                    popUpTo(navController.graph.id) { inclusive = true }
                }
            }
        }

        // Google Sign-In setup
        val gso = remember {
            GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(context.getString(R.string.default_web_client_id))
                .requestEmail()
                .build()
        }
        val googleSignInClient = remember { GoogleSignIn.getClient(context, gso) }

        val googleSignInLauncher = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.StartActivityForResult(),
            onResult = { result ->
                // Only handle OK result; if user cancels, do nothing (avoids setting error/loading)
                if (result.resultCode == Activity.RESULT_OK) {
                    val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
                    try {
                        val account = task.getResult(ApiException::class.java)
                        val idToken = account?.idToken
                        if (!idToken.isNullOrEmpty()) {
                            viewModel.signInWithGoogle(idToken)
                        } else {
                            viewModel.setAuthError("Không lấy được ID Token từ Google.")
                        }
                    } catch (e: ApiException) {
                        viewModel.setAuthError("Đăng nhập Google thất bại: ${e.message ?: e.statusCode}")
                    } catch (e: Exception) {
                        viewModel.setAuthError(e.message ?: "Lỗi Google Sign-In")
                    }
                }
            }
        )

        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 32.dp)
                    .padding(top = 24.dp, bottom = 120.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Top
            ) {
                // Header
                Text(
                    if (isLoginMode) "Nice to see you again" else "Create Account",
                    style = MaterialTheme.typography.headlineLarge.copy(fontWeight = FontWeight.Bold),
                    modifier = Modifier.align(Alignment.Start)
                )
                if (!isLoginMode) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        "Sign up to get started",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.Gray,
                        modifier = Modifier.align(Alignment.Start)
                    )
                }
                Spacer(modifier = Modifier.height(24.dp))

                // Error message (only show when Error)
                if (authState is AuthResult.Error) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFEBEE))
                    ) {
                        Text(
                            text = (authState as AuthResult.Error).message,
                            color = Color(0xFFC62828),
                            modifier = Modifier.padding(12.dp)
                        )
                    }
                }

                // Form content
                if (isLoginMode) {
                    // LOGIN FORM
                    OutlinedTextField(
                        value = emailOrPhone,
                        onValueChange = { emailOrPhone = it },
                        label = { Text("Email") },
                        leadingIcon = { Icon(Icons.Default.Email, contentDescription = "Email") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        label = { Text("Enter password") },
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
                            Checkbox(checked = rememberMe, onCheckedChange = { rememberMe = it })
                            Text("Remember me")
                        }
                        Text(
                            "Forgot password?",
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.clickable { /* TODO: Forgot password */ }
                        )
                    }
                    Spacer(modifier = Modifier.height(20.dp))

                    // Sign in button
                    Button(
                        onClick = {
                            if (!isLoading) {
                                viewModel.login(emailOrPhone.trim(), password)
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
                            Text("Sign in", style = MaterialTheme.typography.titleMedium)
                        }
                    }
                } else {
                    // REGISTER FORM
                    OutlinedTextField(
                        value = registerFullName,
                        onValueChange = { registerFullName = it },
                        label = { Text("Full Name") },
                        leadingIcon = { Icon(Icons.Default.Person, contentDescription = "Full Name") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedTextField(
                        value = registerEmail,
                        onValueChange = { registerEmail = it },
                        label = { Text("Email") },
                        leadingIcon = { Icon(Icons.Default.Email, contentDescription = "Email") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                        modifier = Modifier.fillMaxWidth(),
                        isError = registerEmail.isNotBlank() && !android.util.Patterns.EMAIL_ADDRESS.matcher(registerEmail).matches()
                    )
                    if (registerEmail.isNotBlank() && !android.util.Patterns.EMAIL_ADDRESS.matcher(registerEmail).matches()) {
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
                        label = { Text("Password") },
                        leadingIcon = { Icon(Icons.Default.Lock, contentDescription = "Password") },
                        trailingIcon = {
                            IconButton(onClick = { showRegisterPassword = !showRegisterPassword }) {
                                Icon(
                                    imageVector = if (showRegisterPassword) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                    contentDescription = if (showRegisterPassword) "Hide" else "Show"
                                )
                            }
                        },
                        visualTransformation = if (showRegisterPassword) VisualTransformation.None else PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = registerConfirmPassword,
                        onValueChange = { registerConfirmPassword = it },
                        label = { Text("Confirm Password") },
                        leadingIcon = { Icon(Icons.Default.Lock, contentDescription = "Confirm Password") },
                        trailingIcon = {
                            IconButton(onClick = { showConfirmPassword = !showConfirmPassword }) {
                                Icon(
                                    imageVector = if (showConfirmPassword) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                    contentDescription = if (showConfirmPassword) "Hide" else "Show"
                                )
                            }
                        },
                        visualTransformation = if (showConfirmPassword) VisualTransformation.None else PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        modifier = Modifier.fillMaxWidth(),
                        isError = registerConfirmPassword.isNotBlank() && registerPassword != registerConfirmPassword
                    )
                    if (registerConfirmPassword.isNotBlank() && registerPassword != registerConfirmPassword) {
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
                        Text("I agree to the ")
                        Text(
                            "Terms & Conditions",
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.clickable { /* TODO */ }
                        )
                    }
                    Spacer(modifier = Modifier.height(20.dp))

                    Button(
                        onClick = {
                            if (!isLoading) {
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
                                        // register and switch to login on success (UI callback)
                                        viewModel.registerAndSwitchToLogin(registerEmail.trim(), registerPassword) {
                                            isLoginMode = true
                                            registerFullName = ""
                                            registerEmail = ""
                                            registerPassword = ""
                                            registerConfirmPassword = ""
                                            agreeToTerms = false
                                        }
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
                            Text("Sign Up", style = MaterialTheme.typography.titleMedium)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Divider
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Divider(modifier = Modifier.weight(1f), color = Color.LightGray)
                    Text(
                        " Or sign ${if (isLoginMode) "in" else "up"} with ",
                        style = MaterialTheme.typography.labelMedium,
                        color = Color.Gray
                    )
                    Divider(modifier = Modifier.weight(1f), color = Color.LightGray)
                }
                Spacer(modifier = Modifier.height(16.dp))

                // Google Sign-In button
                Button(
                    onClick = {
                        if (!isLoading) {
                            googleSignInLauncher.launch(googleSignInClient.signInIntent)
                        }
                    },
                    enabled = !isLoading,
                    modifier = Modifier.fillMaxWidth().height(50.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF0F0F0)),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(20.dp),
                            strokeWidth = 2.dp
                        )
                    } else {
                        Image(
                            painter = painterResource(id = R.drawable.gg),
                            contentDescription = "Google",
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(Modifier.width(8.dp))
                        Text(
                            "Sign ${if (isLoginMode) "in" else "up"} with Google",
                            color = Color.Black,
                            style = MaterialTheme.typography.titleMedium
                        )
                    }
                }

                // Facebook button only on login (kept for UI placeholder)
                if (isLoginMode) {
                    Spacer(Modifier.height(12.dp))
                    Button(
                        onClick = { /* TODO: Facebook Auth */ },
                        enabled = !isLoading,
                        modifier = Modifier.fillMaxWidth().height(50.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF3B5998)),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.fb),
                            contentDescription = "Facebook",
                            tint = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(Modifier.width(8.dp))
                        Text("Sign in with Facebook", color = Color.White)
                    }
                }
            }
            // Bottom fixed tab (Login / Register)
            Surface(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth(),
                color = MaterialTheme.colorScheme.surface,
                shadowElevation = 8.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Button(
                        onClick = { isLoginMode = true },
                        modifier = Modifier.weight(1f).height(50.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isLoginMode) MaterialTheme.colorScheme.primary else Color(0xFFE0E0E0)
                        ),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            "Login",
                            color = if (isLoginMode) Color.White else Color.DarkGray,
                            fontWeight = if (isLoginMode) FontWeight.Bold else FontWeight.Normal,
                            style = MaterialTheme.typography.titleMedium
                        )
                    }
                    Button(
                        onClick = { isLoginMode = false },
                        modifier = Modifier.weight(1f).height(50.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (!isLoginMode) MaterialTheme.colorScheme.primary else Color(0xFFE0E0E0)
                        ),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            "Register",
                            color = if (!isLoginMode) Color.White else Color.DarkGray,
                            fontWeight = if (!isLoginMode) FontWeight.Bold else FontWeight.Normal,
                            style = MaterialTheme.typography.titleMedium
                        )
                    }
                }
            }
        }
    }
}