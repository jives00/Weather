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

    private val precipConditions = setOf(
        WeatherCondition.RAIN, WeatherCondition.RAIN_SHOWERS, WeatherCondition.DRIZZLE,
        WeatherCondition.FREEZING_RAIN, WeatherCondition.SNOW, WeatherCondition.SNOW_SHOWERS,
        WeatherCondition.THUNDERSTORM
    )

    // Open-Meteo's weather_code and precipitation_probability come from different model outputs
    // and frequently disagree. Downgrade any precipitation icon to partly cloudy when the
    // probability is too low to be meaningful.
    private fun resolveCondition(raw: WeatherCondition, precipProb: Int?, isDay: Boolean): WeatherCondition {
        if (raw !in precipConditions) return raw
        if ((precipProb ?: 0) >= 15) return raw
        return if (isDay) WeatherCondition.PARTLY_CLOUDY_DAY else WeatherCondition.PARTLY_CLOUDY_NIGHT
    }

    fun mapToForecast(
        response: WeatherApiResponse,
        location: WeatherLocation,
        units: Units,
        aqi: Int?
    ): WeatherForecast {
        val daily = mapDaily(response)
        val todayUvIndex = daily.firstOrNull()?.uvIndexMax ?: 0.0
        val todayPrecipProb = response.daily.precipitationProbabilityMax.getOrNull(0)
        val current = mapCurrent(response, todayUvIndex, todayPrecipProb)
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

    private fun mapCurrent(response: WeatherApiResponse, uvIndex: Double, todayPrecipProb: Int?): CurrentWeather {
        val c = response.current
        val isDay = c.isDay == 1
        val rawCondition = WeatherCondition.fromWmoCode(c.weatherCode, isDay)
        val condition = resolveCondition(rawCondition, todayPrecipProb, isDay)
        val description = if (condition != rawCondition) "Chance of ${rawCondition.name.lowercase().replace('_', ' ')}" else WeatherCondition.descriptionFromWmoCode(c.weatherCode)
        return CurrentWeather(
            temperature = c.temperature,
            feelsLike = c.apparentTemperature,
            humidity = c.relativeHumidity,
            condition = condition,
            conditionDescription = description,
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
            val precipProb = h.precipitationProbability.getOrNull(i) ?: 0
            val rawCondition = WeatherCondition.fromWmoCode(h.weatherCode[i], isDay)
            val condition = resolveCondition(rawCondition, precipProb, isDay)
            HourlyWeather(
                time = parseHourlyTime(h.time[i]),
                temperature = h.temperature[i],
                precipitationProbability = precipProb,
                precipitation = h.precipitation.getOrNull(i) ?: 0.0,
                condition = condition,
                wmoCode = h.weatherCode[i],
                isDay = isDay
            )
        }
    }

    private fun mapDaily(response: WeatherApiResponse): List<DailyWeather> {
        val d = response.daily
        return d.time.indices.map { i ->
            val precipProb = d.precipitationProbabilityMax.getOrNull(i)
            val rawCondition = WeatherCondition.fromWmoCode(d.weatherCode[i], true)
            val condition = resolveCondition(rawCondition, precipProb, isDay = true)
            DailyWeather(
                time = parseDailyTime(d.time[i]),
                tempMax = d.temperatureMax[i],
                tempMin = d.temperatureMin[i],
                condition = condition,
                wmoCode = d.weatherCode[i],
                sunrise = parseIsoTime(d.sunrise[i]),
                sunset = parseIsoTime(d.sunset[i]),
                precipitationProbability = precipProb,
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
