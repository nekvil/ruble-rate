package com.example.rublerate.util

import android.util.Log
import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException

/**
 * Interceptor to handle network errors.
 */
class ErrorInterceptor : Interceptor {
    private val tag = ErrorInterceptor::class.qualifiedName

    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val response = chain.proceed(request)

        if (!response.isSuccessful) {
            // Handle specific HTTP error codes
            when (response.code()) {
                400 -> handleBadRequestError(response)
                401 -> handleUnauthorizedError(response)
                403 -> handleForbiddenError(response)
                404 -> handleNotFoundError(response)
                408 -> handleRequestTimeoutError(response)
                500 -> handleInternalServerError(response)
                502 -> handleBadGatewayError(response)
                503 -> handleServiceUnavailableError(response)
                else -> handleGeneralError(response)
            }
        }

        return response
    }

    private fun handleBadRequestError(response: Response) {
        Log.e(tag, "400 Bad Request: ${response.message()}")
    }

    private fun handleUnauthorizedError(response: Response) {
        Log.e(tag, "401 Unauthorized: ${response.message()}")
    }

    private fun handleForbiddenError(response: Response) {
        Log.e(tag, "403 Forbidden: ${response.message()}")
    }

    private fun handleNotFoundError(response: Response) {
        Log.e(tag, "404 Not Found: ${response.message()}")
    }

    private fun handleRequestTimeoutError(response: Response) {
        Log.e(tag, "408 Request Timeout: ${response.message()}")
    }

    private fun handleInternalServerError(response: Response) {
        Log.e(tag, "500 Internal Server Error: ${response.message()}")
    }

    private fun handleBadGatewayError(response: Response) {
        Log.e(tag, "502 Bad Gateway: ${response.message()}")
    }

    private fun handleServiceUnavailableError(response: Response) {
        Log.e(tag, "503 Service Unavailable: ${response.message()}")
    }

    private fun handleGeneralError(response: Response) {
        Log.e(tag, "General HTTP Error: ${response.code()}, ${response.message()}")
    }
}
