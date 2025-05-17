package com.blossy.flowerstore.domain.repository

import com.blossy.flowerstore.data.remote.dto.AddressResponse
import com.blossy.flowerstore.data.remote.utils.BaseResponse
import com.blossy.flowerstore.domain.model.Address
import com.blossy.flowerstore.domain.model.User
import com.blossy.flowerstore.domain.utils.Result
import retrofit2.Response
import retrofit2.http.GET

interface UserRepository  {

    suspend fun getUserProfile(): Result<User>

    suspend fun getUserAddresses(): Result<List<Address>>

    suspend fun getDefaultAddress(): Result<Address>

    suspend fun addAddress(address: AddressResponse): Result<Address>

    suspend fun updateAddress(address: AddressResponse): Result<Address>

    suspend fun deleteAddress(addressId: String): Result<Address>

    suspend fun updateFcmToken(token: String): Result<Boolean>

    suspend fun logout(): Result<Boolean>

    suspend fun updateUserProfile(id: String, name: String, email: String): Result<User>

    suspend fun getAddressById(id: String): Result<Address>

}