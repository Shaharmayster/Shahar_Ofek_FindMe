package com.example.findme_shahar_ofek

import android.content.Context
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

/** Fetches remote API posts and caches them into Room. */
class ApiRepository(
    private val appContext: Context,
    private val apiPostDao: ApiPostDao,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) {
    private val httpClient = OkHttpClient.Builder()
        .connectTimeout(20, TimeUnit.SECONDS)
        .readTimeout(20, TimeUnit.SECONDS)
        .build()

    private val apiService: ApiService = Retrofit.Builder()
        .baseUrl(ApiService.BASE_URL)
        .client(httpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(ApiService::class.java)

    fun observePosts(): Flow<List<ApiPostEntity>> = apiPostDao.observeAll()

    suspend fun refreshPosts() = withContext(ioDispatcher) {
        val response = apiService.getAnimalFacts(count = 20)
        val entities = response.data.take(20).mapIndexed { index, fact ->
            ApiPostEntity(
                id = index + 1,
                userId = 0,
                title = appContext.getString(R.string.cat_fact),
                body = fact
            )
        }
        apiPostDao.deleteAll()
        apiPostDao.insertAll(entities)
    }
}
