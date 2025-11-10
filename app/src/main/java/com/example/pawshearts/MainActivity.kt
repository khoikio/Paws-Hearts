package com.example.pawshearts

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.pawshearts.ui.theme.Theme
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //FirebaseApp.initializeApp(this)
        //FirebaseAuth.getInstance().signOut() // login lại khi chay app
        setContent {
            Theme {
                AppRoot()
            }
        }
    }
}
