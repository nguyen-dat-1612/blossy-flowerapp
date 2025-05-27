package com.blossy.flowerstore.domain.model.request

import com.blossy.flowerstore.domain.model.CartItemModel
import com.blossy.flowerstore.domain.model.ShippingAddressModel

data class CreateOrderModel(
    val cartItems: List<CartOrderModel>,
    val shippingAddress: ShippingAddressModel,
    val paymentMethod: String
)