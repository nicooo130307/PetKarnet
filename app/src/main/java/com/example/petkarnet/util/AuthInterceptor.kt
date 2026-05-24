package com.example.petkarnet.util

import android.content.Context
import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor(private val context: Context) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val token = obtenerToken(context)  // lo lees de SharedPreferences
        val request = chain.request().newBuilder()
        if (!token.isNullOrBlank()) {
            request.addHeader("Authorization", "Bearer $token")
        }
        return chain.proceed(request.build())
    }
}

fun obtenerToken(context: Context): String? {
    val prefs = context.getSharedPreferences("petkarnet_prefs", Context.MODE_PRIVATE)
    return prefs.getString("jwt_token", null)
}