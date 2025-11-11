package com.example.pawshearts

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.core.view.WindowCompat
import com.example.pawshearts.navmodel.AppRoot
import com.example.pawshearts.ui.theme.Theme
import com.google.firebase.auth.FirebaseAuth

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false) // cho tràn viền ở topbar
        //FirebaseApp.initializeApp(this)
        //FirebaseAuth.getInstance().signOut() // login lại khi chay app
        setContent {
            Theme {
                AppRoot()
            }
        }
    }
}
