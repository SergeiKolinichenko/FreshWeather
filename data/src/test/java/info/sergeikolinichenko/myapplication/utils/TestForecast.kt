package info.sergeikolinichenko.myapplication.utils

import info.sergeikolinichenko.domain.entity.CurrentForecast
import info.sergeikolinichenko.domain.entity.DayForecast
import info.sergeikolinichenko.domain.entity.Forecast
import info.sergeikolinichenko.domain.entity.HourForecast
import info.sergeikolinichenko.myapplication.local.models.ForecastDbModel

/** Created by Sergei Kolinichenko on 25.11.2024 at 15:55 (GMT+3) **/

private val testCurrentForecast = CurrentForecast(
  date = 10000L,
  temp = "20C°",
  feelsLike= "25C°",
  cloudCover = 50F,
  windDir = 280F,
  windSpeed = 88F,
  pressure = "pressure",
  humidity = 0.8F,
  precipProb = 25F,
  precip = "precip",
  precipType = listOf("precipType"),
  uvIndex = 1,
  conditions = "conditions",
  icon = "icon"
)

private val testDayForecast = DayForecast(
  date = 10000L,
  temp = "20C°",
  tempMax = "25C°",
  tempMin = "10C°",
  humidity = 0.8F,
  windSpeed = 20F,
  windDir = 50F,
  pressure = "pressure",
  uvIndex = 1,
  cloudCover = 50F,
  precipProb = 25F,
  precip = "precip",
  precipType = listOf("precipType"),
  description = "description",
  icon = "icon",
  sunrise = 50L,
  sunset = 50L,
  moonrise = 50L,
  moonset = 50L,
  moonPhase = 1F
)

private val testHourForecast = HourForecast(
  date = 10000L,
  temp = "20C°",
  icon = "icon",
  pressure = "pressure",
  humidity = 0.8F,
  uvIndex = 1,
  precipProb = 25F,
  precipType = listOf("precipType")
)

internal val testForecast = Forecast(
  id = 1,
  tzId = "tzId",
  currentForecast = testCurrentForecast,
  upcomingDays = listOf(testDayForecast),
  upcomingHours = listOf(testHourForecast)
)

val testForecastDbModel = ForecastDbModel(
  id = 1,
  tzId = "tzId",
  currentForecast = testCurrentForecast,
  upcomingDays = listOf(testDayForecast),
  upcomingHours = listOf(testHourForecast)
)
// endregion helper fields