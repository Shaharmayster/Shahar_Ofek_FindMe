package com.example.findme_shahar_ofek

import androidx.room.Entity
import androidx.room.PrimaryKey

/** Room entity for cached animal facts shown in the API screen. */
@Entity(tableName = "api_posts")
data class ApiPostEntity(
    @PrimaryKey val id: Int,
    val userId: Int,
    val title: String,
    val body: String,
    val imageUrl: String = ""
)

/** DTO for TheCatAPI image search endpoint. */
data class CatImageDto(
    val id: String = "",
    val url: String = "",
    val width: Int = 0,
    val height: Int = 0
)
