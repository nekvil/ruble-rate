package com.example.rublerate.util

/**
 * Object containing constant values used in the application.
 */
object Constants {
    // Base URL for the currency API
    const val BASE_URL = "https://www.cbr-xml-daily.ru/"

    // Format for displaying currency values with two decimal places
    const val VALUE_FORMAT = "%.2f"

    // Format for displaying currency differences with two decimal places
    const val DIFFERENCE_FORMAT = "%s%.2f"

    // Date format for parsing API response dates
    const val API_DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ssXXX"

    // Date format for displaying dates in the UI
    const val DISPLAY_DATE_FORMAT = "dd.MM.yyyy, HH:mm:ss"

    // Update interval for fetching currency data from the API (in milliseconds)
    const val UPDATE_INTERVAL_MS = 30 * 1000
}
