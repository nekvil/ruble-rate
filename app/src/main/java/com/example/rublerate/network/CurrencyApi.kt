package com.example.rublerate.network

import retrofit2.Call
import retrofit2.http.GET

/**
 * Retrofit interface defining an HTTP GET request method for retrieving currency data from
 * the specified endpoint ("daily_json.js").
 */
interface CurrencyApi {
    @GET("daily_json.js")
    fun getCurrencies(): Call<Map<String, Any>> // Endpoint for retrieving currency data
}
