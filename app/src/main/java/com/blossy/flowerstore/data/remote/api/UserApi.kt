package com.blossy.flowerstore.data.remote.api

import com.blossy.flowerstore.data.remote.dto.AddressResponse
import com.blossy.flowerstore.data.remote.dto.PushRequest
import com.blossy.flowerstore.data.remote.dto.UserProfileResponse
import com.blossy.flowerstore.data.remote.utils.BaseResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface UserApi {

    @GET("users/address")
    suspend fun getUserAddresses(): Response<BaseResponse<List<AddressResponse>>>

    @GET("users/address/default")
    suspend fun getDefaultAddress(): Response<BaseResponse<AddressResponse>>

    @GET("users/me")
    suspend fun getUserProfile(): Response<BaseResponse<UserProfileResponse>>

    @POST("users/address/default/{id}")
    suspend fun setDefaultAddress(@Path("id") id: String): Response<BaseResponse<AddressResponse>>

    @POST("users/address")
    suspend fun addAddress(@Body address: AddressResponse):  Response<BaseResponse<List<AddressResponse>>>

    @DELETE("users/address")
    suspend fun deleteAddress(@Query("addressId") addressId: String):  Response<BaseResponse<List<AddressResponse>>>

    @PUT("users/address")
    suspend fun updateAddress(@Body address: AddressResponse):  Response<BaseResponse<List<AddressResponse>>>

    @PUT("users/fcm")
    suspend fun updateFcmToken(@Body pushRequest: PushRequest): Response<BaseResponse<Boolean>>

}