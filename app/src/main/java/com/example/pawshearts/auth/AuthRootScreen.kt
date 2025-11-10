package com.example.pawshearts.auth

import android.app.Activity
import android.app.Application // <-- T THÊM IMPORT NÀY
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
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.ui.text.font.FontWeight
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.pawshearts.R
import com.example.pawshearts.navmodel.Routes
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import androidx.navigation.NavController
// XÓA CÁI IMPORT AuthRepositoryImpl ĐI
// import com.example.pawshearts.auth.AuthRepositoryImpl // <-- XÓA CÁI NÀY

// T THÊM 2 IMPORT NÀY
import com.example.pawshearts.auth.AuthViewModel
import com.example.pawshearts.auth.AuthViewModelFactory




@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AuthRootScreen(navController: NavController) {
    // T BỎ CÁI AuthViewModelProvider CỦA M RA
    // AuthViewModelProvider { viewModel -> // <-- BỎ CÁI NÀY

    val context = LocalContext.current

    // --- LẤY VIEWMODEL THEO CÁCH ĐÚNG NÈ KKK ---
    val application = context.applicationContext as Application
    val authViewModelFactory = AuthViewModelFactory(application)
    val viewModel: AuthViewModel = viewModel(factory = authViewModelFactory)
    // ----------------------------------------------

    val authState by viewModel.authState.collectAsStateWithLifecycle()
    val isLoggedIn by viewModel.isUserLoggedIn.collectAsStateWithLifecycle()

    // --- GLOBAL STATES ---
    var isLoginMode by remember { mutableStateOf(true) } // <-- State Chuyển đổi Tab

    // Register fields (Cần giữ lại để sau khi đăng ký thành công, form cũ không bị mất data)
    var registerFullName by remember { mutableStateOf("") }
    // ... (Bạn có thể giữ lại các state đăng ký khác ở đây nếu cần)

    // Login fields (Cần giữ lại cho prefill)
    var emailOrPhone by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var showPassword by remember { mutableStateOf(false) } // <-- Chỉ cần giữ lại cái này

    val isLoading = authState is AuthResult.Loading
    val prefilled by viewModel.prefilledCredentials.collectAsStateWithLifecycle()

    // Navigate when logged in
    LaunchedEffect(isLoggedIn) {
        if (isLoggedIn) {
            navController.navigate(Routes.HOME) {
                popUpTo(navController.graph.id) { inclusive = true }
            }
        }
    }

    // Auto-Fill logic (Sau khi Register thành công)
    LaunchedEffect(prefilled) {
        prefilled?.let { (prefillEmail, prefillPassword) ->
            emailOrPhone = prefillEmail
            password = prefillPassword
            isLoginMode = true // <-- Chuyển về Tab Login
            viewModel.clearPrefilledCredentials()
        }
    }

    // --- GOOGLE SIGN-IN SETUP ---
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

    // --- BOX CHỨA FORM VÀ BOTTOM BAR ---
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
                if (isLoginMode) "Paws & Hearts" else "Tạo tài khoản",
                style = MaterialTheme.typography.headlineLarge.copy(fontWeight = FontWeight.Bold),
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

            // Error message
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

            // --- CHỖ GỌI 2 MÀN HÌNH TÁCH RA ---
            if (isLoginMode) {
                LoginTabScreen(
                    viewModel = viewModel,
                    isLoading = isLoading,
                    googleSignInLauncher = googleSignInLauncher,
                    googleSignInClient = googleSignInClient,
                    prefillEmail = emailOrPhone, // Truyền State Prefill/Input
                    prefillPassword = password,
                    onEmailOrPhoneChange = { emailOrPhone = it }, // Cập nhật State
                    onPasswordChange = { password = it } // Cập nhật State
                )
            } else {
                RegisterTabScreen(
                    viewModel = viewModel,
                    isLoading = isLoading,
                    onSwitchToLogin = { isLoginMode = true } // Callback để về Login
                )
            }
        }

        // --- BOTTOM FIXED TAB (LOGIC SWITCH) ---
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
}