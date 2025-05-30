package com.piwew.jaki.data.database.dao

import androidx.room.*
import com.piwew.jaki.data.database.entities.NewsEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface NewsDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveNews(news: NewsEntity)

    @Delete
    suspend fun deleteNews(news: NewsEntity)

    @Query("SELECT * FROM saved_news WHERE isBookmarked = 1 ORDER BY publishedAt DESC")
    fun getAllSavedNews(): Flow<List<NewsEntity>>

    @Query("SELECT EXISTS(SELECT * FROM saved_news WHERE title = :title AND isBookmarked = 1)")
    suspend fun isNewsBookmarked(title: String): Boolean
}