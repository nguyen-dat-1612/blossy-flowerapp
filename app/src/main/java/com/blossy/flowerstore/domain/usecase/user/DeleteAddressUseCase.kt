package com.blossy.flowerstore.domain.usecase.user

import com.blossy.flowerstore.domain.repository.UserRepository
import javax.inject.Inject

class DeleteAddressUseCase @Inject constructor(
    private val userRepository: UserRepository
){
    suspend operator fun invoke(addressId: String) = userRepository.deleteAddress(addressId)
}