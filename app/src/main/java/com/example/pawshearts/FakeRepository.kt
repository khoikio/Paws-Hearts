package com.example.pawshearts

import android.util.Log
import com.example.pawshearts.PetPost

/**
 * FakeRepository - cung cấp dữ liệu giả (mock data) cho các màn hình.
 * Mục đích: test UI khi chưa kết nối Firebase / API thật.
 */

object FakeRepository {

    val petPosts = listOf(
        PetPost(
            postId = "p1",
            title = "Milo",
            type = "dog",
            gender = "male",
            ageMonth = 12,
            weightKg = 8.5,
            location = "TP.HCM",
            description = "Chú cún ngoan ngoãn, thân thiện, thích chơi với trẻ con.",
            photos = listOf("https://images.unsplash.com/photo-1518717758536-85ae29035b6d"),
            status = "open"
        ),
        PetPost(
            postId = "p2",
            title = "Luna",
            type = "cat",
            gender = "female",
            ageMonth = 10,
            weightKg = 3.2,
            location = "Hà Nội",
            description = "Cô mèo xinh xắn, rất tình cảm, đã được tiêm phòng đầy đủ.",
            photos = listOf("https://images.unsplash.com/photo-1592194996308-7b43878e84a6"),
            status = "open"
        ),
        PetPost(
            postId = "p3",
            title = "Max",
            type = "dog",
            gender = "male",
            ageMonth = 8,
            weightKg = 6.0,
            location = "Đà Nẵng",
            description = "Cún bị lạc, hiện đang được tạm giữ tại trung tâm cứu hộ.",
            photos = listOf("https://images.unsplash.com/photo-1558944351-c9c5ae33b27a"),
            status = "lost"
        ),
        PetPost(
            postId = "p4",
            title = "Coco",
            type = "cat",
            gender = "female",
            ageMonth = 6,
            weightKg = 2.8,
            location = "Cần Thơ",
            description = "Mèo nhỏ đáng yêu, lông mượt, thích nằm sưởi nắng.",
            photos = listOf("https://images.unsplash.com/photo-1574158622682-e40e69881006"),
            status = "open"
        ),
        PetPost(
            postId = "p5",
            title = "Buddy",
            type = "dog",
            gender = "male",
            ageMonth = 15,
            weightKg = 9.2,
            location = "Huế",
            description = "Rất trung thành và ngoan, hợp với gia đình có sân rộng.",
            photos = listOf("https://images.unsplash.com/photo-1537151625747-768eb6cf92b6"),
            status = "adopted"
        ),
        PetPost(
            postId = "p6",
            title = "Mimi",
            type = "cat",
            gender = "female",
            ageMonth = 9,
            weightKg = 3.1,
            location = "Hà Nội",
            description = "Hiền lành, hay cọ sát vào người, cần tìm chủ yêu thương.",
            photos = listOf("https://images.unsplash.com/photo-1543852786-1cf6624b9987"),
            status = "open"
        )
    )

    fun getFeed(): List<PetPost> = petPosts

    fun getPostById(id: String): PetPost? = petPosts.find { it.postId == id }
}
