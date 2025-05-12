package com.blossy.flowerstore

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.appcompat.app.AppCompatDelegate
import com.blossy.flowerstore.utils.SettingsManager
import com.google.firebase.FirebaseApp
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class App : Application() {

    @Inject lateinit var settingsManager: SettingsManager

    override fun onCreate() {
        super.onCreate()
        applyTheme()
    }

    fun applyTheme() {
        val isDarkMode = settingsManager.isDarkMode
        AppCompatDelegate.setDefaultNightMode(
            if (isDarkMode) AppCompatDelegate.MODE_NIGHT_YES
            else AppCompatDelegate.MODE_NIGHT_NO
        )
    }

}