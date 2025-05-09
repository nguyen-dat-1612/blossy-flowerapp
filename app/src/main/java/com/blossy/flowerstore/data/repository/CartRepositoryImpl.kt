package com.blossy.flowerstore.data.repository

import com.blossy.flowerstore.data.remote.api.CartApi
import com.blossy.flowerstore.data.remote.dto.AddtoCartRequest
import com.blossy.flowerstore.domain.model.CartItem
import com.blossy.flowerstore.domain.repository.CartRepository
import javax.inject.Inject
import com.blossy.flowerstore.domain.utils.Result
class CartRepositoryImpl @Inject constructor(
    private val cartApi: CartApi
): CartRepository {
    override suspend fun getCart(): Result<List<CartItem>> {
        return try {
            val response = cartApi.getCart()
            if (response.isSuccessful) {
                val body = response.body()
                if (body != null && body.success) {
                    val data = body.data
                    if (data != null) {
                        Result.Success(body.data.items)
                    } else {
                        Result.Empty
                    }
                } else {
                    Result.Error("Failed to fetch cart items")
                }
            } else {
                Result.Error("Failed to fetch cart items")
            }
        }
        catch (e: Exception) {
            Result.Error(e.message ?: "An error occurred")
        }
    }

    override suspend fun addToCart(productId: String, quantity: Int): Result<List<CartItem>> {
        return try {
            val response = cartApi.addToCart(AddtoCartRequest(productId, quantity))
            if (response.isSuccessful) {
                val body = response.body()
                if (body != null && body.success) {
                    Result.Success(body.data!!.items)
                } else {
                    Result.Error("Failed to add product to cart")
                }
            } else {
                Result.Error("Failed to add product to cart")
            }
        } catch (e: Exception) {
            Result.Error(e.message ?: "An error occurred")
        }
    }

    override suspend fun removeCart(productId: String): Result<Boolean> {
        return try {
            val response = cartApi.removeFromCart(productId)
            if (response.isSuccessful) {
                val body = response.body()
                if (body != null && body.success) {
                    Result.Success(true)
                } else {
                    Result.Error("Failed to remove product from cart")
                }
            } else {
                Result.Error("Failed to remove product from cart")
            }
        } catch (e: Exception) {
            Result.Error(e.message ?: "An error occurred")
        }
    }

    override suspend fun updateCart(productId: String, quantity: Int): Result<Boolean> {
        return try {
            val response = cartApi.updateCart(AddtoCartRequest(productId, quantity))
            if (response.isSuccessful) {
                val body = response.body()
                if (body != null && body.success) {
                    Result.Success(true)
                } else {
                    Result.Error("Failed to update cart")
                }
            } else {
                Result.Error("Failed to update cart")
            }
        } catch (e: Exception) {
            Result.Error(e.message ?: "An error occurred")
        }
    }

    override suspend fun clearCart(): Result<Boolean> {
        return try {
            val response = cartApi.clearCart()
            if (response.isSuccessful) {
                val body = response.body()
                if (body != null && body.success) {
                    Result.Success(true)
                } else {
                    Result.Error("Failed to clear cart")
                }
            } else {
                Result.Error("Failed to clear cart")
            }
        } catch (e: Exception) {
            Result.Error(e.message ?: "An error occurred")
        }
    }


}