package com.blossy.flowerstore.di

import android.content.Context
import android.content.SharedPreferences
import com.blossy.flowerstore.utils.EncryptionManager
import com.blossy.flowerstore.utils.SecureTokenManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object StorageModule {

    @Provides
    @Singleton
    fun provideEncryptionManager(): EncryptionManager {
        return EncryptionManager()
    }

    @Provides
    @Singleton
    fun provideSecureTokenManager(
        encryptionManager: EncryptionManager,
        sharedPreferences: SharedPreferences
    ): SecureTokenManager {
        return SecureTokenManager(encryptionManager, sharedPreferences)
    }

    @Provides
    @Singleton
    fun provideSharedPreferences(
        @ApplicationContext context: Context
    ): SharedPreferences {
        return context.getSharedPreferences(
            "BlossyFlowerPrefs", // Tên file preferences
            Context.MODE_PRIVATE
        )
    }

}