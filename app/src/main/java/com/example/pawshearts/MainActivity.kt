package com.example.pawshearts

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.pawshearts.navmodel.Routes
import com.example.pawshearts.ui.theme.Theme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Theme {
                AppRoot(startDestination = Routes.LOGIN)
            }
        }
    }
}
