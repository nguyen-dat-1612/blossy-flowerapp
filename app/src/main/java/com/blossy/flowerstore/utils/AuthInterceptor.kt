package com.blossy.flowerstore.utils

import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException

class AuthInterceptor(private val tokenManager: SecureTokenManager) : Interceptor {

    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val accessToken = tokenManager.accessToken
        var request = chain.request()

        if (request.header("No-Authentication") != null) {
            return chain.proceed(request);
        }

        if (accessToken != null) {
            request = request.newBuilder()
                .addHeader("Authorization", "Bearer $accessToken")
                .build()
        }

        return chain.proceed(request)
    }
}
