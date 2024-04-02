package com.example.rublerate.data

/**
 * Data class representing currency information, including ID, numerical code, character code,
 * nominal value, name, current value, and previous value.
 */
data class Currency(
    val id: String,
    val numCode: String,
    val charCode: String,
    val nominal: Double,
    val name: String,
    val value: Double,
    val previous: Double
)
