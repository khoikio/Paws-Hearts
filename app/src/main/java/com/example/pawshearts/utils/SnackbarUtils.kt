package com.example.pawshearts.utils

import androidx.compose.material3.SnackbarHostState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

fun showSnackbar(scope: CoroutineScope, snackbarHostState: SnackbarHostState, message: String) {
    scope.launch {
        snackbarHostState.showSnackbar(message)
    }
}
