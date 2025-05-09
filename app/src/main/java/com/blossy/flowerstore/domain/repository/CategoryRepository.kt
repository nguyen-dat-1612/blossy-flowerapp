package com.blossy.flowerstore.domain.repository

import com.blossy.flowerstore.domain.model.Category
import com.blossy.flowerstore.domain.utils.Result

interface CategoryRepository {
    suspend fun getCategories(): Result<List<Category>>
}