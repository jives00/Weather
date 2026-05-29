package com.weather.app.domain.model

enum class Units(val label: String) {
    IMPERIAL("Imperial (°F)"),
    METRIC("Metric (°C)");

    val temperatureSymbol: String get() = if (this == IMPERIAL) "°F" else "°C"
    val windSpeedUnit: String get() = if (this == IMPERIAL) "mph" else "km/h"
    val precipitationUnit: String get() = if (this == IMPERIAL) "in" else "mm"
    val visibilityUnit: String get() = if (this == IMPERIAL) "mi" else "km"
    val pressureUnit: String get() = "hPa"

    val apiTemperatureUnit: String get() = if (this == IMPERIAL) "fahrenheit" else "celsius"
    val apiWindSpeedUnit: String get() = if (this == IMPERIAL) "mph" else "kmh"
    val apiPrecipitationUnit: String get() = if (this == IMPERIAL) "inch" else "mm"
}
