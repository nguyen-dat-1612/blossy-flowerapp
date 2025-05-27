package com.blossy.flowerstore.domain.model

data class PaginationModel(
    val page: Int,
    val limit: Int,
    val pages: Int
)