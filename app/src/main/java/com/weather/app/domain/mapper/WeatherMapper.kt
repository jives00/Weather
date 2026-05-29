package com.weather.app.domain.mapper

import com.weather.app.data.api.WeatherApiResponse
import com.weather.app.domain.model.CurrentWeather
import com.weather.app.domain.model.DailyWeather
import com.weather.app.domain.model.HourlyWeather
import com.weather.app.domain.model.Units
import com.weather.app.domain.model.WeatherCondition
import com.weather.app.domain.model.WeatherForecast
import com.weather.app.domain.model.WeatherLocation
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

object WeatherMapper {
    private val hourlyFmt = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm")
    private val dailyFmt = DateTimeFormatter.ofPattern("yyyy-MM-dd")
    private val isoFmt = DateTimeFormatter.ISO_DATE_TIME

    fun mapToForecast(
        response: WeatherApiResponse,
        location: WeatherLocation,
        units: Units,
        aqi: Int?
    ): WeatherForecast {
        val daily = mapDaily(response)
        val todayUvIndex = daily.firstOrNull()?.uvIndexMax ?: 0.0
        val current = mapCurrent(response, todayUvIndex)
        val hourly = mapHourly(response)

        return WeatherForecast(
            location = location,
            current = current,
            hourly = hourly,
            daily = daily,
            highToday = daily.firstOrNull()?.tempMax ?: current.temperature,
            lowToday = daily.firstOrNull()?.tempMin ?: current.temperature,
            aqi = aqi,
            units = units
        )
    }

    private fun mapCurrent(response: WeatherApiResponse, uvIndex: Double): CurrentWeather {
        val c = response.current
        val isDay = c.isDay == 1
        return CurrentWeather(
            temperature = c.temperature,
            feelsLike = c.apparentTemperature,
            humidity = c.relativeHumidity,
            condition = WeatherCondition.fromWmoCode(c.weatherCode, isDay),
            conditionDescription = WeatherCondition.descriptionFromWmoCode(c.weatherCode),
            windSpeed = c.windSpeed,
            windDirection = c.windDirection,
            uvIndex = uvIndex,
            visibility = c.visibility,
            pressure = c.surfacePressure,
            isDay = isDay,
            wmoCode = c.weatherCode
        )
    }

    private fun mapHourly(response: WeatherApiResponse): List<HourlyWeather> {
        val h = response.hourly
        return h.time.indices.map { i ->
            val isDay = h.isDay.getOrNull(i) == 1
            HourlyWeather(
                time = parseHourlyTime(h.time[i]),
                temperature = h.temperature[i],
                precipitationProbability = h.precipitationProbability.getOrNull(i) ?: 0,
                condition = WeatherCondition.fromWmoCode(h.weatherCode[i], isDay),
                wmoCode = h.weatherCode[i],
                isDay = isDay
            )
        }
    }

    private fun mapDaily(response: WeatherApiResponse): List<DailyWeather> {
        val d = response.daily
        return d.time.indices.map { i ->
            DailyWeather(
                time = parseDailyTime(d.time[i]),
                tempMax = d.temperatureMax[i],
                tempMin = d.temperatureMin[i],
                condition = WeatherCondition.fromWmoCode(d.weatherCode[i], true),
                wmoCode = d.weatherCode[i],
                sunrise = parseIsoTime(d.sunrise[i]),
                sunset = parseIsoTime(d.sunset[i]),
                precipitationProbability = d.precipitationProbabilityMax.getOrNull(i) ?: 0,
                precipitationSum = d.precipitationSum[i],
                windSpeedMax = d.windSpeedMax[i],
                uvIndexMax = d.uvIndexMax[i]
            )
        }
    }

    private fun parseHourlyTime(s: String): Long =
        LocalDateTime.parse(s, hourlyFmt).atZone(ZoneId.systemDefault()).toEpochSecond()

    private fun parseDailyTime(s: String): Long =
        LocalDate.parse(s, dailyFmt).atStartOfDay(ZoneId.systemDefault()).toEpochSecond()

    private fun parseIsoTime(s: String): Long = try {
        LocalDateTime.parse(s, isoFmt).atZone(ZoneId.systemDefault()).toEpochSecond()
    } catch (e: Exception) {
        parseDailyTime(s.substringBefore("T"))
    }
}
