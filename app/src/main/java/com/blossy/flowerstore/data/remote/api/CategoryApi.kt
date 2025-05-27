package com.blossy.flowerstore.data.remote.api

import com.blossy.flowerstore.data.remote.dto.CategoryDTO
import com.blossy.flowerstore.data.remote.utils.BaseResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Headers

interface CategoryApi {
    @Headers("No-Authentication: true")
    @GET("categories")
    suspend fun getCategories(): BaseResponse<List<CategoryDTO>>
}