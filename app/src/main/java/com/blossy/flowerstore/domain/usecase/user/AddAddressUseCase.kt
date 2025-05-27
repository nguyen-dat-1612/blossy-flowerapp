package com.blossy.flowerstore.domain.usecase.user

import com.blossy.flowerstore.data.remote.dto.AddressDTO
import com.blossy.flowerstore.domain.model.AddressModel
import com.blossy.flowerstore.domain.repository.UserRepository
import javax.inject.Inject

class AddAddressUseCase @Inject constructor(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(address: AddressModel) = userRepository.addAddress(address)
}