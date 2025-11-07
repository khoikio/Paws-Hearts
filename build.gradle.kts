// File: build.gradle.kts (NGOÀI CÙNG - BẢN "CHỐT HẠ")

plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.ksp) apply false
    id("com.google.gms.google-services") version "4.4.4" apply false

    // --- THÊM 2 DÒNG "MA" MÀY THIẾU VÀO ĐÂY ---
    alias(libs.plugins.protobuf) apply false // (CHO DATASTORE)
}