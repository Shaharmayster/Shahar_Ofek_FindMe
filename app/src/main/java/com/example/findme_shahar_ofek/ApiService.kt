package com.example.findme_shahar_ofek

import retrofit2.http.GET
import retrofit2.http.Query

/** Retrofit contract for sample REST endpoint. */
interface ApiService {
    @GET("api/v2/facts")
    suspend fun getAnimalFacts(@Query("limit") limit: Int = 20): AnimalFactsResponse

    companion object {
        const val BASE_URL = "https://dogapi.dog/"
    }
}
