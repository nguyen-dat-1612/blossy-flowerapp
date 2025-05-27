package com.blossy.flowerstore.data.remote.dto

import com.google.gson.annotations.SerializedName

data class PaginationDTO (
    @SerializedName("page")
    val page: Int,
    @SerializedName("limit")
    val limit: Int,
    @SerializedName("pages")
    val pages: Int
)