// Trong file settings.gradle.kts

pluginManagement {
    repositories {
        // Đặt các kho lưu trữ theo thứ tự ưu tiên này.
        // Gradle sẽ tìm từ trên xuống dưới.
        gradlePluginPortal() // Kho plugin chính thức của Gradle
        google()             // Kho của Google
        mavenCentral()       // Kho cộng đồng lớn
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        // Bạn có thể thêm các kho khác ở đây nếu cần, ví dụ:
        // maven { url = uri("https://jitpack.io") }
    }
}

// Giữ nguyên phần còn lại
rootProject.name = "Paws_Hearts"
include(":app")
