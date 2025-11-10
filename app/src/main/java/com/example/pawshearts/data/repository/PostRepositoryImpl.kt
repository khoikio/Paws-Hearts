package com.example.pawshearts.data.repository

import android.net.Uri
import android.util.Log
import com.example.pawshearts.auth.AuthResult
import com.example.pawshearts.data.model.Comment
import com.example.pawshearts.data.model.Post
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import java.lang.Exception
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.tasks.await

/**
 * Thằng này chịu trách nhiệm nói chuyện với Firestore collection "posts".
 */
class PostRepositoryImpl(
    private val firestore: FirebaseFirestore
) : PostRepository {

    override suspend fun createPost(post: Post): AuthResult<Unit> {
        return try {
            // 1. Tạo 1 document rỗng để lấy ID
            val newPostRef = firestore.collection("posts").document()

            // 2. Gán cái ID đó vô bài post của M
            //    rồi set() data (T xài 'set' chứ ko xài 'add'
            //    để T kiểm soát được cái ID)
            newPostRef.set(post.copy(id = newPostRef.id)).await()

            Log.d("PostRepoImpl", "Đăng bài thành công: ${newPostRef.id}")
            AuthResult.Success(Unit) // Trả về Success (Unit = đéo cần data gì)

        } catch (e: Exception) {
            Log.e("PostRepoImpl", "Đăng bài thất bại", e)
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
    // ham tim like bai viet
    override suspend fun toggleLike(postId: String, userId: String) {
        val postRef = firestore.collection("posts").document(postId)

        // T dùng "Transaction" cho nó xịn KKK
        // Nó đảm bảo M ko bị lỗi 2 thằng cùng like 1 lúc
        try {
            firestore.runTransaction { transaction ->
                val snapshot = transaction.get(postRef)
                // Lấy danh sách 'likes' (List<String>) hiện tại
                val currentLikes = snapshot.get("likes") as? List<String> ?: emptyList()

                if (currentLikes.contains(userId)) {
                    // Nếu M đã like -> M bấm lại -> XÓA (Unlike)
                    transaction.update(postRef, "likes", FieldValue.arrayRemove(userId))
                } else {
                    // Nếu M chưa like -> M bấm -> THÊM VÔ (Like)
                    transaction.update(postRef, "likes", FieldValue.arrayUnion(userId))
                }
                null // Transaction bắt M trả về gì đó, M kệ mẹ nó
            }.await()
        } catch (e: Exception) {
            Log.e("PostRepoImpl", "Lỗi toggleLike: ${e.message}")
            // M báo lỗi gì ở đây cũng đc
        }
    }
    // HÀM LẤY CMT VỀ
    override fun getCommentsFlow(postId: String): Flow<List<Comment>> {
        return callbackFlow {
            // T sẽ lấy cmt từ "sub-collection" (collection con)
            val listener = firestore.collection("posts").document(postId)
                .collection("comments") // <-- LẤY TRONG NÀY
                .orderBy("createdAt", Query.Direction.ASCENDING) // <-- CMT CŨ NHẤT LÊN ĐẦU
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
            // T dùng "Batch Write" (Lô) để T làm 2 việc 1 lúc
            val batch = firestore.batch()

            // 1. Lấy vị trí bài post
            val postRef = firestore.collection("posts").document(comment.postId)

            // 2. Lấy vị trí cmt mới (trong sub-collection)
            val commentRef = postRef.collection("comments").document()

            // 3. (Việc 1) Thêm cmt mới (với ID xịn)
            batch.set(commentRef, comment.copy(id = commentRef.id))

            // 4. (Việc 2) Cập nhật lại 'commentCount' (cái M thêm ở Post.kt)
            batch.update(postRef, "commentCount", FieldValue.increment(1))

            // 5. Chạy 2 lệnh
            batch.commit().await()

            AuthResult.Success(Unit)
        } catch (e: Exception) {
            Log.e("PostRepoImpl", "Lỗi addComment", e)
            AuthResult.Error(e.message ?: "Lỗi cmnr")
        }
    }
    override suspend fun uploadImage(uri: Uri): AuthResult<String> {
        return try {
            // 1. Tạo 1 cái tên file độc nhất (T lấy 16 số cuối + time)
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
                        trySend(post) // Gửi 1 BÀI về ViewModel
                    } else {
                        trySend(null) // Bài đéo tồn tại
                    }
                }

            // Khi ViewModel bị hủy, tự gỡ listener
            awaitClose { listener.remove() }
        }
    }
}

