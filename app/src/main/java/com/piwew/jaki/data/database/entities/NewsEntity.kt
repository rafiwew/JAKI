package com.piwew.jaki.data.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "saved_news")
data class NewsEntity(
    @PrimaryKey val title: String,
    val content: String,
    val thumbnailUrl: String,
    val publishedAt: String,
    val likes: Int,
    val isBookmarked: Boolean = true
)