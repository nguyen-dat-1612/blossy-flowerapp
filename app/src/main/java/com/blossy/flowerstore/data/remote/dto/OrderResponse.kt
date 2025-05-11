package com.blossy.flowerstore.data.remote.dto

import com.blossy.flowerstore.domain.model.Order
import com.blossy.flowerstore.domain.model.OrderItem
import com.blossy.flowerstore.domain.model.Pagination
import com.blossy.flowerstore.domain.model.Product
import com.blossy.flowerstore.domain.model.ShippingAddress
import com.google.gson.annotations.SerializedName

data class OrderResponse (
    val count: Int,
    val total: Int,
    val currentPage: Int,
    val totalPages : Int,
    val orders: List<Order>
)