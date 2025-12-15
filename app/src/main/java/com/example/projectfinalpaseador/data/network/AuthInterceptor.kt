package com.example.projectfinalpaseador.data.network

import android.content.Context
import okhttp3.Interceptor
import okhttp3.Response
import com.example.projectfinalpaseador.data.repository.TokenRepository

class AuthInterceptor(context: Context) : Interceptor {
    private val tokenRepository = TokenRepository(context)

    override fun intercept(chain: Interceptor.Chain): Response {
        val requestBuilder = chain.request().newBuilder()

        tokenRepository.getToken()?.let { token ->
            requestBuilder.addHeader("Authorization", "Bearer $token")
        }

        return chain.proceed(requestBuilder.build())
    }
}