package com.example.pawshearts

object FakeRepository {
    fun getFeed(): List<PetPost> {
        return listOf(
            PetPost(
                postId = "1",
                ownerId = "u001",
                title = "Milo cần nhận nuôi",
                description = "Chú chó dễ thương, hiền lành.",
                type = "dog",
                gender = "male",
                ageMonth = 18,
                weightKg = 6.5,
                location = "Hà Nội",
                status = "open",
                photos = listOf("https://images.unsplash.com/photo-1558788353-f76d92427f16"),
                createdAt = 1635782400000,
                updatedAt = 1635782400000
            ),
            PetPost(
                postId = "2",
                ownerId = "u002",
                title = "Miu mất ở Hồ Chí Minh",
                description = "Ai thấy Miu thì báo giúp!",
                type = "cat",
                gender = "female",
                ageMonth = 24,
                weightKg = 4.0,
                location = "Hồ Chí Minh",
                status = "lost",
                photos = listOf("https://images.unsplash.com/photo-1518717758536-85ae29035b6d"),
                createdAt = 1635965200000,
                updatedAt = 1635965200000
            ),
            PetPost(
                postId = "3",
                ownerId = "u003",
                title = "Chim cảnh cần tìm chủ mới",
                description = "Chim khỏe, đã tiêm phòng.",
                type = "other",
                gender = "unknown",
                ageMonth = 12,
                weightKg = null,
                location = "Đà Nẵng",
                status = "open",
                photos = listOf("https://images.unsplash.com/photo-1465101046530-73398c7f28ca"),
                createdAt = 1636051600000,
                updatedAt = 1636051600000
            )
            // Có thể thêm nhiều mẫu khác
        )
    }
}