package com.blossy.flowerstore.domain.usecase.user

import com.blossy.flowerstore.domain.repository.UserRepository
import javax.inject.Inject

class GetUseProfileUseCase @Inject constructor(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke() = userRepository.getUserProfile()
}