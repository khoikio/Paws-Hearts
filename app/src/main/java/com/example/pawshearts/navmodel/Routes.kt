package com.example.pawshearts.navmodel

object Routes {
    const val HOME = "home"
    const val DONATE = "donate"
    const val ADOPT = "adopt"
    const val PROFILE = "profile"

    const val LOGIN_SCREEN = "login"
    const val REGISTER_SCREEN = "register"

    // Detail: truyền postId
    const val PET_DETAIL = "pet/{id}"
    fun petDetail(id: String) = "pet/$id"
    const val COMMENT_SCREEN = "comment_screen"
    fun comment(postId: String) = "$COMMENT_SCREEN/$postId"
    const val SPLASH_SCREEN = "splash_screen"
    const val CREATE_POST_SCREEN = "create_post_screen"
    const val MY_POSTS_SCREEN = "my-posts" // taps hiện các bài đăng
}