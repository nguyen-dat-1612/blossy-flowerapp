package com.blossy.flowerstore.domain.usecase.cart

import com.blossy.flowerstore.domain.repository.CartRepository
import javax.inject.Inject

class AddToCartUseCase @Inject constructor(
    private val cartRepository: CartRepository
) {
    suspend operator fun invoke(productId: String, quantity: Int) =
        cartRepository.addToCart(productId, quantity)

}