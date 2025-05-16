package com.blossy.flowerstore.data.repository

import com.blossy.flowerstore.data.remote.api.CartApi
import com.blossy.flowerstore.data.remote.dto.AddtoCartRequest
import com.blossy.flowerstore.domain.model.CartItem
import com.blossy.flowerstore.domain.repository.CartRepository
import javax.inject.Inject
import com.blossy.flowerstore.domain.utils.Result
import com.blossy.flowerstore.data.remote.utils.safeApiCall
import com.blossy.flowerstore.data.remote.utils.toResult
import kotlinx.coroutines.withTimeout

class CartRepositoryImpl @Inject constructor(
    private val cartApi: CartApi
): CartRepository {
    override suspend fun getCart(): Result<List<CartItem>> = withTimeout(TIMEOUT) {
        safeApiCall {
            cartApi.getCart().toResult { response ->
                response.items
            }
        }
    }

    override suspend fun addToCart(productId: String, quantity: Int): Result<List<CartItem>> = withTimeout(TIMEOUT){
        safeApiCall {
            cartApi.addToCart(AddtoCartRequest(productId, quantity)).toResult { response ->
                response.items
            }
        }
    }

    override suspend fun removeCart(productId: String): Result<Boolean> = withTimeout(TIMEOUT){
        safeApiCall {
            cartApi.removeFromCart(productId).toResult() {
                true
            }
        }
    }

    override suspend fun updateCart(productId: String, quantity: Int): Result<Boolean> = withTimeout(TIMEOUT) {
        safeApiCall {
            cartApi.updateCart(AddtoCartRequest(productId, quantity)).toResult() {
                true
            }
        }
    }

    override suspend fun clearCart(): Result<Boolean> = withTimeout(TIMEOUT){
        safeApiCall {
            cartApi.clearCart().toResult() {
                true
            }
        }
    }

    companion object {
        private const val TIMEOUT = 5000L
    }

}