package com.example.pawshearts.activities

import com.example.pawshearts.auth.AuthResult
import com.example.pawshearts.data.model.Activity
import kotlinx.coroutines.flow.Flow

interface ActivityRepository {
    // Lấy list hoạt động cho User xem
    fun getAllActivitiesFlow(): Flow<List<Activity>>

    // Hàm tạo hoạt động (Chỉ Admin xài)
    suspend fun createActivity(activity: Activity): AuthResult<Unit>
    suspend fun updateActivity(activity: Activity): AuthResult<Unit>
    suspend fun deleteActivity(activityId: String)
    suspend fun getActivityById(activityId: String): Activity?
    suspend fun registerUserToActivity(activityId: String, userId: String, userName: String, userAvatar: String): AuthResult<Unit>
    suspend fun checkIsRegistered(activityId: String, userId: String): Boolean
}