package com.blossy.flowerstore.domain.usecase.user

import com.blossy.flowerstore.data.remote.dto.AddressResponse
import com.blossy.flowerstore.domain.repository.UserRepository
import javax.inject.Inject

class UpdateAddressUseCase @Inject constructor(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(address: AddressResponse) = userRepository.updateAddress(address)
}