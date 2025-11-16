package com.example.pawshearts.navmodel

object Routes {
    const val HOME = "home"
    const val DONATE = "donate"
    const val ADOPT = "adopt"
    const val PROFILE = "profile"

    const val LOGIN_SCREEN = "login"
    const val REGISTER_SCREEN = "register"
    const val SPLASH_SCREEN = "splash_screen"

    const val NOTIFICATION_SCREEN ="notifications"

    const val PET_DETAIL_SCREEN = "pet_detail_screen"
    const val PET_DETAIL = "pet/{id}"

    fun petDetail(id: String) = "pet/$id"

    const val COMMENT_SCREEN = "comment_screen"
    fun comment(postId: String) = "$COMMENT_SCREEN/$postId"

    const val CREATE_POST_SCREEN = "create_post_screen"
    const val MY_POSTS_SCREEN = "my-posts"

    const val MY_ADOPT_POSTS_SCREEN = "my-adopt-posts"
    const val CREATE_ADOPT_POST_SCREEN = "create-adopt-post"

    const val ADOPT_COMMENT_SCREEN = "adopt_comment_screen"

    const val DONATE_BANK_SCREEN = "donate-bank-info"

    const val ACTIVITIES_LIST_SCREEN = "activities_list"
    const val CREATE_ACTIVITY_SCREEN = "create_activity"

    const val MESSAGES = "messages"
    const val CHAT = "chat/{threadId}"
    fun chat(threadId: String) = "chat/$threadId"
}