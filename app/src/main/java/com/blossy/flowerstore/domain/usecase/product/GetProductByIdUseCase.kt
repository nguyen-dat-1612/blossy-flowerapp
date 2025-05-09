package com.blossy.flowerstore.domain.usecase.product

import com.blossy.flowerstore.domain.model.Product
import com.blossy.flowerstore.domain.repository.ProductRepository
import com.blossy.flowerstore.domain.utils.Result
import javax.inject.Inject

class GetProductByIdUseCase @Inject constructor(
    private val productRepository: ProductRepository
) {
    suspend operator fun invoke(id: String): Result<Product>  = productRepository.getProductById(id)
}