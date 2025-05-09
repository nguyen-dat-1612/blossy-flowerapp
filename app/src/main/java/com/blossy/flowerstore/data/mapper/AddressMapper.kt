package com.blossy.flowerstore.data.mapper

import com.blossy.flowerstore.data.remote.dto.AddressResponse
import com.blossy.flowerstore.domain.model.Address

fun AddressResponse.toAddress(): Address {
    return Address(
        id = id,
        name = name,
        phone = phone,
        address = address,
        isDefault = isDefault
    )
}