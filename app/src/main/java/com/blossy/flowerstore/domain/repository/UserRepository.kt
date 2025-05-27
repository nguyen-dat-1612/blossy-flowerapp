package com.blossy.flowerstore.domain.repository

import com.blossy.flowerstore.data.remote.dto.AddressDTO
import com.blossy.flowerstore.domain.model.AddressModel
import com.blossy.flowerstore.domain.model.UpdateProfileModel
import com.blossy.flowerstore.domain.model.UserModel
import com.blossy.flowerstore.domain.utils.Result

interface UserRepository  {

    suspend fun getUserProfile(): Result<UserModel>

    suspend fun getUserAddresses(): Result<List<AddressModel>>

    suspend fun getDefaultAddress(): Result<AddressModel>

    suspend fun addAddress(address: AddressModel): Result<AddressModel>

    suspend fun updateAddress(address: AddressModel): Result<AddressModel>

    suspend fun deleteAddress(addressId: String): Result<AddressModel>

    suspend fun updateFcmToken(token: String): Result<Boolean>

    suspend fun updateUserProfile(id: String, updateProfileModel: UpdateProfileModel): Result<UserModel>

    suspend fun getAddressById(id: String): Result<AddressModel>

}