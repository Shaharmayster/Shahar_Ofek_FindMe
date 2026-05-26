package com.example.findme_shahar_ofek

import android.content.Context
import android.net.Uri
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

/** Owns profile cache, sync, and edit operations for the logged-in user. */
class ProfileRepository(
    private val appContext: Context,
    private val userProfileDao: UserProfileDao,
    private val authRepository: AuthRepository,
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance(),
    private val storage: FirebaseStorage = FirebaseStorage.getInstance(),
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) {
    fun observeProfile(userId: String): Flow<UserProfileEntity?> = userProfileDao.observeById(userId)

    suspend fun syncProfile(userId: String) = withContext(ioDispatcher) {
        val snapshot = firestore.collection(USERS_COLLECTION).document(userId).get().await()
        val remote = snapshot.toObject(UserProfileEntity::class.java)
        val cached = userProfileDao.getById(userId)
        if (remote != null) {
            userProfileDao.upsert(remote.copy(userId = userId, localImagePath = cached?.localImagePath))
        } else {
            val fallback = createFallbackProfile(userId)
            firestore.collection(USERS_COLLECTION).document(userId).set(fallback).await()
            userProfileDao.upsert(fallback)
        }
    }

    suspend fun saveProfile(
        userId: String,
        displayName: String,
        imageUri: Uri?
    ): UserProfileEntity = withContext(ioDispatcher) {
        val current = userProfileDao.getById(userId) ?: createFallbackProfile(userId)
        var imageUrl = current.imageUrl
        var localImagePath = current.localImagePath

        if (imageUri != null) {
            ImageCache.validateImageForUpload(appContext, imageUri)
            localImagePath = ImageCache.saveInternalCopy(appContext, imageUri, "profiles")
            if (current.localImagePath != null && current.localImagePath != localImagePath) {
                ImageCache.deleteIfInternal(current.localImagePath)
            }
            val ref = storage.reference.child("profiles/$userId.jpg")
            ref.putFile(imageUri).await()
            imageUrl = ref.downloadUrl.await().toString()
        }

        val profile = current.copy(
            userId = userId,
            email = authRepository.currentUserEmail() ?: current.email,
            displayName = displayName.trim(),
            imageUrl = imageUrl,
            localImagePath = localImagePath,
            updatedAt = System.currentTimeMillis()
        )

        firestore.collection(USERS_COLLECTION).document(userId).set(profile).await()
        userProfileDao.upsert(profile)
        profile
    }

    private fun createFallbackProfile(userId: String): UserProfileEntity {
        val email = authRepository.currentUserEmail().orEmpty()
        val defaultName = email.substringBefore('@').ifBlank {
            appContext.getString(R.string.default_profile_name)
        }
        return UserProfileEntity(
            userId = userId,
            email = email,
            displayName = defaultName,
            imageUrl = null,
            updatedAt = System.currentTimeMillis()
        )
    }

    private companion object {
        const val USERS_COLLECTION = "users"
    }
}
