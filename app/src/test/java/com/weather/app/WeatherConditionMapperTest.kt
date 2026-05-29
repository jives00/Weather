package com.weather.app

import com.weather.app.domain.model.WeatherCondition
import org.junit.Assert.assertEquals
import org.junit.Test

class WeatherConditionMapperTest {

    @Test
    fun `clear sky code 0 maps to CLEAR_DAY when is_day`() {
        assertEquals(WeatherCondition.CLEAR_DAY, WeatherCondition.fromWmoCode(0, isDay = true))
    }

    @Test
    fun `clear sky code 0 maps to CLEAR_NIGHT when not is_day`() {
        assertEquals(WeatherCondition.CLEAR_NIGHT, WeatherCondition.fromWmoCode(0, isDay = false))
    }

    @Test
    fun `overcast code 3 maps to OVERCAST regardless of day`() {
        assertEquals(WeatherCondition.OVERCAST, WeatherCondition.fromWmoCode(3, isDay = true))
        assertEquals(WeatherCondition.OVERCAST, WeatherCondition.fromWmoCode(3, isDay = false))
    }

    @Test
    fun `rain codes map to RAIN`() {
        for (code in listOf(61, 63, 65)) {
            assertEquals("code $code", WeatherCondition.RAIN, WeatherCondition.fromWmoCode(code, isDay = true))
        }
    }

    @Test
    fun `snow codes map to SNOW`() {
        for (code in listOf(71, 73, 75, 77)) {
            assertEquals("code $code", WeatherCondition.SNOW, WeatherCondition.fromWmoCode(code, isDay = true))
        }
    }

    @Test
    fun `thunderstorm codes map to THUNDERSTORM`() {
        for (code in listOf(95, 96, 99)) {
            assertEquals("code $code", WeatherCondition.THUNDERSTORM, WeatherCondition.fromWmoCode(code, isDay = true))
        }
    }

    @Test
    fun `drizzle codes map to DRIZZLE`() {
        for (code in listOf(51, 53, 55)) {
            assertEquals("code $code", WeatherCondition.DRIZZLE, WeatherCondition.fromWmoCode(code, isDay = true))
        }
    }

    @Test
    fun `fog codes map to FOG`() {
        for (code in listOf(45, 48)) {
            assertEquals("code $code", WeatherCondition.FOG, WeatherCondition.fromWmoCode(code, isDay = true))
        }
    }

    @Test
    fun `unknown code returns UNKNOWN`() {
        assertEquals(WeatherCondition.UNKNOWN, WeatherCondition.fromWmoCode(999, isDay = true))
    }

    @Test
    fun `description from known codes returns non-empty string`() {
        val knownCodes = listOf(0, 1, 2, 3, 45, 51, 61, 71, 80, 95)
        knownCodes.forEach { code ->
            val desc = WeatherCondition.descriptionFromWmoCode(code)
            assert(desc.isNotEmpty()) { "Empty description for code $code" }
            assert(desc != "Unknown") { "Code $code returned 'Unknown'" }
        }
    }
}
