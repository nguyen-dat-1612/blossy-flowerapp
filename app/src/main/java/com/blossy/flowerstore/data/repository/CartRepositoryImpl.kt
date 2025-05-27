package com.blossy.flowerstore.data.repository

import com.blossy.flowerstore.data.mapper.toCartItem
import com.blossy.flowerstore.data.mapper.toRequest
import com.blossy.flowerstore.data.remote.api.CartApi
import com.blossy.flowerstore.domain.repository.CartRepository
import javax.inject.Inject
import com.blossy.flowerstore.data.remote.utils.safeApiCall
import com.blossy.flowerstore.data.remote.utils.toResult
import com.blossy.flowerstore.domain.model.CartItemModel
import com.blossy.flowerstore.domain.model.request.AddToCartModel
import com.blossy.flowerstore.domain.model.request.UpdateCartModel
import com.blossy.flowerstore.domain.utils.Result
import kotlinx.coroutines.withTimeout

class CartRepositoryImpl @Inject constructor(
    private val cartApi: CartApi
): CartRepository {
    override suspend fun getCart(): Result<List<CartItemModel>> {
        return safeApiCall {
            cartApi.getCart().toResult { response ->
                response.items.map {
                    it.toCartItem()
                }
            }
        }
    }

    override suspend fun addToCart(addToCartModel: AddToCartModel): Result<List<CartItemModel>> {
        return safeApiCall {
            val request = addToCartModel.toRequest();
            cartApi.addToCart(request).toResult { response ->
                response.items.map {
                    it.toCartItem()
                }
            }
        }
    }

    override suspend fun removeCart(productId: String): Result<Boolean> {
        return safeApiCall {
            cartApi.removeFromCart(productId).toResult() {
                true
            }
        }
    }

    override suspend fun updateCart(updateCartModel: UpdateCartModel): Result<Boolean> {
        return safeApiCall {
            val request = updateCartModel.toRequest();
            cartApi.updateCart(request).toResult() {
                true
            }
        }
    }

    override suspend fun clearCart(): Result<Boolean> {
        return safeApiCall {
            cartApi.clearCart().toResult() {
                true
            }
        }
    }
}