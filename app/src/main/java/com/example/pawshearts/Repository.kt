package com.example.pawshearts
//
//object FakeRepository {
//
//    val posts: List<PetPost> = listOf(
//        PetPost(
//            postId = "cat1",
//            ownerId = "u1",
//            title = "Miu tam thể cần nhà thương 🐾",
//            description = "Bé hiền, đã tiêm phòng đầy đủ, rất quấn người.",
//            type = "cat",
//            gender = "female",
//            ageMonth = 8,
//            weightKg = 3.2,
//            location = "Q.7, TP.HCM",
//            status = "open",
//            photos = listOf("https://picsum.photos/seed/cat_miu/800/600")
//        ),
//        PetPost(
//            postId = "dog1",
//            ownerId = "u2",
//            title = "Cún Vàng ngoan, biết ngồi và bắt tay",
//            description = "Phù hợp gia đình có trẻ em, đã tẩy giun.",
//            type = "dog",
//            gender = "male",
//            ageMonth = 10,
//            weightKg = 9.5,
//            location = "Q.3, TP.HCM",
//            status = "open",
//            photos = listOf("https://picsum.photos/seed/dog_vang/800/600")
//        ),
//        PetPost(
//            postId = "cat2",
//            ownerId = "u3",
//            title = "Bé Heo hơi nhát nhưng cực ngoan",
//            description = "Đã triệt sản, cần chủ kiên nhẫn.",
//            type = "cat",
//            gender = "male",
//            ageMonth = 14,
//            weightKg = 4.1,
//            location = "Thủ Đức, TP.HCM",
//            status = "pending",
//            photos = listOf("https://picsum.photos/seed/cat_heo/800/600")
//        ),
//        PetPost(
//            postId = "dog2",
//            ownerId = "u4",
//            title = "Bông nhỏ bị lạc, cần tìm chủ",
//            description = "Tìm thấy gần công viên, rất thân thiện.",
//            type = "dog",
//            gender = "female",
//            ageMonth = 6,
//            weightKg = 6.0,
//            location = "Bình Thạnh, TP.HCM",
//            status = "lost",
//            photos = listOf("https://picsum.photos/seed/dog_bong/800/600")
//        ),
//        PetPost(
//            postId = "cat3",
//            ownerId = "u5",
//            title = "Mun đen tuyền đẹp trai 😼",
//            description = "Ăn khoẻ, ngủ nhiều, rất dễ thương.",
//            type = "cat",
//            gender = "male",
//            ageMonth = 5,
//            weightKg = 2.8,
//            location = "Q.10, TP.HCM",
//            status = "open",
//            photos = listOf("https://picsum.photos/seed/cat_mun/800/600")
//        ),
//        PetPost(
//            postId = "dog3",
//            ownerId = "u6",
//            title = "Bully lai cần nhận nuôi có trách nhiệm",
//            description = "Khoẻ mạnh, cần người có kinh nghiệm nuôi chó to.",
//            type = "dog",
//            gender = "male",
//            ageMonth = 12,
//            weightKg = 15.0,
//            location = "Tân Bình, TP.HCM",
//            status = "open",
//            photos = listOf("https://picsum.photos/seed/dog_bully/800/600")
//        )
//    )
//
//    fun getFeed(): List<PetPost> = posts.sortedByDescending { it.createdAt }
//    fun getAdopt(): List<PetPost> = posts.filter { it.status != "adopted" }
//    fun byId(id: String): PetPost? = posts.find { it.postId == id }
//    fun myPosts(userId: String = "u1"): List<PetPost> = posts.filter { it.ownerId == userId }
//}
