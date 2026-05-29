# Weather

A personal Android weather app modeled after the classic Google Weather app. Built with Kotlin and Jetpack Compose.

## Features

- **Current conditions** — temperature, feels like, high/low, humidity, wind, UV index, pressure, visibility, sunrise/sunset, AQI
- **Hourly forecast** — next 24 hours with precipitation probability
- **10-day forecast** — expandable daily rows with hour-by-hour detail
- **Precipitation chart** — shows when rain/snow is expected to start or end
- **Animated backgrounds** — condition and time-of-day aware gradients with subtle particle effects (rain, snow, clouds)
- **Multiple locations** — GPS current location + up to 5 saved locations, swipe between them
- **4 home screen widgets** — 2x1, 4x1 bar, 4x2, and 4x4, all resizable
- **Units** — Imperial (default) or Metric

## Tech Stack

- **Language:** Kotlin
- **UI:** Jetpack Compose + Material 3
- **Widgets:** Glance
- **Weather data:** [Open-Meteo](https://open-meteo.com/) (free, no API key required)
- **Background refresh:** WorkManager (every 60 minutes)
- **Storage:** DataStore

## Installation

Download the latest APK from the [Releases](https://github.com/jives00/weather/releases) page and sideload it onto your device.

## License

[Creative Commons Attribution-NonCommercial 4.0 International](LICENSE)
