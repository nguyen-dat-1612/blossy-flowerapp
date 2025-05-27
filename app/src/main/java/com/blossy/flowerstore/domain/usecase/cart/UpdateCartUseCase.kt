package com.blossy.flowerstore.domain.usecase.cart

import com.blossy.flowerstore.domain.model.request.UpdateCartModel
import com.blossy.flowerstore.domain.repository.CartRepository
import javax.inject.Inject

class UpdateCartUseCase @Inject constructor(
    private val cartRepository: CartRepository
){
    suspend operator fun invoke(
        updateCartModel: UpdateCartModel
    ) = cartRepository.updateCart(updateCartModel)

}