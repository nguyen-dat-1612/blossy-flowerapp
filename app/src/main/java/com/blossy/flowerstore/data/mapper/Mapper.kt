package com.blossy.flowerstore.data.mapper

import com.blossy.flowerstore.data.remote.dto.AddToCartDTO
import com.blossy.flowerstore.data.remote.dto.AddressDTO
import com.blossy.flowerstore.data.remote.dto.CartItemDTO
import com.blossy.flowerstore.data.remote.dto.CartOrderDTO
import com.blossy.flowerstore.data.remote.dto.CategoryDTO
import com.blossy.flowerstore.data.remote.dto.CreateOrderDTO
import com.blossy.flowerstore.data.remote.dto.FavoriteDTO
import com.blossy.flowerstore.data.remote.dto.LoginRequest
import com.blossy.flowerstore.data.remote.dto.LoginResponse
import com.blossy.flowerstore.data.remote.dto.NotificationDTO
import com.blossy.flowerstore.data.remote.dto.OrderDTO
import com.blossy.flowerstore.data.remote.dto.OrderItemDTO
import com.blossy.flowerstore.data.remote.dto.OrderListDTO
import com.blossy.flowerstore.data.remote.dto.PaginationDTO
import com.blossy.flowerstore.data.remote.dto.PaymentDTO
import com.blossy.flowerstore.data.remote.dto.ProductDTO
import com.blossy.flowerstore.data.remote.dto.ProductListDTO
import com.blossy.flowerstore.data.remote.dto.RegisterDTO
import com.blossy.flowerstore.data.remote.dto.ReviewDTO
import com.blossy.flowerstore.data.remote.dto.ShippingAddressDTO
import com.blossy.flowerstore.data.remote.dto.UpdateCartDTO
import com.blossy.flowerstore.data.remote.dto.UpdatePasswordDTO
import com.blossy.flowerstore.data.remote.dto.UpdateUserDTO
import com.blossy.flowerstore.data.remote.dto.UserDTO
import com.blossy.flowerstore.domain.model.AddressModel
import com.blossy.flowerstore.domain.model.NotificationModel
import com.blossy.flowerstore.domain.model.OrderItemModel
import com.blossy.flowerstore.domain.model.OrderModel
import com.blossy.flowerstore.domain.model.PaymentModel
import com.blossy.flowerstore.domain.model.ReviewModel
import com.blossy.flowerstore.domain.model.ShippingAddressModel
import com.blossy.flowerstore.domain.model.UpdateProfileModel
import com.blossy.flowerstore.domain.model.CartItemModel
import com.blossy.flowerstore.domain.model.PaginationModel
import com.blossy.flowerstore.domain.model.ProductModel
import com.blossy.flowerstore.domain.model.UserModel
import com.blossy.flowerstore.domain.model.request.AddToCartModel
import com.blossy.flowerstore.domain.model.request.LoginModel
import com.blossy.flowerstore.domain.model.request.RegisterModel
import com.blossy.flowerstore.domain.model.request.UpdateCartModel
import com.blossy.flowerstore.domain.model.request.UpdatePasswordModel
import com.blossy.flowerstore.domain.model.CategoryModel
import com.blossy.flowerstore.domain.model.FavoriteModel
import com.blossy.flowerstore.domain.model.response.LoginResponseModel
import com.blossy.flowerstore.domain.model.OrderListModel
import com.blossy.flowerstore.domain.model.request.CartOrderModel
import com.blossy.flowerstore.domain.model.request.CreateOrderModel
import com.blossy.flowerstore.domain.model.response.ProductListModel
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

//Request

fun LoginModel.toRequest(): LoginRequest {
    return LoginRequest(
        email = email,
        password = password
    )
}
fun RegisterModel.toRequest(): RegisterDTO {
    return RegisterDTO(
        name = name,
        email = email,
        password = password
    )
}

fun UpdatePasswordModel.toRequest(): UpdatePasswordDTO {
    return UpdatePasswordDTO(
        oldPassword = oldPassword,
        newPassword = newPassword
    )
}
fun AddToCartModel.toRequest(): AddToCartDTO {
    return AddToCartDTO(
        productId = productId,
        quantity = quantity
    )
}

fun UpdateCartModel.toRequest(): UpdateCartDTO {
    return UpdateCartDTO(
        productId = productId,
        quantity = quantity
    )
}

fun UpdateProfileModel.toRequest(): UpdateUserDTO {
    return UpdateUserDTO(
        name = name,
        email = email
    )
}

fun AddressModel.toRequest(): AddressDTO {
    return AddressDTO(
        id = id,
        name = name,
        phone = phone,
        address = address,
        isDefault = isDefault
    )
}

fun CreateOrderModel.toRequest(): CreateOrderDTO {
    return CreateOrderDTO(
        orderItems = cartItems.map { it.toRequest() },
        shippingAddress = shippingAddress.toRequest(),
        paymentMethod = paymentMethod
    )
}

fun CartOrderModel.toRequest() : CartOrderDTO {
    return CartOrderDTO(
        productId = productId,
        quantity = quantity
    )
}



fun ShippingAddressModel.toRequest(): ShippingAddressDTO {
    return ShippingAddressDTO(
        name = name,
        phone = phone,
        address = address
    )
}



// Response
fun AddressDTO.toAddress(): AddressModel {
    return AddressModel(
        id = id,
        name = name,
        phone = phone,
        address = address,
        isDefault = isDefault
    )
}
fun NotificationDTO.toNotification(): NotificationModel {
    return  NotificationModel(
        userId = userId,
        type = type,
        title = title,
        message = message,
        read = isRead,
        createdAt = parseCreatedAt(createdAt)
    )
}

private fun parseCreatedAt(dateString: String): LocalDateTime {
    return LocalDateTime.parse(dateString, DateTimeFormatter.ISO_DATE_TIME)
}

fun UserDTO.toUser(): UserModel {
    return UserModel(
        id = id,
        name = name,
        email = email,
        role = role,
        emailVerified = emailVerified ?: false,
        avatar = avatar ?: ""
    )
}

fun CategoryDTO.toCategory(): CategoryModel {
    return CategoryModel(
        id = id,
        name = name,
        description = description ?: "",
        image = image ?: "",
        productCount = productCount ?: 0
    )
}
fun LoginResponse.toLoginResponse(): LoginResponseModel {
    return LoginResponseModel(
        token = token,
        user = user.toUser()
    )
}

fun ReviewDTO.toReview(): ReviewModel {
    return ReviewModel(
        userId = userId,
        rating = rating,
        comment = comment
    )
}

fun ProductDTO.toProduct(): ProductModel {
    return ProductModel(
        id = id,
        name = name,
        description = description,
        price = price,
        images = images,
        category = category.toCategory(),
        stock = stock,
        rating = rating,
        numReviews = numReviews,
        reviews = reviews.map { it.toReview()})
}

fun CartItemDTO.toCartItem(): CartItemModel {
    return CartItemModel(
        product = product.toProduct(),
        quantity = quantity
    )
}

fun FavoriteDTO.toFavorite() : FavoriteModel {
    return FavoriteModel(
        id = id,
        userId = userId,
        productId = productId
    )
}

fun ProductListDTO.toProductList(): ProductListModel {
    return ProductListModel(
        count = count,
        total = total,
        pagination = pagination.toPagination(),
        products = products.map { it.toProduct() }
    )
}

fun PaginationDTO.toPagination(): PaginationModel {
    return PaginationModel(
        page = page,
        limit = limit,
        pages = pages
    )
}

fun ProductListDTO.toListProduct() :ProductListModel {
    return ProductListModel(
        count = count,
        total = total,
        pagination = pagination.toPagination(),
        products = products.map { it.toProduct() }
    )
}

fun OrderItemDTO.toOrderItem(): OrderItemModel {
    return OrderItemModel(
        product = product,
        quantity = quantity,
        id = id,
        image = image,
        name = name,
        price = price
    )
}

fun OrderDTO.toOrder(): OrderModel {
    return OrderModel(
        id = id,
        user = user,
        orderItems = orderItems.map { it.toOrderItem() },
        shippingAddress = shippingAddress.toShippingAddress(),
        paymentMethod = paymentMethod,
        shippingPrice = shippingPrice,
        totalPrice = totalPrice,
        isPaid = isPaid,
        isDelivered = isDelivered,
        paymentResult = paymentResult,
        status = status,
        createdAt = createdAt
    )

}

fun ShippingAddressDTO.toShippingAddress() : ShippingAddressModel {
    return ShippingAddressModel(
        name = name,
        phone = phone,
        address = address
    )
}

fun PaymentDTO.toPayment(): PaymentModel {
    return PaymentModel(
        paymentId = paymentId,
        paymentUrl = paymentUrl,
        deeplink = deeplink,
        qrCodeUrl = qrCodeUrl,
        requestId = requestId,
        orderId = orderId
    )
}

fun OrderListDTO.toListOrder(): OrderListModel {
    return OrderListModel(
        count = count,
        total = total,
        currentPage = currentPage,
        totalPages = totalPages,
        orders = orders.map { it.toOrder() }
    )
}


