package com.example.findme_shahar_ofek

import android.content.Context

/** Lightweight service locator for repositories used by ViewModels. */
object RepositoryProvider {
    @Volatile
    private var authRepository: AuthRepository? = null

    @Volatile
    private var postRepository: PostRepository? = null

    @Volatile
    private var profileRepository: ProfileRepository? = null

    @Volatile
    private var apiRepository: ApiRepository? = null

    fun authRepository(): AuthRepository {
        return authRepository ?: synchronized(this) {
            val instance = AuthRepository()
            authRepository = instance
            instance
        }
    }

    fun postRepository(context: Context): PostRepository {
        return postRepository ?: synchronized(this) {
            val db = AppDatabase.getDatabase(context.applicationContext)
            val instance = PostRepository(context.applicationContext, db.postDao(), authRepository())
            postRepository = instance
            instance
        }
    }

    fun profileRepository(context: Context): ProfileRepository {
        return profileRepository ?: synchronized(this) {
            val db = AppDatabase.getDatabase(context.applicationContext)
            val instance = ProfileRepository(
                context.applicationContext,
                db.userProfileDao(),
                authRepository()
            )
            profileRepository = instance
            instance
        }
    }

    fun apiRepository(context: Context): ApiRepository {
        return apiRepository ?: synchronized(this) {
            val db = AppDatabase.getDatabase(context.applicationContext)
            val instance = ApiRepository(context.applicationContext, db.apiPostDao())
            apiRepository = instance
            instance
        }
    }
}
