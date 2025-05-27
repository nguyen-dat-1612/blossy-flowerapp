package com.blossy.flowerstore.domain.model

data class ReviewModel(
    val userId: String,
    val rating: Int,
    val comment: String
)