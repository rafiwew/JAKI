package com.piwew.jaki.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class LaporanData(
    val imageUri: String? = null,
    val latitude: Double? = null,
    val longitude: Double? = null,
    val alamat: String? = null,
    val detailAlamat: String? = null,
    val detail: String? = null,
    val kategori: String? = null
) : Parcelable