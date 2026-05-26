package com.example.findme_shahar_ofek

import retrofit2.http.GET
import retrofit2.http.Query

/** Retrofit contract for sample REST endpoint. */
interface ApiService {
    @GET("v1/images/search")
    suspend fun getAnimalFacts(
        @Query("limit") limit: Int = 12,
        @Query("mime_types") mimeTypes: String = "jpg,png"
    ): List<CatImageDto>

    companion object {
        const val BASE_URL = "https://api.thecatapi.com/"
    }
}
