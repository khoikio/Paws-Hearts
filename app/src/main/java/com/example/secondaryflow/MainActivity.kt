package com.example.secondaryflow

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.example.secondaryflow.ui.components.BottomNavigationBarExample
import com.example.secondaryflow.ui.theme.SecondaryFlowTheme
import com.google.firebase.FirebaseApp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FirebaseApp.initializeApp(this) // ✅ KHỞI TẠO FIREBASE CHO APP CỦA BẠN

        enableEdgeToEdge()
        setContent {
            SecondaryFlowTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    BottomNavigationBarExample()
                }
            }
        }
    }
}

