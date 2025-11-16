package com.example.pawshearts.navmodel

object Routes {
    const val HOME = "home"
    const val DONATE = "donate"
    const val ADOPT = "adopt"
    const val PROFILE = "profile"

    const val LOGIN_SCREEN = "login"
    const val REGISTER_SCREEN = "register"

    // Detail: truyền postId
    const val PET_DETAIL_SCREEN_BASE = "pet_detail"
    const val PET_DETAIL = "$PET_DETAIL_SCREEN_BASE/{id}"
    const val COMMENT_SCREEN = "comment_screen"
    fun comment(postId: String) = "$COMMENT_SCREEN/$postId"
    const val SPLASH_SCREEN = "splash_screen"
    const val CREATE_POST_SCREEN = "create_post_screen"
    const val MY_POSTS_SCREEN = "my-posts" // taps hiện các bài đăng
    const val MY_ADOPT_POSTS_SCREEN = "my-adopt-posts"  // (TRANG LIST NHẬN NUÔI)
    const val CREATE_ADOPT_POST_SCREEN = "create-adopt-post" //(TRANG TẠO BÀI NHẬN NUÔI)
    const val DONATE_BANK_SCREEN = "donate-bank-info"
    const val ACTIVITIES_LIST_SCREEN = "activities_list"
    const val CREATE_ACTIVITY_SCREEN = "create_activity"
    const val SETTINGS_SCREEN = "settings_screen"
    const val NOTIFICATION_SCREEN = "notification_screen"
}