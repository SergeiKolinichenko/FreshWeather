package info.sergeikolinichenko.myapplication.mappers

import info.sergeikolinichenko.domain.entity.CurrentForecast
import info.sergeikolinichenko.domain.entity.DayForecast
import info.sergeikolinichenko.domain.entity.Forecast
import info.sergeikolinichenko.domain.entity.HourForecast
import info.sergeikolinichenko.domain.entity.PRECIPITATION
import info.sergeikolinichenko.domain.entity.PRESSURE
import info.sergeikolinichenko.domain.entity.Settings
import info.sergeikolinichenko.domain.entity.TEMPERATURE
import info.sergeikolinichenko.domain.entity.Weather
import info.sergeikolinichenko.myapplication.network.dto.CurrentWeatherDto
import info.sergeikolinichenko.myapplication.network.dto.DayForecastDto
import info.sergeikolinichenko.myapplication.network.dto.ForecastDto
import info.sergeikolinichenko.myapplication.network.dto.HourForecastDto
import info.sergeikolinichenko.myapplication.utils.tiInchString
import info.sergeikolinichenko.myapplication.utils.toCelsiusString
import info.sergeikolinichenko.myapplication.utils.toFahrenheitString
import info.sergeikolinichenko.myapplication.utils.toHpaString
import info.sergeikolinichenko.myapplication.utils.toMmHgString
import info.sergeikolinichenko.myapplication.utils.toMmsString
import info.sergeikolinichenko.myapplication.utils.toStringFromStringList
import info.sergeikolinichenko.myapplication.utils.toStringListFromString

/** Created by Sergei Kolinichenko on 29.07.2024 at 17:36 (GMT+3) **/

//fun ForecastDto.toForecast()
internal fun ForecastDto.mapForecastDtoToWeather(settings: Settings) = Weather(
  temp = when(settings.temperature) {
    TEMPERATURE.CELSIUS -> this.currentWeatherDto.temp.toCelsiusString()
    TEMPERATURE.FAHRENHEIT -> this.currentWeatherDto.temp.toFahrenheitString()
  },
  maxTemp = when (settings.temperature) {
    TEMPERATURE.CELSIUS -> this.daysForecast.first().tempMax.toCelsiusString()
    TEMPERATURE.FAHRENHEIT -> this.daysForecast.first().tempMax.toFahrenheitString()
  },
  minTemp = when (settings.temperature) {
    TEMPERATURE.CELSIUS -> this.daysForecast.first().tempMin.toCelsiusString()
    TEMPERATURE.FAHRENHEIT -> this.daysForecast.first().tempMin.toFahrenheitString()
  },
  description = this.description,
  condIconUrl = this.daysForecast.first().icon
)

internal fun ForecastDto.mapToForecast(settings: Settings) = Forecast(
  tzId = this.timeZone,
  currentForecast = this.mapToForecastCurrent(settings),
  upcomingDays = this.mapToDaysForecast(settings),
  upcomingHours = this.daysForecast.flatMap { it.mapToHourlyForecast(settings) }
)

private fun ForecastDto.mapToForecastCurrent(settings: Settings) = CurrentForecast(
  date = this.currentWeatherDto.datetimeEpoch,
  temp = when(settings.temperature) {
    TEMPERATURE.CELSIUS -> this.currentWeatherDto.temp.toCelsiusString()
    TEMPERATURE.FAHRENHEIT -> this.currentWeatherDto.temp.toFahrenheitString()
  },
  feelsLike = when(settings.temperature) {
    TEMPERATURE.CELSIUS -> this.currentWeatherDto.feelsLike.toCelsiusString()
    TEMPERATURE.FAHRENHEIT -> this.currentWeatherDto.feelsLike.toFahrenheitString()
  },
  humidity = this.currentWeatherDto.humidity,
  windSpeed = this.currentWeatherDto.windSpeed,
  windDir = this.currentWeatherDto.windDir,
  precipProb = this.currentWeatherDto.precipProb,
  precip = when (settings.precipitation) {
    PRECIPITATION.MM -> this.currentWeatherDto.precip.toMmsString()
    PRECIPITATION.INCHES -> this.currentWeatherDto.precip.tiInchString()
  },
  precipType = this.currentWeatherDto.precipType,
  pressure = when (settings.pressure) {
    PRESSURE.MMHG -> this.currentWeatherDto.pressure.toMmHgString()
    PRESSURE.HPA -> this.currentWeatherDto.pressure.toHpaString()
  },
  uvIndex = this.currentWeatherDto.uvIndex,
  cloudCover = this.currentWeatherDto.cloudCover,
  conditions = this.daysForecast.first().description,
  icon = this.currentWeatherDto.icon
)

private fun ForecastDto.mapToDaysForecast(settings: Settings) =
  this.daysForecast.map { it.mapToForecastDay(settings) }

private fun DayForecastDto.mapToForecastDay(settings: Settings) = DayForecast(
  date = this.datetimeEpoch,
  temp = when(settings.temperature) {
    TEMPERATURE.CELSIUS -> this.temp.toCelsiusString()
    TEMPERATURE.FAHRENHEIT -> this.temp.toFahrenheitString()
  },
  tempMax = when (settings.temperature) {
    TEMPERATURE.CELSIUS -> this.tempMax.toCelsiusString()
    TEMPERATURE.FAHRENHEIT -> this.tempMax.toFahrenheitString()
  },
  tempMin = when (settings.temperature) {
    TEMPERATURE.CELSIUS -> this.tempMin.toCelsiusString()
    TEMPERATURE.FAHRENHEIT -> this.tempMin.toFahrenheitString()
  },
  humidity = this.humidity,
  windSpeed = this.windSpeed,
  windDir = this.windDir,
  pressure = when (settings.pressure) {
    PRESSURE.MMHG -> this.pressure.toMmHgString()
    PRESSURE.HPA -> this.pressure.toHpaString()
  },
  uvIndex = this.uvIndex,
  cloudCover = this.cloudCover,
  precipProb = this.precipProb,
  precip = when (settings.precipitation) {
    PRECIPITATION.MM -> this.precip.toMmsString()
    PRECIPITATION.INCHES -> this.precip.tiInchString()
  },
  precipType = this.precipType,
  description = this.description,
  icon = this.icon,
  sunrise = this.sunrise,
  sunset = this.sunset,
  moonrise = this.moonrise,
  moonset = this.moonset,
  moonPhase = this.moonPhase
)

private fun DayForecastDto.mapToHourlyForecast(settings: Settings)
= this.hoursForecast.map { it.mapToForecastHour(settings) }

private fun HourForecastDto.mapToForecastHour(settings: Settings) = HourForecast(
  date = this.datetimeEpoch,
  temp = when(settings.temperature) {
    TEMPERATURE.CELSIUS -> this.temp.toCelsiusString()
    TEMPERATURE.FAHRENHEIT -> this.temp.toFahrenheitString()
  },
  icon = this.icon,
  pressure = when (settings.pressure) {
    PRESSURE.MMHG -> this.pressure.toMmHgString()
    PRESSURE.HPA -> this.pressure.toHpaString()
  },
  humidity = this.humidity,
  uvIndex = this.uvIndex,
  precipProb = this.precipProb,
)