# Weather App — Build Plan

## Overview

A personal Android weather app with beautiful widgets and an app UI modeled after the classic Google Weather app (pre-search-only era).

---

## Tech Stack

| Component | Choice |
|---|---|
| Framework | Native Kotlin + Jetpack Compose |
| Weather API | Open-Meteo (free, no API key required) |
| Storage | DataStore (no database) |
| Background work | WorkManager |

---

## Features

### Widgets
- **2x1 small** — Temperature + condition icon
- **4x1 bar** — Location + temperature + condition icon + feels-like or high/low
- **4x2 medium** — Current conditions + hourly forecast strip
- **4x4 large** — Full current conditions + daily forecast + location
- All widgets are resizable
- Widgets refresh every 60 minutes via WorkManager background job
- Opening the app triggers a fresh fetch that also updates widgets

### App UI
Single scrollable screen per location:
1. **Hero section** — Large current temperature, condition, high/low, location name, animated background
2. **Hourly forecast** — Scrollable horizontal strip, next 24-48 hours
3. **Daily forecast** — Next 7-10 days with high/low
4. **Detail cards** — Humidity, wind, UV index, visibility, pressure, sunrise/sunset, feels-like, AQI

### Animated Backgrounds
- Condition and time-of-day aware (clear day, overcast, rain, snow, night, sunrise/sunset, etc.)
- Stylized/illustrative aesthetic — deep rich gradients (iOS Weather style)
- Subtle particle animations (drifting elements, light rain particles, etc.)
- No full scene animations (battery/performance tradeoff)

### Location
- GPS current location (always first)
- Up to ~5-6 manually saved locations
- Swipe between locations (matching classic Google Weather UX)

---

## Data

### Weather API: Open-Meteo
- No API key, no rate limits, free forever
- Single call returns: current, hourly, daily, precipitation, UV, wind, AQI
- Supports Imperial and Metric natively

### Data displayed
| Field | Widget | App |
|---|---|---|
| Temperature | Yes | Yes |
| Condition / icon | Yes | Yes |
| High / Low | Some widgets | Yes |
| Hourly forecast | Medium + Large widgets | Yes |
| Daily forecast | Large widget | Yes |
| Humidity | No | Yes |
| Wind speed + direction | No | Yes |
| UV Index | No | Yes |
| Visibility | No | Yes |
| Pressure | No | Yes |
| Sunrise / Sunset | No | Yes |
| Feels-like | Some widgets | Yes |
| AQI | No | Yes |

---

## Settings
- Units toggle (Imperial default / Metric)
- Location management (add, remove, reorder saved locations)

---

## Notifications
- None for initial build (architecture should support adding severe weather alerts later)

---

## Visual Identity
- **App name:** Weather
- **App icon:** Simple gradient with sun/cloud, matching in-app art style
- **Art style:** Stylized/illustrative, rich gradients, clean geometric particle effects

---

## Automated Tests

### Unit Tests (`test/`)
- **WeatherRepository** — verify correct API parameters are built (units, lat/lon, fields), verify response parsing into domain models, verify error handling (network failure, malformed response)
- **WeatherConditionMapper** — verify WMO weather code → condition/icon/background mapping for all codes
- **TemperatureFormatter** — verify Imperial/Metric formatting and unit conversion
- **LocationDataStore** — verify save/load/delete of saved locations, verify ordering is preserved
- **WorkManager scheduling** — verify refresh job is enqueued with correct interval constraints

### Instrumented Tests (`androidTest/`)
- **MainScreen** — verify current temp, condition, high/low render with mock data; verify hourly strip scrolls; verify daily forecast row count
- **DetailCards** — verify all 8 detail fields (humidity, wind, UV, visibility, pressure, sunrise/sunset, feels-like, AQI) are displayed
- **SettingsScreen** — verify units toggle switches between °F and °C; verify saved location can be added and removed
- **Widget rendering** — verify each of the 4 widget layouts renders without crash using Glance test utilities

### What is NOT tested
- Animated backgrounds (visual/subjective, not logic)
- GPS location accuracy (device-dependent)
- Actual Open-Meteo API responses (network, not unit tested — repository is tested with mocked responses)

---

## Out of Scope (for now)
- Play Store distribution (personal use only)
- Severe weather alerts / notifications
- iOS version
- Complications / wearable support
