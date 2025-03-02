package com.piwew.jaki.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class News(
    val title: String = "",
    val content: String = "",
    val publishedAt: String = "",
    val likes: Int = 0,
    val thumbnailUrl: String = ""
) : Parcelable