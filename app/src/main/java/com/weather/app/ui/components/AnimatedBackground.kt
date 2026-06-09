package com.weather.app.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.unit.IntSize
import com.weather.app.domain.model.WeatherCondition
import com.weather.app.ui.theme.*
import kotlinx.coroutines.delay
import kotlin.math.sin
import kotlin.random.Random

private enum class TimeOfDay { DAWN, DAY, DUSK, NIGHT }

private fun timeOfDay(hour: Int): TimeOfDay = when (hour) {
    in 5..6 -> TimeOfDay.DAWN
    in 7..16 -> TimeOfDay.DAY
    in 17..19 -> TimeOfDay.DUSK
    else -> TimeOfDay.NIGHT
}

private data class BgColors(val top: Color, val bottom: Color)

private fun bgColors(condition: WeatherCondition, tod: TimeOfDay): BgColors = when (condition) {
    WeatherCondition.CLEAR_DAY, WeatherCondition.CLEAR_NIGHT -> when (tod) {
        TimeOfDay.DAWN -> BgColors(DawnTop, DawnBottom)
        TimeOfDay.DAY -> BgColors(ClearDayTop, ClearDayBottom)
        TimeOfDay.DUSK -> BgColors(DuskTop, DuskBottom)
        TimeOfDay.NIGHT -> BgColors(ClearNightTop, ClearNightBottom)
    }
    WeatherCondition.PARTLY_CLOUDY_DAY, WeatherCondition.PARTLY_CLOUDY_NIGHT -> when (tod) {
        TimeOfDay.DAWN -> BgColors(Color(0xFF3D1C6E), Color(0xFFE88B5E))
        TimeOfDay.DAY -> BgColors(PartlyCloudyTop, PartlyCloudyBottom)
        TimeOfDay.DUSK -> BgColors(Color(0xFF6A1B9A), Color(0xFFFF7043))
        TimeOfDay.NIGHT -> BgColors(Color(0xFF141428), Color(0xFF2A2A5E))
    }
    WeatherCondition.OVERCAST -> BgColors(OvercastTop, OvercastBottom)
    WeatherCondition.FOG -> BgColors(FogTop, FogBottom)
    WeatherCondition.DRIZZLE, WeatherCondition.RAIN,
    WeatherCondition.FREEZING_RAIN, WeatherCondition.RAIN_SHOWERS -> BgColors(RainTop, RainBottom)
    WeatherCondition.SNOW, WeatherCondition.SNOW_SHOWERS -> BgColors(SnowTop, SnowBottom)
    WeatherCondition.THUNDERSTORM -> BgColors(StormTop, StormBottom)
    WeatherCondition.UNKNOWN -> BgColors(OvercastTop, OvercastBottom)
}

private enum class ParticleType { NONE, RAIN, SNOW, CLOUD }

private fun particleType(condition: WeatherCondition): ParticleType = when (condition) {
    WeatherCondition.DRIZZLE, WeatherCondition.RAIN, WeatherCondition.FREEZING_RAIN,
    WeatherCondition.RAIN_SHOWERS, WeatherCondition.THUNDERSTORM -> ParticleType.RAIN
    WeatherCondition.SNOW, WeatherCondition.SNOW_SHOWERS -> ParticleType.SNOW
    WeatherCondition.PARTLY_CLOUDY_DAY, WeatherCondition.PARTLY_CLOUDY_NIGHT,
    WeatherCondition.OVERCAST -> ParticleType.CLOUD
    else -> ParticleType.NONE
}

private data class Particle(val x: Float, val y: Float, val speed: Float, val size: Float, val alpha: Float, val drift: Float = 0f)

private fun initParticles(type: ParticleType, w: Float, h: Float, rng: Random): List<Particle> = when (type) {
    ParticleType.RAIN -> (0 until 80).map {
        Particle(rng.nextFloat() * w, rng.nextFloat() * h, rng.nextFloat() * 8f + 8f, rng.nextFloat() * 1.5f + 0.8f, rng.nextFloat() * 0.35f + 0.3f, rng.nextFloat() * 0.6f - 0.3f)
    }
    ParticleType.SNOW -> (0 until 55).map {
        Particle(rng.nextFloat() * w, rng.nextFloat() * h, rng.nextFloat() * 1.2f + 0.4f, rng.nextFloat() * 5f + 2f, rng.nextFloat() * 0.4f + 0.5f)
    }
    ParticleType.CLOUD -> (0 until 6).map { i ->
        Particle(rng.nextFloat() * w, h * 0.04f + i * h * 0.05f, rng.nextFloat() * 0.25f + 0.08f, rng.nextFloat() * 35f + 55f, rng.nextFloat() * 0.1f + 0.06f)
    }
    ParticleType.NONE -> emptyList()
}

private fun stepParticles(list: List<Particle>, type: ParticleType, w: Float, h: Float, rng: Random): List<Particle> =
    list.map { p ->
        when (type) {
            ParticleType.RAIN -> {
                val ny = p.y + p.speed
                val nx = p.x + p.drift
                if (ny > h) p.copy(y = -20f, x = rng.nextFloat() * w) else p.copy(y = ny, x = nx)
            }
            ParticleType.SNOW -> {
                val ny = p.y + p.speed
                val nx = p.x + sin(ny * 0.02f) * 0.7f
                if (ny > h) p.copy(y = -10f, x = rng.nextFloat() * w) else p.copy(y = ny, x = nx)
            }
            ParticleType.CLOUD -> {
                val nx = p.x + p.speed
                if (nx > w + 160f) p.copy(x = -160f) else p.copy(x = nx)
            }
            ParticleType.NONE -> p
        }
    }

private fun DrawScope.drawRainDrop(p: Particle) {
    drawLine(RainParticle.copy(alpha = p.alpha), Offset(p.x, p.y), Offset(p.x + p.drift * 3f, p.y + p.size * 7f), p.size)
}

private fun DrawScope.drawSnowflake(p: Particle) {
    drawCircle(SnowParticle.copy(alpha = p.alpha), p.size, Offset(p.x, p.y))
}

private fun DrawScope.drawCloud(p: Particle) {
    val a = CloudParticle.copy(alpha = p.alpha)
    drawCircle(a, p.size, Offset(p.x, p.y))
    drawCircle(a, p.size * 0.72f, Offset(p.x - p.size * 0.85f, p.y + p.size * 0.22f))
    drawCircle(a, p.size * 0.78f, Offset(p.x + p.size * 0.85f, p.y + p.size * 0.22f))
}

@Composable
fun AnimatedBackground(
    condition: WeatherCondition,
    hourOfDay: Int,
    modifier: Modifier = Modifier
) {
    val tod = remember(hourOfDay) { timeOfDay(hourOfDay) }
    val colors = remember(condition, tod) { bgColors(condition, tod) }
    val pType = remember(condition) { particleType(condition) }
    val rng = remember { Random(System.currentTimeMillis()) }

    val isNight = tod == TimeOfDay.NIGHT
    val showStars = isNight && condition in listOf(WeatherCondition.CLEAR_NIGHT, WeatherCondition.PARTLY_CLOUDY_NIGHT)
    val starPositions = remember {
        val sr = Random(42)
        (0 until 65).map { Pair(sr.nextFloat(), sr.nextFloat() * 0.55f) }
    }

    var size by remember { mutableStateOf(IntSize.Zero) }
    var particles by remember(pType) { mutableStateOf(emptyList<Particle>()) }

    LaunchedEffect(pType, size) {
        if (size.width <= 0) return@LaunchedEffect
        particles = initParticles(pType, size.width.toFloat(), size.height.toFloat(), rng)
    }

    LaunchedEffect(pType, size) {
        if (pType == ParticleType.NONE || size.width <= 0) return@LaunchedEffect
        while (true) {
            delay(33L)
            particles = stepParticles(particles, pType, size.width.toFloat(), size.height.toFloat(), rng)
        }
    }

    Canvas(
        modifier = modifier
            .fillMaxSize()
            .then(Modifier) // onSizeChanged applied via BoxWithConstraints in parent
    ) {
        if (this.size.width.toInt() != size.width || this.size.height.toInt() != size.height) {
            // Size is read-only in draw scope; parent uses onSizeChanged to set it
        }

        drawRect(brush = Brush.verticalGradient(listOf(colors.top, colors.bottom)))

        if (showStars) {
            starPositions.forEach { (rx, ry) ->
                drawCircle(Color.White.copy(alpha = 0.65f), radius = 1.4f, center = Offset(rx * this.size.width, ry * this.size.height))
            }
        }

        when (pType) {
            ParticleType.RAIN -> particles.forEach { drawRainDrop(it) }
            ParticleType.SNOW -> particles.forEach { drawSnowflake(it) }
            ParticleType.CLOUD -> particles.forEach { drawCloud(it) }
            ParticleType.NONE -> {}
        }
    }
}

@Composable
fun AnimatedBackgroundWithSize(
    condition: WeatherCondition,
    hourOfDay: Int,
    modifier: Modifier = Modifier
) {
    val tod = remember(hourOfDay) { timeOfDay(hourOfDay) }
    val colors = remember(condition, tod) { bgColors(condition, tod) }
    val pType = remember(condition) { particleType(condition) }
    val rng = remember { Random(System.currentTimeMillis()) }

    val showStars = tod == TimeOfDay.NIGHT &&
            condition in listOf(WeatherCondition.CLEAR_NIGHT, WeatherCondition.PARTLY_CLOUDY_NIGHT)
    val starPositions = remember {
        val sr = Random(42)
        (0 until 65).map { Pair(sr.nextFloat(), sr.nextFloat() * 0.55f) }
    }

    var canvasW by remember { mutableFloatStateOf(0f) }
    var canvasH by remember { mutableFloatStateOf(0f) }
    var particles by remember(pType) { mutableStateOf(emptyList<Particle>()) }

    LaunchedEffect(pType, canvasW, canvasH) {
        if (canvasW <= 0f) return@LaunchedEffect
        particles = initParticles(pType, canvasW, canvasH, rng)
    }

    LaunchedEffect(pType, canvasW, canvasH) {
        if (pType == ParticleType.NONE || canvasW <= 0f) return@LaunchedEffect
        while (true) {
            delay(33L)
            particles = stepParticles(particles, pType, canvasW, canvasH, rng)
        }
    }

    Canvas(modifier = modifier.fillMaxSize()) {
        canvasW = this.size.width
        canvasH = this.size.height

        drawRect(brush = Brush.verticalGradient(listOf(colors.top, colors.bottom)))

        if (showStars) {
            starPositions.forEach { (rx, ry) ->
                drawCircle(Color.White.copy(alpha = 0.65f), radius = 1.4f, center = Offset(rx * canvasW, ry * canvasH))
            }
        }

        when (pType) {
            ParticleType.RAIN -> particles.forEach { drawRainDrop(it) }
            ParticleType.SNOW -> particles.forEach { drawSnowflake(it) }
            ParticleType.CLOUD -> particles.forEach { drawCloud(it) }
            ParticleType.NONE -> {}
        }
    }
}
