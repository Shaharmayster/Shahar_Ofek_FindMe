package com.example.findme_shahar_ofek

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

/** Room access for cached REST API posts. */
@Dao
interface ApiPostDao {
    @Query("SELECT * FROM api_posts ORDER BY id ASC LIMIT 20")
    fun observeAll(): Flow<List<ApiPostEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(posts: List<ApiPostEntity>)

    @Query("DELETE FROM api_posts")
    suspend fun deleteAll()
}
