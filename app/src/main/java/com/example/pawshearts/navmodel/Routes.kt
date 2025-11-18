package com.example.pawshearts.navmodel

object Routes {
    const val SPLASH_SCREEN = "splash_screen"
    const val LOGIN_SCREEN = "login_screen"
    const val REGISTER_SCREEN = "register_screen"

    const val HOME = "home"
    const val DONATE = "donate"
    const val ADOPT = "adopt"

    // SỬA LẠI PROFILE ROUTE
    const val PROFILE = "profile" // Route gốc cho profile của mình
    fun userProfile(userId: String) = "profile/$userId" // Route cho profile người khác

    const val MESSAGES = "messages"
    const val PET_DETAIL = "pet_detail/{id}"
    fun petDetail(id: String) = "pet_detail/$id"



    const val CREATE_POST_SCREEN = "create_post"
    const val MY_ADOPT_POSTS_SCREEN = "my_adopt_posts_screen"
    const val MY_POSTS_SCREEN = "my_posts_screen"

    const val ADOPTS_COLLECTION = "adopt_posts_collection"
    const val PET_DETAIL_SCREEN = "pet_detail_screen"

    const val CREATE_ADOPT_POST_SCREEN = "create_adopt_post_screen"
    const val PET_DETAIL_ROUTE_WITH_ARG = "pet_detail_screen/{id}"
    const val DONATE_BANK_SCREEN = "donate_bank_screen"
    const val ACTIVITIES_LIST_SCREEN = "activities_list_screen"
    const val CREATE_ACTIVITY_SCREEN = "create_activity_screen"
    const val SETTINGS_SCREEN = "settings_screen"
    const val NOTIFICATION_SCREEN = "notification_screen"
    const val CHAT = "chat_screen/{threadId}"
    fun chat(threadId: String) = "chat/$threadId"

}
