package com.example.pawshearts

object Routes {
    const val HOME = "home"
    const val DONATE = "donate"
    const val ADOPT = "adopt"
    const val PROFILE = "profile"

    // Detail: truyền postId
    const val PET_DETAIL = "pet/{id}"
    fun petDetail(id: String) = "pet/$id"
}
