package com.blossy.flowerstore.domain.model

data class Review(
    val userId: String,
    val rating: Int,
    val comment: String
)