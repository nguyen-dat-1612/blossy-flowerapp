package com.blossy.flowerstore.data.mapper

import com.blossy.flowerstore.data.remote.dto.CategoryResponse
import com.blossy.flowerstore.domain.model.Category

fun CategoryResponse.toCategory(): Category  {
    return Category(
        id = id,
        name = name,
        description = description,
        image = image,
        productCount = productCount
    )
}