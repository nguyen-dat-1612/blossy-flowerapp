package com.blossy.flowerstore.domain.usecase.user

import com.blossy.flowerstore.domain.repository.UserRepository
import javax.inject.Inject

class AddressByIdUseCase @Inject constructor(
    private val userRepository: UserRepository
){
    suspend operator fun invoke(id: String) = userRepository.getAddressById(id)
}