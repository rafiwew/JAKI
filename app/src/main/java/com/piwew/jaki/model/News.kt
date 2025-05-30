package com.piwew.jaki.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class News(
    val id: String = "",
    val title: String = "",
    val content: String = "",
    val publishedAt: String = "",
    val likes: Int = 0,
    val thumbnailUrl: String = "",
    val isBookmarked: Boolean = false
) : Parcelable