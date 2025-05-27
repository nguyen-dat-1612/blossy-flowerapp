package com.blossy.flowerstore.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class CategoryModel(
    val id: String,
    val name: String,
    val description: String,
    val image: String,
    val productCount: Int
) : Parcelable