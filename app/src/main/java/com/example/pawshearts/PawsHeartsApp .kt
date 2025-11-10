package com.example.pawshearts

import android.app.Application
import com.google.firebase.FirebaseApp

class PawsHeartsApp : Application() {
    override fun onCreate() {
        super.onCreate()

        // KHỞI TẠO FIREBASE 1 LẦN DUY NHẤT Ở ĐÂY NÈ M :D
        FirebaseApp.initializeApp(this)
    }
}