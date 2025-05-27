package com.blossy.flowerstore.presentation.home.state

import com.blossy.flowerstore.domain.model.ProductModel
import com.blossy.flowerstore.domain.model.UserModel
import com.blossy.flowerstore.domain.model.CategoryModel

data class HomeUiState (
    val isLoading: Boolean = true,
    val categories: List<CategoryModel> = emptyList(),
    val products: List<ProductModel> = emptyList(),
    val user: UserModel? = null,
    val locationText: String = "",
    val error: String = ""
)