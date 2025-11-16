package com.example.pawshearts.post

import android.net.Uri
import android.util.Log
import com.example.pawshearts.auth.AuthResult
import com.example.pawshearts.notification.Notification
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import java.lang.Exception
import java.util.Date

/**
 * Thằng này chịu trách nhiệm nói chuyện với Firestore collection "posts".
 */
class PostRepositoryImpl(
    private val firestore: FirebaseFirestore
) : PostRepository {
    object NotificationTypes {
        const val LIKE = "LIKE"
        const val COMMENT = "COMMENT"
        const val SYSTEM = "SYSTEM"
        const val NEW_POST = "NEW_POST"
    }

    override suspend fun createPost(post: Post): AuthResult<Unit> {
        return try {
            val auth = FirebaseAuth.getInstance()
            val currentUser = auth.currentUser ?: return AuthResult.Error("Bạn chưa đăng nhập")
            val authorId = currentUser.uid
            val userName = currentUser.displayName ?: "Ai đó"

            val newPostRef = firestore.collection("posts").document()
            val finalPost = post.copy(id = newPostRef.id, userId = authorId, userName =userName, userAvatarUrl = currentUser.photoUrl?.toString())

            val batch = firestore.batch()
            batch.set(newPostRef, finalPost)

            val authorDoc = firestore.collection("users").document(authorId).get().await()
            val followers = authorDoc.get("followers") as? List<String> ?: emptyList()

            followers.forEach { followerId ->
                if (followerId != authorId) {
                    val notificationRef = firestore.collection("notifications").document()
                    val newNotification = Notification(
                        id = notificationRef.id,
                        userId = followerId,
                        actorId = authorId,
                        actorName = userName,
                        actorAvatarUrl = currentUser.photoUrl?.toString(),
                        type = NotificationTypes.NEW_POST, // DÙNG TÊN MỚI
                        message = "đã đăng một bài viết mới.",
                        postId = newPostRef.id,
                        isRead = false,
                        createdAt = Date()
                    )
                    batch.set(notificationRef, newNotification)
                }
            }

            batch.commit().await()
            AuthResult.Success(Unit)
        } catch (e: Exception) {
            AuthResult.Error(e.message ?: "Lỗi cmnr")
        }
    }

    // them ham getPostsByUserId (bai viet cua User)
    override fun getPostsByUserId(userId: String): Flow<List<Post>> {
        return callbackFlow {
            // Mở 1 kênh lắng nghe
            val listener = firestore.collection("posts")
                .whereEqualTo("userId", userId) // <-- Chỉ lấy bài của M
                .orderBy("createdAt", Query.Direction.DESCENDING) // <-- THÊM LẠI DÒNG NÀY
                .addSnapshotListener { snapshot, error ->

                    if (error != null) {
                        Log.e("PostRepoImpl", "Lỗi nghe post", error)
                        close(error) // Báo lỗi & đóng Flow
                        return@addSnapshotListener
                    }

                    if (snapshot != null) {
                        // Chuyển data Firebase sang List<Post>
                        val postList = snapshot.toObjects(Post::class.java)
                        trySend(postList) // Gửi cái list mới về cho ViewModel
                        Log.d("PostRepoImpl", "Tìm thấy ${postList.size} bài đăng của user $userId")
                    }
                }

            // Khi ViewModel bị hủy, tự gỡ listener (tiết kiệm pin)
            awaitClose {
                listener.remove()
            }
        }
    }
    // lay tat ca bai viet
    override fun fetchAllPostsFlow(): Flow<List<Post>> {
        return callbackFlow {
            val listener = firestore.collection("posts")
                .orderBy("createdAt", Query.Direction.DESCENDING) // <-- THÊM LẠI DÒNG NÀY
                .addSnapshotListener { snapshot, error ->

                    if (error != null) {
                        Log.e("PostRepoImpl", "Lỗi nghe TẤT CẢ post", error)
                        close(error)
                        return@addSnapshotListener
                    }

                    if (snapshot != null) {
                        val postList = snapshot.toObjects(Post::class.java)
                        trySend(postList) // Gửi list (TẤT CẢ) về ViewModel
                        Log.d("PostRepoImpl", "Tìm thấy ${postList.size} BÀI TẤT CẢ")
                    }
                }

            awaitClose {
                listener.remove()
            }
        }
    }

    override suspend fun toggleLike(postId: String, userId: String) {
        val postRef = firestore.collection("posts").document(postId)

        val auth = FirebaseAuth.getInstance()
        val currentUser = auth.currentUser ?: return

        val currentUserId = currentUser.uid
        val userDisplayName = currentUser.displayName ?: "Một người dùng"
        val userAvatar = currentUser.photoUrl?.toString()

        try {
            firestore.runTransaction { transaction ->
                val snapshot = transaction.get(postRef)

                // danh sách user đã like hiện tại
                val currentLikes = snapshot.get("likes") as? List<String> ?: emptyList()

                // id của chủ bài viết
                val postAuthorId = snapshot.getString("userId")

                if (currentLikes.contains(userId)) {
                    // ĐÃ LIKE -> BỎ LIKE (không tạo thông báo)
                    transaction.update(postRef, "likes", FieldValue.arrayRemove(userId))
                } else {
                    // CHƯA LIKE -> THÊM LIKE
                    transaction.update(postRef, "likes", FieldValue.arrayUnion(userId))

                    // Tạo thông báo cho CHỦ BÀI VIẾT (không tự thông báo cho chính mình)
                    if (postAuthorId != null && postAuthorId != userId) {
                        val notificationRef =
                            firestore.collection("notifications").document() // tự tạo id

                        val newNotification = Notification( // Bây giờ nó sẽ dùng đúng data class
                            id = notificationRef.id,
                            userId = postAuthorId,
                            actorId = currentUserId,
                            actorName = userDisplayName,
                            actorAvatarUrl = userAvatar,
                            type = NotificationTypes.LIKE,
                            message = "đã thích bài viết của bạn.",
                            postId = postId,
                            isRead = false,
                            createdAt = java.util.Date()
                        )

                        transaction.set(notificationRef, newNotification)
                    }
                }

                null
            }.await()
        } catch (e: Exception) {
            Log.e("PostRepoImpl", "Lỗi toggleLike: ${e.message}", e)
        }
    }
    // HÀM LẤY CMT VỀ
    override fun getCommentsFlow(postId: String): Flow<List<Comment>> {
        return callbackFlow {
            val listener = firestore.collection("posts").document(postId)
                .collection("comments") // <-- LẤY TRONG NÀY
                .orderBy("createdAt", Query.Direction.ASCENDING)
                .addSnapshotListener { snapshot, error ->

                    if (error != null) {
                        Log.e("PostRepoImpl", "Lỗi nghe Comment", error)
                        close(error)
                        return@addSnapshotListener
                    }
                    if (snapshot != null) {
                        val commentList = snapshot.toObjects(Comment::class.java)
                        trySend(commentList) // Gửi list cmt về ViewModel
                        Log.d("PostRepoImpl", "Tìm thấy ${commentList.size} cmt của post $postId")
                    }
                }

            awaitClose { listener.remove() }
        }
    }
    private val storage = FirebaseStorage.getInstance()
    // HÀM ĐĂNG CMT LÊN
    override suspend fun addComment(comment: Comment): AuthResult<Unit> {
        return try {
            val auth = com.google.firebase.auth.FirebaseAuth.getInstance()
            val currentUser = auth.currentUser ?: return  AuthResult.Error("Bạn chưa đăng nhập")
            val userDisplayName =currentUser.displayName ?: "Một người dùng"
            val userAvatar = currentUser.photoUrl?.toString()
            val batch = firestore.batch()
            val postRef = firestore.collection("posts").document(comment.postId)

            val commentRef = postRef.collection("comments").document()
            val postSnapshot = postRef.get().await()
            val postAuthorId = postSnapshot.getString("userId")
            //1 Thêm cmt mới
            batch.set(commentRef, comment.copy(id = commentRef.id))

            // 2 Cập nhật lại 'commentCount'
            batch.update(postRef, "commentCount", FieldValue.increment(1))

            // 3 tao thong bao
            if (postAuthorId != null && postAuthorId != currentUser.uid) {
                val notificationRef = firestore.collection("notifications").document()
                val currentUserId = currentUser.uid

                val newNotification = Notification( // Bây giờ nó sẽ dùng đúng data class
                    id = notificationRef.id,
                    userId = postAuthorId,
                    actorId = currentUserId,
                    actorName = userDisplayName,
                    actorAvatarUrl = userAvatar,
                    type = NotificationTypes.COMMENT, // DÙNG TÊN MỚI
                    message = "đã bình luận về bài viết của bạn.",
                    postId = comment.postId,
                    isRead = false,
                    createdAt = java.util.Date()
                )
                batch.set(notificationRef, newNotification)
            }

            batch.commit().await()

            AuthResult.Success(Unit)
        } catch (e: Exception) {
            Log.e("PostRepoImpl", "Lỗi addComment", e)
            AuthResult.Error(e.message ?: "Lỗi cmnr")
        }
    }
    override suspend fun uploadImage(uri: Uri): AuthResult<String> {
        return try {
            val fileName = "posts/${uri.lastPathSegment}_${System.currentTimeMillis()}"
            // 2. Lấy vị trí up
            val imageRef = storage.reference.child(fileName)

            // 3. ĐẨY FILE LÊN (putFile)
            imageRef.putFile(uri).await()

            // 4. LẤY LẠI CÁI LINK WEB (http://...)
            val downloadUrl = imageRef.downloadUrl.await()

            Log.d("PostRepoImpl", "Up ảnh thành công: $downloadUrl")
            AuthResult.Success(downloadUrl.toString()) // <-- Trả link xịn

        } catch (e: Exception) {
            Log.e("PostRepoImpl", "Lỗi up ảnh", e)
            AuthResult.Error(e.message ?: "Lỗi cmnr")
        }
    }
    override fun getPostById(postId: String): Flow<Post?> {
        // T trả về 1 'callbackFlow' cho nó real-time
        return callbackFlow {
            // Mở 1 kênh lắng nghe
            val listener = firestore.collection("posts").document(postId)
                .addSnapshotListener { snapshot, error ->

                    if (error != null) {
                        Log.e("PostRepoImpl", "Lỗi nghe 1 Post", error)
                        close(error)
                        return@addSnapshotListener
                    }
                    if (snapshot != null && snapshot.exists()) {
                        val post = snapshot.toObject(Post::class.java)
                        trySend(post)
                    } else {
                        trySend(null)
                    }
                }

            // Khi ViewModel bị hủy, tự gỡ listener
            awaitClose { listener.remove() }
        }
    }
}