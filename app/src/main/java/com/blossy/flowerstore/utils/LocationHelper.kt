package com.blossy.flowerstore.utils

import android.content.Context
import android.location.Location
import com.google.android.gms.location.LocationServices

class LocationHelper(private val context: Context) {
    private val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)

    fun getLastLocation(onSuccess: (Location?) -> Unit, onError: (Exception) -> Unit) {
        try {
            fusedLocationClient.lastLocation
                .addOnSuccessListener(onSuccess)
                .addOnFailureListener(onError)
        } catch (e: SecurityException) {
            onError(e)
        }
    }
}