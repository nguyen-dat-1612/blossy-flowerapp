package com.blossy.flowerstore.di

import com.blossy.flowerstore.data.repository.AuthRepositoryImpl
import com.blossy.flowerstore.data.repository.CartRepositoryImpl
import com.blossy.flowerstore.data.repository.CategoryRepositoryImpl
import com.blossy.flowerstore.data.repository.FavoriteRepositoryImpl
import com.blossy.flowerstore.data.repository.NotificationRepositoryImpl
import com.blossy.flowerstore.data.repository.OrderRepositoryImpl
import com.blossy.flowerstore.data.repository.PaymentRepositoryImpl
import com.blossy.flowerstore.data.repository.ProductRepositoryImpl
import com.blossy.flowerstore.data.repository.UserRepositoryImpl
import com.blossy.flowerstore.domain.repository.AuthRepository
import com.blossy.flowerstore.domain.repository.CartRepository
import com.blossy.flowerstore.domain.repository.CategoryRepository
import com.blossy.flowerstore.domain.repository.FavoriteRepository
import com.blossy.flowerstore.domain.repository.NotificationRepository
import com.blossy.flowerstore.domain.repository.OrderRepository
import com.blossy.flowerstore.domain.repository.PaymentRepository
import com.blossy.flowerstore.domain.repository.ProductRepository
import com.blossy.flowerstore.domain.repository.UserRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    @Binds
    @Singleton
    abstract fun bindAuthRepository(impl: AuthRepositoryImpl): AuthRepository

    @Binds
    @Singleton
    abstract fun bindCategoryRepository(impl: CategoryRepositoryImpl): CategoryRepository

    @Binds
    @Singleton
    abstract fun bindProductRepository(impl: ProductRepositoryImpl): ProductRepository

    @Binds
    @Singleton
    abstract fun bindCartRepository(impl: CartRepositoryImpl): CartRepository


    @Binds
    @Singleton
    abstract fun bindPaymentRepository(impl: PaymentRepositoryImpl): PaymentRepository

    @Binds
    @Singleton
    abstract fun bindUserRepository(impl: UserRepositoryImpl): UserRepository

    @Binds
    @Singleton
    abstract fun bindOrderRepository(impl: OrderRepositoryImpl): OrderRepository

    @Binds
    @Singleton
    abstract fun bindFavoriteRepository(impl: FavoriteRepositoryImpl): FavoriteRepository

    @Binds
    @Singleton
    abstract fun bindNotificationRepository(impl: NotificationRepositoryImpl): NotificationRepository

}