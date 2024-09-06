package info.sergeikolinichenko.myapplication.utils

import info.sergeikolinichenko.domain.entity.City
import info.sergeikolinichenko.myapplication.network.dto.CurrentWeatherDto
import info.sergeikolinichenko.myapplication.network.dto.DayForecastDto
import info.sergeikolinichenko.myapplication.network.dto.ForecastDto
import info.sergeikolinichenko.myapplication.network.dto.HourForecastDto

/** Created by Sergei Kolinichenko on 18.06.2024 at 18:46 (GMT+3) **/

// test field HourForecastDto
private var testHourForecastDto = HourForecastDto(
  datetimeEpoch = 10100,
  temp = 10.0f,
  precipProb = 12.0f,
  humidity = 13.0f,
  pressure = 14.0f,
  uvIndex = 15,
  icon = "sunny-day",
  precipType = listOf("rain", "snow")
)

// test field DayForecastDto
private val testDayForecastDto = DayForecastDto(
  datetimeEpoch = 10100,
  temp = 10.0f,
  tempMax = 24.0f,
  tempMin = 10.0f,
  humidity = 12.0f,
  windSpeed = 13.0f,
  windDir = 14.0f,
  pressure = 15.0f,
  uvIndex = 16,
  cloudCover = 17.0f,
  precipProb = 18.0f,
  precip = 40.0f,
  precipType = listOf("rain", "snow"),
  description = "sunny",
  icon = "sunny-day",
  sunrise = 10100,
  sunset = 10100,
  moonrise = 10100,
  moonset = 10100,
  moonPhase = 10.0f,
  hoursForecast = listOf(testHourForecastDto)
)

// test field CurrentWeatherDto
private val testCurrentWeatherDto: CurrentWeatherDto = CurrentWeatherDto(
  datetimeEpoch = 10100,
  temp = 10.0f,
  feelsLike = 10.0f,
  humidity = 12.0f,
  windSpeed = 13.0f,
  windDir = 14.0f,
  pressure = 15.0f,
  precipProb = 16.0f,
  precip = 17.0f,
  precipType = listOf("rain", "snow"),
  uvIndex = 16,
  cloudCover = 17.0f,
  conditions = "sunny",
  icon = "sunny-day"
)

// test field ForecastDto
internal val testForecastDto  = ForecastDto(
  timeZone = "Bulgaria/Sofia",
  currentWeatherDto = testCurrentWeatherDto,
  daysForecast = listOf(testDayForecastDto)
)

internal val testCity = City(
  id = 1,
  name = "Sofia",
  lat = 42.6975,
  lon = 23.3242,
  country = "Bulgaria",
  region = "Sofia"
)