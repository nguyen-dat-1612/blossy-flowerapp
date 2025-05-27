package com.blossy.flowerstore.data.repository

import com.blossy.flowerstore.data.mapper.toAddress
import com.blossy.flowerstore.data.mapper.toRequest
import com.blossy.flowerstore.data.mapper.toUser
import com.blossy.flowerstore.data.remote.api.UserApi
import com.blossy.flowerstore.data.remote.dto.AddressDTO
import com.blossy.flowerstore.domain.repository.UserRepository
import com.blossy.flowerstore.domain.utils.Result
import com.blossy.flowerstore.data.remote.dto.PushFCMTO
import com.blossy.flowerstore.data.remote.utils.safeApiCall
import com.blossy.flowerstore.data.remote.utils.toResult
import com.blossy.flowerstore.domain.model.AddressModel
import com.blossy.flowerstore.domain.model.UpdateProfileModel
import com.blossy.flowerstore.domain.model.UserModel
import kotlinx.coroutines.withTimeout
import javax.inject.Inject

class UserRepositoryImpl @Inject constructor(
    private val userApi: UserApi
): UserRepository {

    override suspend fun getUserProfile(): Result<UserModel> {
        return safeApiCall {
            userApi.getUserProfile().toResult { response ->
                response.toUser()
            }
        }
    }

    override suspend fun getUserAddresses(): Result<List<AddressModel>> {
        return safeApiCall {
            userApi.getUserAddresses().toResult { response ->
                response.map { it.toAddress() }
            }
        }
    }

    override suspend fun getDefaultAddress():Result<AddressModel> {
        return safeApiCall {
            userApi.getDefaultAddress().toResult { response ->
                response.toAddress()
            }
        }
    }

    override suspend fun addAddress(address: AddressModel):Result<AddressModel>  {
        return safeApiCall {
            val request = address.toRequest();
            userApi.addAddress(request).toResult { response ->
                response.toAddress()
            }
        }
    }

    override suspend fun updateAddress(address: AddressModel):Result<AddressModel>{
        return safeApiCall {
            val request = address.toRequest();
            userApi.updateAddress(request).toResult() { response ->
                response.toAddress()
            }
        }
    }

    override suspend fun deleteAddress(addressId: String): Result<AddressModel> {
        return safeApiCall {
            userApi.deleteAddress(addressId).toResult { response ->
                response.toAddress()
            }
        }
    }

    override suspend fun updateFcmToken(token: String): Result<Boolean>{
        return safeApiCall {
            userApi.updateFcmToken(PushFCMTO(token)).toResult()
        }
    }


    override suspend fun updateUserProfile(id: String, updateProfileModel: UpdateProfileModel): Result<UserModel> {
        return safeApiCall {
            val request = updateProfileModel.toRequest()
            userApi.updateUser(id, request).toResult { response ->
                response.toUser()
            }
        }
    }

    override suspend fun getAddressById(id: String): Result<AddressModel> {
        return safeApiCall {
            userApi.getAddressById(id).toResult { response ->
                response.toAddress()
            }
        }
    }


}