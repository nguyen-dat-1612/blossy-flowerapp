package com.blossy.flowerstore.domain.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class AddressModel (
    val id: String?,
    val name: String,
    val phone: String,
    val address: String,
    val isDefault: Boolean
) : Parcelable
