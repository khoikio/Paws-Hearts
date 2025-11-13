# =================================================================================
# QUY TẮC CHO DATA MODEL (GIỮ NGUYÊN CỦA MÀY, RẤT TỐT)
# =================================================================================
-keep class com.example.pawshearts.data.model.** { *; }
-keepnames class com.example.pawshearts.data.model.** { *; }
-keepclassmembers class com.example.pawshearts.data.model.** {
    <init>(...);
}

# =================================================================================
# QUY TẮC BẮT BUỘC CHO JETPACK COMPOSE (ĐỂ KHÔNG BỊ CRASH)
# =================================================================================
-keepclassmembers class * {
    @androidx.compose.runtime.Composable <methods>;
}
-keepclass @androidx.compose.runtime.Composable, androidx.compose.runtime.Composer

# Giữ lại tên của các hàm @Composable để hệ thống có thể gọi đúng.
# Dòng này đã được sửa lại cho đúng.
-keepnames,allowshrinking @androidx.compose.runtime.Composable public fun *

# Giữ lại các class và thành viên liên quan đến các tiện ích của Compose.
-keepclass class androidx.compose.ui.util.** { *; }
-keepclass class androidx.compose.ui.text.** { *; }

# Bỏ qua các cảnh báo không cần thiết từ các lớp nội bộ của Compose.
-dontwarn androidx.compose.runtime.internal.ComposableLambda
-dontwarn androidx.compose.runtime.ComposablesKt
-dontwarn androidx.compose.runtime.ComposerKt

# =================================================================================
# QUY TẮC CHO CÁC THƯ VIỆN KHÁC (Firebase, Coroutines...)
# =================================================================================
# Giữ lại các quy tắc mặc định cho Firebase.
-keep class com.google.firebase.** { *; }
-keep class com.google.android.gms.common.** { *; }

# Giữ lại các quy tắc cho Coroutines.
-keepnames class kotlinx.coroutines.internal.** { *; }
-keepclassmembers class kotlinx.coroutines.flow.** {
    public <init>(...);
}
-keepclassmembers class **.R$* {
    public static <fields>;
}
