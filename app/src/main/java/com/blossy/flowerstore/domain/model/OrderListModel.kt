package com.blossy.flowerstore.domain.model

data class OrderListModel(
    val count: Int,
    val total: Int,
    val currentPage: Int,
    val totalPages: Int,
    val orders: List<OrderModel>
)