package com.blossy.flowerstore.data.repository

import com.blossy.flowerstore.data.mapper.toCategory
import com.blossy.flowerstore.data.remote.api.CategoryApi
import com.blossy.flowerstore.domain.repository.CategoryRepository
import javax.inject.Inject
import com.blossy.flowerstore.domain.utils.Result
import com.blossy.flowerstore.data.remote.utils.safeApiCall
import com.blossy.flowerstore.data.remote.utils.toResult
import com.blossy.flowerstore.domain.model.CategoryModel
import kotlinx.coroutines.withTimeout

class CategoryRepositoryImpl @Inject constructor(
    private val categoryApi: CategoryApi
): CategoryRepository {
    override suspend fun getCategories(): Result<List<CategoryModel>> {
        return safeApiCall {
            categoryApi.getCategories().toResult { response ->
                response.map { it.toCategory() }
            }
        }
    }

}