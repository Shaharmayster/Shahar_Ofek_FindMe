package com.example.findme_shahar_ofek

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.firebase.firestore.Exclude
import kotlinx.parcelize.Parcelize

/** Room entity for user posts, also used by Firestore serialization. */
@Parcelize
@Entity(tableName = "posts")
data class PostEntity(
    @PrimaryKey val id: String = "",
    val userId: String = "",
    val title: String = "",
    val imageUrl: String? = null,
    @get:Exclude val localImagePath: String? = null,
    val createdAt: Long = System.currentTimeMillis()
) : Parcelable
