package com.blossy.flowerstore.domain.model.response

import com.blossy.flowerstore.domain.model.PaginationModel
import com.blossy.flowerstore.domain.model.ProductModel

data class ProductListModel(
    val count: Int,
    val total: Int,
    val pagination: PaginationModel,
    val products: List<ProductModel>
)