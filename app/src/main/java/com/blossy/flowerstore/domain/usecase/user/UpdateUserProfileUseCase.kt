package com.blossy.flowerstore.domain.usecase.user

import com.blossy.flowerstore.domain.model.User
import com.blossy.flowerstore.domain.repository.UserRepository
import javax.inject.Inject

class UpdateUserProfileUseCase @Inject constructor(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(id: String, name: String, email: String) = userRepository.updateUserProfile(id, name, email)

}