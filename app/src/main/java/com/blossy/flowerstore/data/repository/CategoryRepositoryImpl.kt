package com.blossy.flowerstore.data.repository

import android.util.Log
import com.blossy.flowerstore.data.remote.api.CategoryApi
import com.blossy.flowerstore.domain.model.Category
import com.blossy.flowerstore.domain.repository.CategoryRepository
import javax.inject.Inject
import com.blossy.flowerstore.domain.utils.Result
class CategoryRepositoryImpl @Inject constructor(
    private val categoryApi: CategoryApi
): CategoryRepository {
    override suspend fun getCategories(): Result<List<Category>> {
        return try {
            val response = categoryApi.getCategories()
            if (response.isSuccessful) {
                val body = response.body()
                if (
                    body != null &&
                    body.code == 200 &&
                    body.data != null
                ) {
                    Log.d("CategoryRepositoryImpl", "getCategories: ${body.data}")
                } else {
                    Log.d("CategoryRepositoryImpl", "getCategories: ${body?.message}")
                }
                if (body != null && body.code == 200 && body.data != null) {
                    Result.Success(body.data ?: emptyList())
                } else {
                    Result.Error(body?.message ?:  "Failed to fetch categories")
                }
            } else {
                Result.Error(response.message())
            }
        } catch (e: Exception) {
            Result.Error(e.message ?: "An error occurred")
        }
    }

}