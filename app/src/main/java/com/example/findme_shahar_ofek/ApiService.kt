package com.example.findme_shahar_ofek

import retrofit2.http.GET

/** Retrofit contract for sample REST endpoint. */
interface ApiService {
    @GET("api/breeds/list/all")
    suspend fun getAnimalFacts(): AnimalFactsResponse

    companion object {
        const val BASE_URL = "https://dog.ceo/"
    }
}
