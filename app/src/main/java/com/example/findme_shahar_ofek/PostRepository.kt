package com.example.findme_shahar_ofek

import android.content.Context
import android.net.Uri
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.io.File
import java.util.UUID

/** Owns post data flow, remote sync, and create/edit/delete actions. */
class PostRepository(
    private val appContext: Context,
    private val postDao: PostDao,
    private val authRepository: AuthRepository,
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance(),
    private val storage: FirebaseStorage = FirebaseStorage.getInstance(),
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) {
    private val syncPrefs = appContext.getSharedPreferences(SYNC_PREFS, Context.MODE_PRIVATE)

    fun observeFeedPosts(): Flow<List<PostEntity>> = postDao.observeAll()

    fun observePostsByUser(userId: String): Flow<List<PostEntity>> = postDao.observeByUser(userId)

    fun lastSyncedAt(): Long = syncPrefs.getLong(KEY_LAST_SYNCED_AT, 0L)

    suspend fun syncPosts(limit: Long = 50) = withContext(ioDispatcher) {
        val snapshot = firestore.collection(POSTS_COLLECTION)
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .limit(limit)
            .get()
            .await()

        val posts = snapshot.toObjects(PostEntity::class.java).map { remote ->
            val cached = postDao.getById(remote.id)
            remote.copy(localImagePath = cached?.localImagePath)
        }
        postDao.replaceAll(posts)
        syncPrefs.edit().putLong(KEY_LAST_SYNCED_AT, System.currentTimeMillis()).apply()
    }

    suspend fun getPostById(postId: String): PostEntity? = withContext(ioDispatcher) {
        postDao.getById(postId) ?: fetchPostFromFirestore(postId)?.also {
            postDao.insertAll(listOf(it))
        }
    }

    suspend fun createOrUpdatePost(
        postId: String?,
        title: String,
        category: String,
        imageUri: Uri?
    ): PostEntity = withContext(ioDispatcher) {
        val userId = authRepository.currentUserId()
            ?: error(appContext.getString(R.string.error_user_not_logged_in))
        val existing = postId?.let { getPostById(it) }
        if (postId != null) {
            requireNotNull(existing) { "Post does not exist." }
            require(existing.userId == userId) { "Only the post owner can edit this post." }
        }
        val resolvedPostId = postId ?: UUID.randomUUID().toString()

        var imageUrl: String? = existing?.imageUrl
        var localImagePath: String? = existing?.localImagePath
        if (imageUri != null) {
            ImageCache.validateImageForUpload(appContext, imageUri)
            localImagePath = ImageCache.saveInternalCopy(appContext, imageUri, "posts")
            if (existing?.localImagePath != null && existing.localImagePath != localImagePath) {
                ImageCache.deleteIfInternal(existing.localImagePath)
            }
            imageUrl = uploadPostImage(userId, Uri.fromFile(File(localImagePath)))
        }

        val post = PostEntity(
            id = resolvedPostId,
            userId = userId,
            title = title.trim(),
            category = category.ifBlank { PostEntity.DEFAULT_CATEGORY },
            imageUrl = imageUrl,
            localImagePath = localImagePath,
            createdAt = existing?.createdAt ?: System.currentTimeMillis()
        )

        firestore.collection(POSTS_COLLECTION).document(resolvedPostId).set(post).await()
        postDao.insertAll(listOf(post))
        post
    }

    suspend fun deletePost(post: PostEntity) = withContext(ioDispatcher) {
        val userId = authRepository.currentUserId()
            ?: error(appContext.getString(R.string.error_user_not_logged_in))
        require(post.userId == userId) { "Only the post owner can delete this post." }

        firestore.collection(POSTS_COLLECTION).document(post.id).delete().await()
        postDao.deleteById(post.id)
        ImageCache.deleteIfInternal(post.localImagePath)
    }

    private suspend fun fetchPostFromFirestore(postId: String): PostEntity? {
        val snapshot = firestore.collection(POSTS_COLLECTION).document(postId).get().await()
        return snapshot.toObject(PostEntity::class.java)
    }

    private suspend fun uploadPostImage(userId: String, imageUri: Uri): String {
        val ref = storage.reference.child("posts/$userId/${UUID.randomUUID()}.jpg")
        ref.putFile(imageUri).await()
        return ref.downloadUrl.await().toString()
    }

    private companion object {
        const val POSTS_COLLECTION = "posts"
        const val SYNC_PREFS = "post_sync"
        const val KEY_LAST_SYNCED_AT = "last_synced_at"
    }
}
