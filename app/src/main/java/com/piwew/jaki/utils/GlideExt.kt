package com.piwew.jaki.utils

import android.widget.ImageView
import com.bumptech.glide.Glide

fun ImageView.loadImage(url: String) {
    Glide.with(this.context)
        .load(url)
        .fitCenter()
        .skipMemoryCache(true)
        .into(this)
}