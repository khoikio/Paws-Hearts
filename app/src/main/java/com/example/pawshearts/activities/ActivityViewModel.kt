package com.example.pawshearts.activities

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pawshearts.auth.AuthResult
import com.example.pawshearts.data.model.Activity // M nhớ import Activity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ActivityViewModel(
    private val repository: ActivityRepository
) : ViewModel() {

    // T VỚI M GIỮ LIST HOẠT ĐỘNG
    private val _activities = MutableStateFlow<List<Activity>>(emptyList())
    val activities: StateFlow<List<Activity>> = _activities

    // T VỚI M GIỮ KẾT QUẢ TẠO BÀI CHO ADMIN
    private val _createResult = MutableStateFlow<AuthResult<Unit>?>(null)
    val createResult: StateFlow<AuthResult<Unit>?> = _createResult

    // Giữ chi tiết của MỘT hoạt động đang được chọn để xem/sửa
    private val _selectedActivity = MutableStateFlow<Activity?>(null)
    val selectedActivity: StateFlow<Activity?> = _selectedActivity

    // Thêm biến state để biết là user đã đăng ký hay chưa
    private val _isRegistered = MutableStateFlow(false)
    val isRegistered: StateFlow<Boolean> = _isRegistered.asStateFlow()
    private val _registerState = MutableStateFlow<AuthResult<Unit>?>(null)
    val registerState: StateFlow<AuthResult<Unit>?> = _registerState.asStateFlow()
    //  TỰ GỌI CÁI NÀY LÚC VÀO APP
    init {
        fetchActivities()
    }

    private fun fetchActivities() {
        viewModelScope.launch {
            // SỬA LẠI CHO ĐÚNG TÊN HÀM TRONG REPO CỦA M
            repository.getAllActivitiesFlow().collect { activities ->
                _activities.value = activities
            }
        }
    }

    // Hàm này cho Admin tạo
    fun createActivity(activity: Activity) {
        _createResult.value = AuthResult.Loading
        viewModelScope.launch {
            // SỬA LẠI CHO ĐÚNG TÊN HÀM TRONG REPO CỦA M
            val result =repository.createActivity(activity)
            // Có thể không cần result ở đây nếu createActivity không trả về gì
            _createResult.value = result
        }
    }
    fun updateActivity(activity: Activity) { // <-- LỖI CỦA BẠN SẼ HẾT Ở ĐÂY
        _createResult.value = AuthResult.Loading
        viewModelScope.launch {
            // Bạn sẽ cần thêm hàm updateActivity vào Repository ở bước sau
            val result = repository.updateActivity(activity)
            _createResult.value = result
        }
    }
    // ******** DÁN HÀM MỚI VÀO ĐÂY ********
    // Hàm này cho Admin xóa
    fun deleteActivity(activityId: String) {
        viewModelScope.launch {
            repository.deleteActivity(activityId)
        }
    }
    // ******** KẾT THÚC HÀM MỚI ********
    fun getActivityById(activityId: String) {
        viewModelScope.launch {
            // Hiển thị trạng thái loading bằng cách set state là null
            _selectedActivity.value = null

            // Gọi Repository để lấy dữ liệu mới và chính xác nhất từ Firebase
            val activityFromRepo = repository.getActivityById(activityId)

            if (activityFromRepo != null) {
                _selectedActivity.value = activityFromRepo
            } else {
                // Ghi log nếu không tìm thấy để dễ debug
                Log.e("ActivityViewModel", "Không tìm thấy hoạt động với ID: $activityId từ Repository")
            }
        }
    }
    // Hàm kiểm tra (Gọi khi mới vào màn hình)
    fun checkRegistrationStatus(activityId: String, userId: String) {
        viewModelScope.launch {
            val result = repository.checkIsRegistered(activityId, userId)
            _isRegistered.value = result
        }
    }

    // Hàm thực hiện đăng ký

    fun registerToActivity(activityId: String, userId: String, userName: String, userAvatar: String) {
        viewModelScope.launch {
            _registerState.value = AuthResult.Loading

            // --- LOG BẮT ĐẦU ---
            Log.d("CHECK_ACTIVITY", " =========================================")
            Log.d("CHECK_ACTIVITY", " BẮT ĐẦU ĐĂNG KÝ HOẠT ĐỘNG")
            Log.d("CHECK_ACTIVITY", " Activity ID: $activityId")
            Log.d("CHECK_ACTIVITY", " User: $userName ($userId)")

            // Gọi Repo đi đăng ký
            Log.d("CHECK_ACTIVITY", "⏳ Đang gửi yêu cầu lên Firestore...")
            val result = repository.registerUserToActivity(activityId, userId, userName, userAvatar)

            _registerState.value = result

            if (result is AuthResult.Success) {
                _isRegistered.value = true
                // --- LOG THÀNH CÔNG ---
                Log.d("CHECK_ACTIVITY", "✅ ĐĂNG KÝ THÀNH CÔNG! Đã lưu vào danh sách.")
                Log.d("CHECK_ACTIVITY", "🎉 Chúc mừng $userName đã tham gia!")
                Log.d("CHECK_ACTIVITY", "📂 Đã lưu vào: activities/$activityId/registrations/$userId")
            } else if (result is AuthResult.Error) {
                // --- LOG THẤT BẠI ---
                Log.e("CHECK_ACTIVITY", " Lỗi: ${result.message}")
            }
            Log.d("CHECK_ACTIVITY", "=========================================")
        }
    }
    fun resetRegisterState() {
        _registerState.value = null
    }
    fun clearSelectedActivity() { // <-- LỖI CỦA BẠN SẼ HẾT Ở ĐÂY
        _selectedActivity.value = null
    }
    fun resetCreateResult() {
        _createResult.value = null
    }
}
