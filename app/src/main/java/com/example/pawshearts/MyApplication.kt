package com.example.pawshearts

import android.app.Application
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestoreSettings

class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        // TẮT CHẾ ĐỘ EMULATOR ĐỂ KẾT NỐI SERVER THẬT
        val useEmulator = false 

        if (useEmulator) {
            // DÙNG IP ĐẶC BIỆT 10.0.2.2 ĐỂ MÁY ẢO NÓI CHUYỆN VỚI MÁY TÍNH
            val host = "10.0.2.2" 

            // Cấu hình Firestore Emulator
            val firestore = FirebaseFirestore.getInstance()
            firestore.useEmulator(host, 8080)
            val settings = firestoreSettings {
                isPersistenceEnabled = false
            }
            firestore.firestoreSettings = settings

            // Cấu hình Authentication Emulator
            FirebaseAuth.getInstance().useEmulator(host, 9099)

        } else {
            // Cấu hình cho app chạy thật (kết nối server thật)
            val firestore = FirebaseFirestore.getInstance()
            val settings = firestoreSettings {
                isPersistenceEnabled = true // Bật cache offline cho app thật
            }
            firestore.firestoreSettings = settings
        }
    }
}
