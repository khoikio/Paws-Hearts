// com/example/pawshearts/messages/model/ChatThreadUtils.kt
package com.example.pawshearts.messages.model

const val GLOBAL_THREAD_ID = "global"

/**
 * Tạo id cho thread 1-1, không phụ thuộc thứ tự:
 * userA_userB hoặc userB_userA -> luôn ra cùng 1 chuỗi.
 */
fun generateThreadId(userA: String, userB: String): String {
    return listOf(userA, userB).sorted().joinToString("_")
}
fun createThreadId(userA: String, userB: String): String {
    return listOf(userA, userB).sorted().joinToString("_")
}
//A = 9sRQ8Of...
//B = Vk6pqfd...
//
//=> threadId = 9sRQ8Of..._Vk6pqfd...
