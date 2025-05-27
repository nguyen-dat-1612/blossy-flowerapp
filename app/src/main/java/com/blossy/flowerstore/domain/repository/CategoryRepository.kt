package com.blossy.flowerstore.domain.repository

import com.blossy.flowerstore.domain.model.CategoryModel
import com.blossy.flowerstore.domain.utils.Result

interface CategoryRepository {
    suspend fun getCategories(): Result<List<CategoryModel>>
}