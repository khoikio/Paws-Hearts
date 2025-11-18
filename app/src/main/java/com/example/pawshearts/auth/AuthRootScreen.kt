package com.example.pawshearts.auth

import android.app.Activity
import android.app.Application
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.pawshearts.R
import com.example.pawshearts.navmodel.Routes
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AuthRootScreen(navController: NavController) {
    val context = LocalContext.current.applicationContext as Application
    val viewModel: AuthViewModel = viewModel(factory = AuthViewModelFactory(context))

    val authState by viewModel.authState.collectAsStateWithLifecycle()
    var isLoginMode by remember { mutableStateOf(true) }

    var emailOrPhone by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    val isLoading = authState is AuthResult.Loading
    val prefilled by viewModel.prefilledCredentials.collectAsStateWithLifecycle()

    // XÓA HOẶC COMMENT OUT CÁI NÀY ĐI
    /*
    LaunchedEffect(authState) {
        if (authState is AuthResult.Success) {
            navController.navigate(Routes.HOME) {
                popUpTo(navController.graph.id) { inclusive = true }
            }
        }
    }
    */

    LaunchedEffect(prefilled) {
        prefilled?.let { (prefillEmail, prefillPassword) ->
            emailOrPhone = prefillEmail
            password = prefillPassword
            isLoginMode = true
            viewModel.clearPrefilledCredentials()
        }
    }

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

    Scaffold(
        bottomBar = {
            Surface(
                modifier = Modifier.fillMaxWidth(),
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
                            "Đăng nhập",
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
                            "Đăng ký",
                            color = if (!isLoginMode) Color.White else Color.DarkGray,
                            fontWeight = if (!isLoginMode) FontWeight.Bold else FontWeight.Normal,
                            style = MaterialTheme.typography.titleMedium
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            Spacer(modifier = Modifier.height(24.dp))
            Text(
                if (isLoginMode) "Paws & Hearts" else "Tạo tài khoản",
                style = MaterialTheme.typography.displayLarge,
                modifier = Modifier.align(Alignment.CenterHorizontally),
                color = if (isLoginMode) Color(0xFFE65100) else Color.Black
            )
            if (!isLoginMode) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    "Đăng Ký để bắt đầu",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray,
                    modifier = Modifier.align(Alignment.Start)
                )
            }
            Spacer(modifier = Modifier.height(24.dp))

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

            if (isLoginMode) {
                LoginTabScreen(
                    viewModel = viewModel,
                    isLoading = isLoading,
                    googleSignInLauncher = googleSignInLauncher,
                    googleSignInClient = googleSignInClient,
                    prefillEmail = emailOrPhone,
                    prefillPassword = password,
                    onEmailOrPhoneChange = { emailOrPhone = it },
                    onPasswordChange = { password = it }
                )
            } else {
                RegisterTabScreen(
                    viewModel = viewModel,
                    isLoading = isLoading,
                    onSwitchToLogin = { isLoginMode = true }
                )
            }
        }
    }
}
