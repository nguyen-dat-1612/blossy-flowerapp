package com.blossy.flowerstore.data.repository

import com.blossy.flowerstore.data.mapper.toAddress
import com.blossy.flowerstore.data.mapper.toUser
import com.blossy.flowerstore.data.remote.api.UserApi
import com.blossy.flowerstore.data.remote.dto.AddressResponse
import com.blossy.flowerstore.domain.model.Address
import com.blossy.flowerstore.domain.model.User
import com.blossy.flowerstore.domain.repository.UserRepository
import com.blossy.flowerstore.domain.utils.Result
import com.blossy.flowerstore.data.remote.dto.PushRequest
import com.blossy.flowerstore.data.remote.dto.UpdateUserRequest
import com.blossy.flowerstore.data.remote.utils.safeApiCall
import com.blossy.flowerstore.data.remote.utils.toResult
import kotlinx.coroutines.withTimeout
import javax.inject.Inject

class UserRepositoryImpl @Inject constructor(
    private val userApi: UserApi
): UserRepository {
    override suspend fun getUserProfile(): Result<User>  = withTimeout(TIMEOUT) {
        safeApiCall {
            userApi.getUserProfile().toResult { response ->
                response.toUser()
            }
        }
    }

    override suspend fun getUserAddresses(): Result<List<Address>> = withTimeout(TIMEOUT) {
        safeApiCall {
            userApi.getUserAddresses().toResult { response ->
                response.map { it.toAddress() }
            }
        }
    }

    override suspend fun getDefaultAddress(): Result<Address> = withTimeout(TIMEOUT) {
        safeApiCall {
            userApi.getDefaultAddress().toResult { response ->
                response.toAddress()
            }
        }
    }

    override suspend fun addAddress(address: AddressResponse): Result<Address> = withTimeout(TIMEOUT) {
        safeApiCall {
            userApi.addAddress(address).toResult { response ->
                response.toAddress()
            }
        }
    }

    override suspend fun updateAddress(address: AddressResponse): Result<Address> = withTimeout(TIMEOUT) {
        safeApiCall {
            userApi.updateAddress(address).toResult() { response ->
                response.toAddress()
            }
        }
    }

    override suspend fun deleteAddress(addressId: String): Result<Address> = withTimeout(TIMEOUT){
        safeApiCall {
            userApi.deleteAddress(addressId).toResult { response ->
                response.toAddress()
            }
        }
    }

    override suspend fun updateFcmToken(token: String): Result<Boolean> = withTimeout(TIMEOUT){
        safeApiCall {
            userApi.updateFcmToken(PushRequest(token)).toResult()
        }
    }

    override suspend fun logout(): Result<Boolean> {
        TODO("Not yet implemented")
    }

    override suspend fun updateUserProfile(id: String, name: String, email: String): Result<User> = withTimeout(TIMEOUT){
        safeApiCall {
            userApi.updateUser(id, UpdateUserRequest(name, email)).toResult { response ->
                response.toUser()
            }
        }
    }

    override suspend fun getAddressById(id: String): Result<Address> = withTimeout(TIMEOUT) {
        safeApiCall {
            userApi.getAddressById(id).toResult { response ->
                response.toAddress()
            }
        }
    }

    companion object {
        private const val TIMEOUT = 5000L
    }

}