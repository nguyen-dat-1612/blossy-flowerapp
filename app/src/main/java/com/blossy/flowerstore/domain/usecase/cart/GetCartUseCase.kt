package com.blossy.flowerstore.domain.usecase.cart

import com.blossy.flowerstore.domain.model.Cart
import com.blossy.flowerstore.domain.repository.CartRepository
import javax.inject.Inject

class GetCartUseCase @Inject constructor(
    private val cartRepository: CartRepository
) {
    suspend operator fun invoke() = cartRepository.getCart()
}