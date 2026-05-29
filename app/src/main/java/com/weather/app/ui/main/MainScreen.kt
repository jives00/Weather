package com.weather.app.ui.main

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.weather.app.domain.model.WeatherForecast
import com.weather.app.domain.model.WeatherLocation
import com.weather.app.ui.components.AnimatedBackgroundWithSize
import com.weather.app.ui.components.DailyForecastList
import com.weather.app.ui.components.DetailCardsGrid
import com.weather.app.ui.components.HourlyForecastRow
import com.weather.app.ui.components.WeatherIcon
import com.weather.app.ui.theme.OnWeatherSurface
import com.weather.app.ui.theme.OnWeatherSurfaceDim
import com.weather.app.ui.theme.WeatherCardBackground
import java.util.Calendar

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun MainScreen(
    viewModel: MainViewModel,
    onNavigateToSettings: () -> Unit
) {
    val locations by viewModel.locations.collectAsStateWithLifecycle()
    val forecasts by viewModel.forecasts.collectAsStateWithLifecycle()
    val units by viewModel.units.collectAsStateWithLifecycle()

    val locationPermission = rememberPermissionState(android.Manifest.permission.ACCESS_COARSE_LOCATION) { granted ->
        if (granted) viewModel.onLocationPermissionGranted()
    }

    LaunchedEffect(locationPermission.status.isGranted) {
        if (locationPermission.status.isGranted) {
            viewModel.onLocationPermissionGranted()
        } else {
            locationPermission.launchPermissionRequest()
        }
    }

    var showAddLocationDialog by remember { mutableStateOf(false) }

    val pageCount = if (locations.isEmpty()) 1 else locations.size
    val pagerState = rememberPagerState(pageCount = { pageCount })

    Box(modifier = Modifier.fillMaxSize()) {
        // Background for the current page
        val currentLocation = locations.getOrNull(pagerState.currentPage)
        val currentForecast = currentLocation?.let { forecasts[it.id] as? WeatherUiState.Success }?.forecast
        val currentCondition = currentForecast?.current?.condition
        val hourOfDay = remember { Calendar.getInstance().get(Calendar.HOUR_OF_DAY) }

        if (currentCondition != null) {
            AnimatedBackgroundWithSize(condition = currentCondition, hourOfDay = hourOfDay, modifier = Modifier.fillMaxSize())
        } else {
            Box(modifier = Modifier.fillMaxSize().background(Color(0xFF1565C0)))
        }

        // Main pager content
        HorizontalPager(state = pagerState, modifier = Modifier.fillMaxSize()) { page ->
            val location = locations.getOrNull(page)
            val state = location?.let { forecasts[it.id] }

            when {
                location == null -> EmptyState(onAddLocation = { showAddLocationDialog = true })
                state == null || state is WeatherUiState.Loading -> LoadingPage()
                state is WeatherUiState.Error -> ErrorPage(message = state.message, onRetry = { viewModel.fetchForecast(location) })
                state is WeatherUiState.Success -> WeatherPage(forecast = state.forecast)
            }
        }

        // Top bar overlay
        TopBar(
            locationName = locations.getOrNull(pagerState.currentPage)?.name ?: "",
            isGps = locations.getOrNull(pagerState.currentPage)?.isCurrentLocation == true,
            onSettings = onNavigateToSettings,
            onAdd = { showAddLocationDialog = true },
            modifier = Modifier.align(Alignment.TopCenter).statusBarsPadding()
        )

        // Page dots indicator
        if (locations.size > 1) {
            PageIndicator(
                pageCount = locations.size,
                currentPage = pagerState.currentPage,
                modifier = Modifier.align(Alignment.BottomCenter).navigationBarsPadding().padding(bottom = 16.dp)
            )
        }
    }

    if (showAddLocationDialog) {
        AddLocationDialog(
            onDismiss = { showAddLocationDialog = false },
            onSearch = { query -> viewModel.searchLocations(query) },
            onSelect = { loc ->
                viewModel.addLocation(loc)
                showAddLocationDialog = false
            }
        )
    }
}

@Composable
private fun WeatherPage(forecast: WeatherForecast) {
    val hourOfDay = remember { Calendar.getInstance().get(Calendar.HOUR_OF_DAY) }
    val nowEpoch = System.currentTimeMillis() / 1000
    val upcomingHourly = remember(forecast) { forecast.hourly.filter { it.time >= nowEpoch }.take(24) }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 32.dp)
    ) {
        // Hero section
        item {
            HeroSection(
                forecast = forecast,
                modifier = Modifier
                    .fillMaxWidth()
                    .fillParentMaxHeight(0.62f)
            )
        }

        // Hourly forecast card
        item {
            SectionCard(modifier = Modifier.padding(horizontal = 16.dp).padding(bottom = 12.dp)) {
                HourlyForecastRow(hourly = upcomingHourly, units = forecast.units, modifier = Modifier.fillMaxWidth())
            }
        }

        // Daily forecast card
        item {
            SectionCard(modifier = Modifier.padding(horizontal = 16.dp).padding(bottom = 12.dp)) {
                DailyForecastList(daily = forecast.daily, units = forecast.units)
            }
        }

        // Detail cards
        item {
            DetailCardsGrid(
                forecast = forecast,
                modifier = Modifier.padding(horizontal = 16.dp).padding(bottom = 12.dp)
            )
        }
    }
}

@Composable
private fun HeroSection(forecast: WeatherForecast, modifier: Modifier = Modifier) {
    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(top = 80.dp) // leave room for top bar
        ) {
            Text(
                text = "${forecast.current.temperature.toInt()}°",
                style = MaterialTheme.typography.displayLarge,
                color = OnWeatherSurface
            )
            Text(
                text = forecast.current.conditionDescription,
                style = MaterialTheme.typography.headlineMedium,
                color = OnWeatherSurfaceDim,
                textAlign = TextAlign.Center
            )
            Spacer(Modifier.height(8.dp))
            Text(
                text = "H:${forecast.highToday.toInt()}° L:${forecast.lowToday.toInt()}°",
                style = MaterialTheme.typography.titleMedium,
                color = OnWeatherSurfaceDim
            )
        }
    }
}

@Composable
private fun SectionCard(modifier: Modifier = Modifier, content: @Composable () -> Unit) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(WeatherCardBackground)
            .padding(vertical = 8.dp)
    ) {
        content()
    }
}

@Composable
private fun TopBar(
    locationName: String,
    isGps: Boolean,
    onSettings: () -> Unit,
    onAdd: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth().padding(horizontal = 8.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onAdd) {
            Icon(Icons.Filled.Add, contentDescription = "Add location", tint = OnWeatherSurface)
        }
        Row(
            modifier = Modifier.weight(1f),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (isGps) {
                Icon(Icons.Filled.MyLocation, contentDescription = null, tint = OnWeatherSurface, modifier = Modifier.size(14.dp))
                Spacer(Modifier.width(4.dp))
            }
            Text(text = locationName, style = MaterialTheme.typography.titleMedium, color = OnWeatherSurface)
        }
        IconButton(onClick = onSettings) {
            Icon(Icons.Filled.Settings, contentDescription = "Settings", tint = OnWeatherSurface)
        }
    }
}

@Composable
private fun PageIndicator(pageCount: Int, currentPage: Int, modifier: Modifier = Modifier) {
    Row(modifier = modifier, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
        repeat(pageCount) { i ->
            Box(
                modifier = Modifier
                    .size(if (i == currentPage) 8.dp else 6.dp)
                    .clip(CircleShape)
                    .background(if (i == currentPage) OnWeatherSurface else OnWeatherSurface.copy(alpha = 0.4f))
            )
        }
    }
}

@Composable
private fun LoadingPage() {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        CircularProgressIndicator(color = OnWeatherSurface)
    }
}

@Composable
private fun ErrorPage(message: String, onRetry: () -> Unit) {
    Column(Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
        Text(message, color = OnWeatherSurface, textAlign = TextAlign.Center, modifier = Modifier.padding(16.dp))
        Button(onClick = onRetry) { Text("Retry") }
    }
}

@Composable
private fun EmptyState(onAddLocation: () -> Unit) {
    Column(Modifier.fillMaxSize().padding(top = 120.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
        Text("No location set", color = OnWeatherSurface, style = MaterialTheme.typography.headlineMedium)
        Spacer(Modifier.height(16.dp))
        Button(onClick = onAddLocation) { Text("Add Location") }
    }
}

@Composable
private fun AddLocationDialog(
    onDismiss: () -> Unit,
    onSearch: (String) -> List<WeatherLocation>,
    onSelect: (WeatherLocation) -> Unit
) {
    var query by remember { mutableStateOf("") }
    var results by remember { mutableStateOf(emptyList<WeatherLocation>()) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add Location") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = query,
                    onValueChange = { query = it },
                    label = { Text("City name") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Button(
                    onClick = { results = onSearch(query) },
                    modifier = Modifier.fillMaxWidth()
                ) { Text("Search") }
                results.forEach { loc ->
                    TextButton(onClick = { onSelect(loc) }, modifier = Modifier.fillMaxWidth()) {
                        Text(loc.name)
                    }
                }
            }
        },
        confirmButton = {},
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } }
    )
}
