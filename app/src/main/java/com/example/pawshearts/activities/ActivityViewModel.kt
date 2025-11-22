package com.example.pawshearts.activities

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pawshearts.auth.AuthResult
import com.example.pawshearts.data.model.Activity // M nhớ import Activity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
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

    // T VỚI M PHẢI TỰ GỌI CÁI NÀY LÚC VÀO APP KKK
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
    fun clearSelectedActivity() { // <-- LỖI CỦA BẠN SẼ HẾT Ở ĐÂY
        _selectedActivity.value = null
    }
    fun resetCreateResult() {
        _createResult.value = null
    }
}
