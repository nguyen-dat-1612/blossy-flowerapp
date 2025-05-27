package com.blossy.flowerstore.data.remote.api

import com.blossy.flowerstore.data.remote.dto.AddressDTO
import com.blossy.flowerstore.data.remote.dto.PushFCMTO
import com.blossy.flowerstore.data.remote.dto.UpdateUserDTO
import com.blossy.flowerstore.data.remote.dto.UserDTO
import com.blossy.flowerstore.data.remote.utils.BaseResponse
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface UserApi {

    @GET("users/address")
    suspend fun getUserAddresses(): BaseResponse<List<AddressDTO>>

    @GET("users/address/default")
    suspend fun getDefaultAddress(): BaseResponse<AddressDTO>

    @GET("users/me")
    suspend fun getUserProfile(): BaseResponse<UserDTO>

    @POST("users/address/default/{id}")
    suspend fun setDefaultAddress(@Path("id") id: String): BaseResponse<AddressDTO>

    @POST("users/address")
    suspend fun addAddress(@Body address: AddressDTO):  BaseResponse<AddressDTO>

    @DELETE("users/address")
    suspend fun deleteAddress(@Query("addressId") addressId: String): BaseResponse<AddressDTO>

    @PUT("users/address")
    suspend fun updateAddress(@Body address: AddressDTO): BaseResponse<AddressDTO>

    @PUT("users/fcm")
    suspend fun updateFcmToken(@Body pushRequest: PushFCMTO): BaseResponse<Boolean>

    @POST("users/logout")
    suspend fun logout(): BaseResponse<Boolean>

    @PUT("users/{id}")
    suspend fun updateUser(@Path("id") id: String, @Body user: UpdateUserDTO): BaseResponse<UserDTO>

    @GET("users/address/{id}")
    suspend fun getAddressById(@Path("id") id: String): BaseResponse<AddressDTO>
}