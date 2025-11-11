package com.example.pawshearts.activities

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

    // T VỚI M PHẢI TỰ GỌI CÁI NÀY LÚC VÀO APP KKK
    init {
        fetchActivities()
    }

    private fun fetchActivities() {
        viewModelScope.launch {
            repository.getAllActivitiesFlow().collect { activities ->
                _activities.value = activities
            }
        }
    }

    // Hàm này cho Admin tạo
    fun createActivity(activity: Activity) {
        _createResult.value = AuthResult.Loading
        viewModelScope.launch {
            val result = repository.createActivity(activity)
            _createResult.value = result
        }
    }

    fun resetCreateResult() {
        _createResult.value = null
    }
}