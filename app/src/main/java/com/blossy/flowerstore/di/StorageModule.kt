package com.blossy.flowerstore.di

import android.content.Context
import android.content.SharedPreferences
import com.blossy.flowerstore.utils.manager.EncryptionManager
import com.blossy.flowerstore.utils.manager.SecureTokenManager
import com.blossy.flowerstore.utils.manager.SettingsManager
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
            "BlossyFlowerPrefs",
            Context.MODE_PRIVATE
        )
    }

    @Provides
    @Singleton
    fun provideSettingsManager(
        @ApplicationContext context: Context
    ) : SettingsManager {
        return SettingsManager(context)
    }

}