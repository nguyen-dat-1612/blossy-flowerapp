package com.blossy.flowerstore.domain.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Category (
    @SerializedName("_id")
    val id: String,
    val name: String,
    val description: String,
    val image: String
) : Parcelable