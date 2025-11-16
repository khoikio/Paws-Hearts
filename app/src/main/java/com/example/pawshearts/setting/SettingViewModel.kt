package com.example.pawshearts.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SettingViewModel(
    private val repository: SettingsRepository): ViewModel(){
    // (•‿•) "Ông quản gia" sẽ nhìn vào cái "biển báo" isDarkMode này
    // Nó đọc dữ liệu từ "ống nước" isDarkModeFlow của "nhà kho".
    val isDarkMode: StateFlow<Boolean> = repository.isDarkModeFlow
        .stateIn(
        scope = viewModelScope, // phạm vi hoạt động như nhà kho
        started = SharingStarted.WhileSubscribed(5000L), //5000L = 5s sau thời gian user ko dùng thì tắt đê tiết kiệm dữ liệu
        initialValue = false // ban dau den sang
    )
    // 凸(¬‿¬)凸 Khi "ông chủ" (UI) ra lệnh, "ông quản gia" sẽ gọi hàm này
    fun toggleDarkMode(){
        viewModelScope.launch {
            // "Ông quản gia" đi tới "nhà kho" và ghi lại trạng thái mới (ngược với hiện tại)
            repository.setTheme(!isDarkMode.value)
        }
    }
}
// Factory để gọi cái bongs đèn tắt , ông quản gia, vì ổng cần "nhà kho" để làm việc =^.^=
class SettingViewModelFactory(private val repository: SettingsRepository): ViewModelProvider.Factory{
    override fun <T : ViewModel> create(modelClass: Class<T>): T{
        if(modelClass.isAssignableFrom(SettingViewModel::class.java)){
            @Suppress("UNCHECKED_CAST")
            return SettingViewModel(repository) as T

        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
