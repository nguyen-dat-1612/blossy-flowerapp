package com.blossy.flowerstore.domain.usecase.favorite

import com.blossy.flowerstore.domain.repository.FavoriteRepository
import javax.inject.Inject

class AddFavoriteUseCase @Inject constructor(
    private val favoriteRepository: FavoriteRepository
){
    suspend operator fun invoke(productId: String) = favoriteRepository.addFavoriteProduct(productId)
}