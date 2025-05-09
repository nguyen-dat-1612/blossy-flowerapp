package com.blossy.flowerstore.data.repository

import com.blossy.flowerstore.data.mapper.toAddress
import com.blossy.flowerstore.data.mapper.toUser
import com.blossy.flowerstore.data.remote.api.UserApi
import com.blossy.flowerstore.data.remote.dto.AddressResponse
import com.blossy.flowerstore.domain.model.Address
import com.blossy.flowerstore.domain.model.User
import com.blossy.flowerstore.domain.repository.UserRepository
import com.blossy.flowerstore.domain.utils.Result
import com.blossy.flowerstore.data.mapper.toAddress
import javax.inject.Inject

class UserRepositoryImpl @Inject constructor(
    private val userApi: UserApi
): UserRepository {
    override suspend fun getUserProfile(): Result<User> {
        return try {
            val response = userApi.getUserProfile()
            if (response.isSuccessful) {
                val body = response.body()
                if (body != null && body.success && body.data != null) {
                    val user = body.data.toUser()
                    Result.Success(user)
                } else {
                    Result.Error(body?.message ?: "An error occurred")
                }
            } else {
                Result.Error(response.message())
            }
        } catch (e: Exception) {
            Result.Error(e.message ?: "An error occurred")
        }
    }

    override suspend fun getUserAddresses(): Result<List<Address>> {
        return try {
            val response = userApi.getUserAddresses()
            if (response.isSuccessful) {
                val body = response.body()
                if (body != null && body.success && body.data != null) {
                    val addresses = body.data.map { it.toAddress() }
                    Result.Success(addresses)
                } else {
                    Result.Error(body?.message ?: "An error occurred")
                }
            } else {
                Result.Error(response.message())
            }
        } catch (e: Exception) {
            Result.Error(e.message ?: "An error occurred")
        }
    }

    override suspend fun getDefaultAddress(): Result<Address> {
        return try {
            val response = userApi.getDefaultAddress()
            if (response.isSuccessful) {
                val body = response.body()
                if (body != null && body.success && body.data != null) {
                    val address = body.data.toAddress()
                    Result.Success(address)
                } else {
                    Result.Error(body?.message ?: "An error occurred")
                }
            } else {
                Result.Error(response.message())
            }
        } catch (e: Exception) {
            Result.Error(e.message ?: "An error occurred")
        }
    }

    override suspend fun addAddress(address: AddressResponse): Result<List<Address>> {
        return try {
            val response = userApi.addAddress(address)
            if (response.isSuccessful) {
                val body = response.body()
                if (body != null && body.success ) {
                    val addresses = body.data?.map { it.toAddress() } ?: emptyList()
                    Result.Success(addresses)
                    } else {
                    Result.Error(body?.message ?: "An error occurred")
                }
            } else {
                Result.Error(response.message())
            }
        } catch (e: Exception) {
            Result.Error(e.message ?: "An error occurred")
        }
    }

    override suspend fun updateAddress(address: AddressResponse): Result<List<Address>> {
        return try {
            val response = userApi.updateAddress(address)
            if (response.isSuccessful) {
                val body = response.body()
                if (body != null && body.success && body.data != null) {
                    val addresses = body.data?.map { it.toAddress() } ?: emptyList()
                    Result.Success(addresses)
                } else {
                    Result.Error(body?.message ?: "An error occurred")
                }
            } else {
                Result.Error(response.message())
            }
        } catch (e: Exception) {
            Result.Error(e.message ?: "An error occurred")
        }
    }

    override suspend fun deleteAddress(addressId: String): Result<List<Address>> {
        return try {
            val response = userApi.deleteAddress(addressId)
            if (response.isSuccessful) {
                val body = response.body()
                if (body != null && body.success
                    && body.data != null) {
                    val addresses = body.data?.map { it.toAddress() } ?: emptyList()
                    Result.Success(addresses)
                } else {
                    Result.Error(body?.message ?: "An error occurred")
                    }
            } else {
                Result.Error(response.message())
            }
        } catch (e: Exception) {
            Result.Error(e.message ?: "An error occurred")
        }
    }

}