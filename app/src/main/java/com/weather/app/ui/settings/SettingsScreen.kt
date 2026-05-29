package com.weather.app.ui.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.weather.app.domain.model.Units
import com.weather.app.domain.model.WeatherLocation
import com.weather.app.ui.theme.OnWeatherSurface
import com.weather.app.ui.theme.OnWeatherSurfaceDim
import com.weather.app.ui.theme.WeatherCardBackground

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel,
    onBack: () -> Unit
) {
    val units by viewModel.units.collectAsStateWithLifecycle()
    val savedLocations by viewModel.savedLocations.collectAsStateWithLifecycle()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Scaffold(
            containerColor = MaterialTheme.colorScheme.background,
            topBar = {
                TopAppBar(
                    title = { Text("Settings", color = OnWeatherSurface) },
                    navigationIcon = {
                        IconButton(onClick = onBack) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = OnWeatherSurface)
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
                )
            }
        ) { padding ->
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(padding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                    UnitsSection(currentUnits = units, onUnitsChange = viewModel::setUnits)
                }
                if (savedLocations.isNotEmpty()) {
                    item {
                        Text("Saved Locations", style = MaterialTheme.typography.titleMedium, color = OnWeatherSurfaceDim)
                    }
                    items(savedLocations, key = { it.id }) { location ->
                        LocationRow(location = location, onRemove = { viewModel.removeLocation(location.id) })
                    }
                }
            }
        }
    }
}

@Composable
fun SettingsScreenPreview(units: Units, onUnitsChange: (Units) -> Unit) {
    UnitsSection(currentUnits = units, onUnitsChange = onUnitsChange)
}

@Composable
private fun UnitsSection(currentUnits: Units, onUnitsChange: (Units) -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text("Units", style = MaterialTheme.typography.titleMedium, color = OnWeatherSurfaceDim)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Units.entries.forEach { unit ->
                FilterChip(
                    selected = currentUnits == unit,
                    onClick = { onUnitsChange(unit) },
                    label = { Text(unit.label) },
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun LocationRow(location: WeatherLocation, onRemove: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(WeatherCardBackground, shape = MaterialTheme.shapes.medium)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(location.name, style = MaterialTheme.typography.bodyLarge, color = OnWeatherSurface)
            Text(
                "${String.format("%.4f", location.latitude)}, ${String.format("%.4f", location.longitude)}",
                style = MaterialTheme.typography.labelSmall,
                color = OnWeatherSurfaceDim
            )
        }
        IconButton(onClick = onRemove) {
            Icon(Icons.Filled.Delete, contentDescription = "Remove", tint = OnWeatherSurfaceDim)
        }
    }
}
