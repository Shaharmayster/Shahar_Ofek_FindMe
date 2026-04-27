package com.example.findme_shahar_ofek

import android.content.Context
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/** Fetches remote API posts and caches them into Room. */
class ApiRepository(
    private val appContext: Context,
    private val apiPostDao: ApiPostDao,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) {
    private val apiService: ApiService = Retrofit.Builder()
        .baseUrl(ApiService.BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(ApiService::class.java)

    fun observePosts(): Flow<List<ApiPostEntity>> = apiPostDao.observeAll()

    suspend fun refreshPosts() = withContext(ioDispatcher) {
        val response = apiService.getAnimalFacts(limit = 20)
        val entities = response.data.take(20).mapIndexed { index, dto ->
            ApiPostEntity(
                id = index + 1,
                userId = 0,
                title = appContext.getString(R.string.animal_fact),
                body = dto.attributes.fact
            )
        }
        apiPostDao.deleteAll()
        apiPostDao.insertAll(entities)
    }
}
