package com.blossy.flowerstore.domain.usecase.product

import com.blossy.flowerstore.domain.repository.ProductRepository
import javax.inject.Inject

class GetTopProductsUseCase @Inject constructor(
    private val productRepository: ProductRepository
){
    suspend operator fun invoke() = productRepository.getTopProducts()
}