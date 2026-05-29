package com.weather.app.domain.model

import java.util.UUID

data class WeatherLocation(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val latitude: Double,
    val longitude: Double,
    val isCurrentLocation: Boolean = false
)
