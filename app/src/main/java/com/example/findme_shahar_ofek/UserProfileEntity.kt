package com.example.findme_shahar_ofek

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.firebase.firestore.Exclude

/** Local cache model for user profile data. */
@Entity(tableName = "user_profiles")
data class UserProfileEntity(
    @PrimaryKey val userId: String = "",
    val email: String = "",
    val displayName: String = "",
    val imageUrl: String? = null,
    @get:Exclude val localImagePath: String? = null,
    val updatedAt: Long = System.currentTimeMillis()
)
