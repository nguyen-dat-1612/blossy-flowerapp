package com.blossy.flowerstore.domain.usecase.user

import com.blossy.flowerstore.domain.repository.UserRepository
import javax.inject.Inject

class GetUserAddressesUseCase @Inject constructor(
    private val userRepository: UserRepository
){
    suspend operator fun invoke() = userRepository.getUserAddresses()

}