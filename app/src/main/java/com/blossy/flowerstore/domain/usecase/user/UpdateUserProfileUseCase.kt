package com.blossy.flowerstore.domain.usecase.user

import com.blossy.flowerstore.domain.model.UpdateProfileModel
import com.blossy.flowerstore.domain.repository.UserRepository
import javax.inject.Inject

class UpdateUserProfileUseCase @Inject constructor(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(id: String, updateProfileModel: UpdateProfileModel) = userRepository.updateUserProfile(id, updateProfileModel)

}