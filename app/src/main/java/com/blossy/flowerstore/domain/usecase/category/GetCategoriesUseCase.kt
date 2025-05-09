package com.blossy.flowerstore.domain.usecase.category

import com.blossy.flowerstore.domain.repository.CategoryRepository
import javax.inject.Inject

class GetCategoriesUseCase @Inject  constructor(
    private val categoriesRepository: CategoryRepository
){
    suspend operator fun invoke() = categoriesRepository.getCategories()
}