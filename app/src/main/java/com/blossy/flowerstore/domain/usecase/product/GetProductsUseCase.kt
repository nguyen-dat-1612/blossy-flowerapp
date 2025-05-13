package com.blossy.flowerstore.domain.usecase.product

import com.blossy.flowerstore.domain.repository.ProductRepository
import javax.inject.Inject

class GetProductsUseCase @Inject constructor(
    private val  productRepository: ProductRepository
){
    suspend operator fun invoke(
        keyword: String? = null,
        categories: Set<String> = emptySet(),
        minPrice: Int? = null,
        maxPrice: Int? = null,
        page: Int = 1
    ) = productRepository.searchProducts(keyword, categories, minPrice, maxPrice, page)

}