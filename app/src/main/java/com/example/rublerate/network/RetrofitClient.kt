package com.example.rublerate.network

import com.example.rublerate.util.Constants
import com.example.rublerate.util.ErrorInterceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/**
 * Singleton object responsible for providing Retrofit instances configured with base URL,
 * OkHttpClient, and GsonConverterFactory.
 */
object RetrofitClient {
    // Lazy initialization of Retrofit instance
    val retrofit: Retrofit by lazy {
        // OkHttpClient with ErrorInterceptor added for error handling
        val client = OkHttpClient.Builder()
            .addInterceptor(ErrorInterceptor())
            .build()

        // Retrofit instance configured with base URL, OkHttpClient, and GsonConverterFactory
        Retrofit.Builder()
            .baseUrl(Constants.BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
}
