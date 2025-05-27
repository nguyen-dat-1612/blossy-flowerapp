package com.blossy.flowerstore.data.remote.dto

import com.blossy.flowerstore.domain.model.OrderModel
import com.google.gson.annotations.SerializedName

data class OrderListDTO (
    @SerializedName("count")
    val count: Int,
    @SerializedName("total")
    val total: Int,
    @SerializedName("currentPage")
    val currentPage: Int,
    @SerializedName("totalPages")
    val totalPages : Int,
    @SerializedName("orders")
    val orders: List<OrderDTO>
)