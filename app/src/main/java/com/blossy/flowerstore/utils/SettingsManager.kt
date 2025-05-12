package com.blossy.flowerstore.utils

import android.content.Context

class SettingsManager(context: Context) {
    private val prefs = context.getSharedPreferences("app_settings", Context.MODE_PRIVATE)

    var isDarkMode: Boolean
        get() = prefs.getBoolean("dark_mode", false)
        set(value) = prefs.edit().putBoolean("dark_mode", value).apply()

    var isPushEnabled: Boolean
        get() = prefs.getBoolean("push_notifications", true)
        set(value) = prefs.edit().putBoolean("push_notifications", value).apply()

    var isLocationEnabled: Boolean
        get() = prefs.getBoolean("location_services", true)
        set(value) = prefs.edit().putBoolean("location_services", value).apply()
}