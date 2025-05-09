package com.blossy.flowerstore.domain.model


data class Pagination(
    val page: Int,
    val limit: Int,
    val pages: Int
)