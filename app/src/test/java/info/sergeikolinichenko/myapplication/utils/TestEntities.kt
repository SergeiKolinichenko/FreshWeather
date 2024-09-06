package info.sergeikolinichenko.myapplication.utils

import info.sergeikolinichenko.domain.entity.City
import info.sergeikolinichenko.domain.entity.CurrentForecast
import info.sergeikolinichenko.domain.entity.DayForecast
import info.sergeikolinichenko.domain.entity.Forecast
import info.sergeikolinichenko.domain.entity.HourForecast
import info.sergeikolinichenko.myapplication.entity.CityFs

/** Created by Sergei Kolinichenko on 06.07.2024 at 18:39 (GMT+3) **/

internal val testCity = City(
  id = 1,
  name = "Sofia",
  region = "Sofia region",
  country = "Bulgaria",
  lat = 42.697708,
  lon = 23.321867
)
internal val testCityFs = CityFs(
  id = 2,
  name = "Blagoevgrad",
  region = "Blagoevgrad region",
  country = "Bulgaria",
  lat = 42.008333,
  lon = 23.514722
)

private val testCurrentForecast = CurrentForecast(
  date = System.currentTimeMillis(),
  temp = "25°C",
  feelsLike = "25°C",
  cloudCover = 255.0f,
  windDir = 182.0f,
  windSpeed = 10.0f,
  pressure = "1000",
  humidity = 11.0f,
  precipProb = 12.0f,
  precip = "100",
  precipType = listOf("rain"),
  uvIndex = 5,
  conditions = "Sunny",
  icon = "//cdn.weatherapi.com/weather/64x64/day/116.png"
)

private val testDayForecast = DayForecast(
  date = 15555411L,
  temp = "25°C",
  tempMax = "30°C",
  tempMin = "15°C",
  humidity = 15.0f,
  windSpeed = 10.0f,
  windDir = 182.0f,
  pressure = "1000",
  uvIndex = 5,
  cloudCover = 255.0f,
  precipProb = 12.0f,
  precip = "100",
  precipType = listOf("rain"),
  description = "Sunny",
  sunrise = 15555411L,
  sunset = 15555411L,
  moonrise = 15555411L,
  moonset = 15555411L,
  moonPhase = 26.0f,
  icon = "//cdn.weatherapi.com/weather/64x64/day/116.png"
)

private val testHourForecast = HourForecast(
  date = 15555411L,
  temp = "25°C",
  humidity = 15.0f,
  pressure = "1000",
  uvIndex = 5,
  precipProb = 12.0f,
  precipType = listOf("rain"),
  icon = "//cdn.weatherapi.com/weather/64x64/day/116.png"
)

internal val testForecast = Forecast(
  id = 1,
  tzId = "Europe/Sofia",
  currentForecast = testCurrentForecast,
  upcomingDays = listOf(testDayForecast),
  upcomingHours = listOf(testHourForecast)
)