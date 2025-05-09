package com.blossy.flowerstore.data.remote.utils

import com.blossy.flowerstore.data.remote.dto.NextSteps
import com.blossy.flowerstore.domain.model.Order

data class OrderResponseWrapper(
    val success: Boolean,
    val code: Int,
    val message: String,
    val data: Order?,
    val nextSteps: NextSteps?
)
