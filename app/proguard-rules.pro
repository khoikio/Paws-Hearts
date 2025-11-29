# ===================================================================
# QUY TẮC BẢO VỆ DATA MODEL
# Giữ lại các class, tên, và hàm khởi tạo cho Firebase/Room.
# ===================================================================
-keep class com.example.pawshearts.data.model.** { *; }
-keepnames class com.example.pawshearts.data.model.** { *; }
-keepclassmembers class com.example.pawshearts.data.model.** {
    <init>(...);
}

# (Mày có thể thêm các quy tắc khác cho Compose, Coroutines... ở đây nếu sau này app crash,
# nhưng quy tắc trên là quan trọng nhất để sửa lỗi loading)
