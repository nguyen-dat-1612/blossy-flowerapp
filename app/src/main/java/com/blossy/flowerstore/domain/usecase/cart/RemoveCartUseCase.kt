package com.blossy.flowerstore.domain.usecase.cart

import com.blossy.flowerstore.domain.repository.CartRepository
import javax.inject.Inject

class RemoveCartUseCase @Inject constructor(
    private val cartRepository: CartRepository
) {
    suspend operator fun invoke(productId: String) = cartRepository.removeCart(productId)

}