package com.weather.app.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import com.weather.app.domain.model.WeatherCondition
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun WeatherScene(
    condition: WeatherCondition,
    isDay: Boolean,
    modifier: Modifier = Modifier
) {
    Canvas(modifier = modifier.fillMaxSize()) {
        val w = size.width
        val h = size.height

        when (condition) {
            WeatherCondition.CLEAR_DAY ->
                drawSun(w * 0.72f, h * 0.42f, w * 0.12f)
            WeatherCondition.CLEAR_NIGHT ->
                drawMoon(w * 0.72f, h * 0.38f, w * 0.09f)
            WeatherCondition.PARTLY_CLOUDY_DAY -> {
                drawSun(w * 0.68f, h * 0.32f, w * 0.10f)
                drawAtmosphericCloud(w * 0.52f, h * 0.48f, w * 0.30f, alpha = 0.18f)
            }
            WeatherCondition.PARTLY_CLOUDY_NIGHT -> {
                drawMoon(w * 0.70f, h * 0.32f, w * 0.08f)
                drawAtmosphericCloud(w * 0.50f, h * 0.46f, w * 0.28f, alpha = 0.12f)
            }
            WeatherCondition.OVERCAST -> {
                drawAtmosphericCloud(w * 0.22f, h * 0.28f, w * 0.36f, alpha = 0.22f)
                drawAtmosphericCloud(w * 0.68f, h * 0.20f, w * 0.28f, alpha = 0.16f)
            }
            WeatherCondition.FOG ->
                drawFogVeil(w, h)
            WeatherCondition.RAIN, WeatherCondition.DRIZZLE, WeatherCondition.RAIN_SHOWERS,
            WeatherCondition.FREEZING_RAIN -> {
                drawAtmosphericCloud(w * 0.30f, h * 0.22f, w * 0.36f, alpha = 0.28f, color = Color(0xFF607D8B))
                drawAtmosphericCloud(w * 0.72f, h * 0.16f, w * 0.26f, alpha = 0.20f, color = Color(0xFF607D8B))
            }
            WeatherCondition.SNOW, WeatherCondition.SNOW_SHOWERS ->
                drawAtmosphericCloud(w * 0.38f, h * 0.22f, w * 0.34f, alpha = 0.22f, color = Color(0xFFB0BEC5))
            WeatherCondition.THUNDERSTORM -> {
                drawAtmosphericCloud(w * 0.35f, h * 0.25f, w * 0.42f, alpha = 0.35f, color = Color(0xFF455A64))
                drawLightningBolt(w * 0.50f, h * 0.48f, h * 0.22f)
            }
            WeatherCondition.UNKNOWN ->
                drawAtmosphericCloud(w * 0.45f, h * 0.30f, w * 0.28f, alpha = 0.14f)
        }

        // Subtle bottom vignette to ground the scene
        drawRect(
            brush = Brush.verticalGradient(
                colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.25f)),
                startY = h * 0.55f,
                endY = h
            ),
            topLeft = Offset(0f, h * 0.55f),
            size = Size(w, h * 0.45f)
        )
    }
}

private fun DrawScope.drawSun(cx: Float, cy: Float, radius: Float) {
    // Outermost atmospheric diffusion — very soft, large
    drawCircle(Color(0x08FFF9C4), radius * 5.0f, Offset(cx, cy))
    drawCircle(Color(0x12FFE082), radius * 3.5f, Offset(cx, cy))
    drawCircle(Color(0x20FFD54F), radius * 2.4f, Offset(cx, cy))
    drawCircle(Color(0x38FFD54F), radius * 1.7f, Offset(cx, cy))
    // Corona
    drawCircle(Color(0x70FFE57F), radius * 1.25f, Offset(cx, cy))
    // Sun disc — slightly warm white at center, golden at edge
    drawCircle(
        brush = Brush.radialGradient(
            colors = listOf(Color(0xFFFFFDE7), Color(0xFFFFD54F)),
            center = Offset(cx, cy),
            radius = radius
        ),
        radius = radius,
        center = Offset(cx, cy)
    )
}

private fun DrawScope.drawMoon(cx: Float, cy: Float, radius: Float) {
    // Soft atmospheric glow
    drawCircle(Color(0x10E3F2FD), radius * 3.0f, Offset(cx, cy))
    drawCircle(Color(0x18BBDEFB), radius * 2.0f, Offset(cx, cy))
    drawCircle(Color(0x30E3F2FD), radius * 1.4f, Offset(cx, cy))
    // Moon disc
    drawCircle(Color(0xFFECEFF1), radius, Offset(cx, cy))
    // Crescent shadow — slightly offset dark circle to create crescent shape
    drawCircle(Color(0xFF1A1A4E), radius * 0.85f, Offset(cx + radius * 0.38f, cy - radius * 0.16f))
}

// Soft, wispy cloud using large translucent circles — no hard edges
private fun DrawScope.drawAtmosphericCloud(
    cx: Float, cy: Float, baseR: Float,
    alpha: Float = 0.18f,
    color: Color = Color.White
) {
    val c = color.copy(alpha = alpha)
    val c2 = color.copy(alpha = alpha * 0.6f)
    val c3 = color.copy(alpha = alpha * 0.35f)
    // Wispy outer fringe
    drawCircle(c3, baseR * 1.5f, Offset(cx, cy))
    drawCircle(c3, baseR * 1.2f, Offset(cx + baseR * 0.55f, cy + baseR * 0.12f))
    drawCircle(c3, baseR * 1.1f, Offset(cx - baseR * 0.50f, cy + baseR * 0.18f))
    // Mid layer
    drawCircle(c2, baseR * 1.0f, Offset(cx, cy - baseR * 0.05f))
    drawCircle(c2, baseR * 0.85f, Offset(cx + baseR * 0.60f, cy + baseR * 0.08f))
    drawCircle(c2, baseR * 0.80f, Offset(cx - baseR * 0.52f, cy + baseR * 0.12f))
    // Dense core
    drawCircle(c, baseR * 0.65f, Offset(cx, cy))
    drawCircle(c, baseR * 0.55f, Offset(cx + baseR * 0.45f, cy + baseR * 0.05f))
    drawCircle(c, baseR * 0.50f, Offset(cx - baseR * 0.38f, cy + baseR * 0.08f))
}

private fun DrawScope.drawFogVeil(w: Float, h: Float) {
    // Horizontal fog bands with soft edges
    val bands = listOf(0.20f to 0.06f, 0.36f to 0.05f, 0.50f to 0.07f, 0.64f to 0.05f)
    bands.forEach { (yFrac, heightFrac) ->
        val y = h * yFrac
        val bh = h * heightFrac
        drawRect(
            brush = Brush.verticalGradient(
                colors = listOf(Color.Transparent, Color(0x22FFFFFF), Color.Transparent),
                startY = y,
                endY = y + bh
            ),
            topLeft = Offset(0f, y),
            size = Size(w, bh)
        )
    }
}

private fun DrawScope.drawLightningBolt(cx: Float, topY: Float, length: Float) {
    val boltPath = Path().apply {
        moveTo(cx + length * 0.12f, topY)
        lineTo(cx - length * 0.06f, topY + length * 0.42f)
        lineTo(cx + length * 0.08f, topY + length * 0.42f)
        lineTo(cx - length * 0.12f, topY + length)
        lineTo(cx + length * 0.22f, topY + length * 0.52f)
        lineTo(cx + length * 0.06f, topY + length * 0.52f)
        close()
    }
    // Glow
    drawCircle(Color(0x30FFD54F), length * 0.35f, Offset(cx, topY + length * 0.5f))
    drawPath(boltPath, Color(0xCCFFD54F))
}
