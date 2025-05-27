package com.blossy.flowerstore.data.remote.utils

import com.blossy.flowerstore.data.remote.dto.NextStepsDTO
import com.blossy.flowerstore.data.remote.dto.OrderDTO

data class OrderResponseWrapper(
    val success: Boolean,
    val code: Int,
    val message: String,
    val data: OrderDTO?,
    val nextSteps: NextStepsDTO?
)
