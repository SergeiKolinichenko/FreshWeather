package info.sergeikolinichenko.myapplication.utils

import info.sergeikolinichenko.myapplication.network.dto.ConditionDto
import info.sergeikolinichenko.myapplication.network.dto.CurrentDto
import info.sergeikolinichenko.myapplication.network.dto.CurrentWeatherDto
import info.sergeikolinichenko.myapplication.network.dto.DayDto
import info.sergeikolinichenko.myapplication.network.dto.ForecastCurrentDto
import info.sergeikolinichenko.myapplication.network.dto.ForecastDailyDto
import info.sergeikolinichenko.myapplication.network.dto.ForecastDayDto
import info.sergeikolinichenko.myapplication.network.dto.ForecastDto
import info.sergeikolinichenko.myapplication.network.dto.ForecastLocationDto
import info.sergeikolinichenko.myapplication.network.dto.HourDto

/** Created by Sergei Kolinichenko on 18.06.2024 at 18:46 (GMT+3) **/

internal val conditionDto =
  ConditionDto(
    text = "sunny",
    icon = "//cdn.weatherapi.com/weather/64x64/day/116.png"
  )

internal val currentWeatherDto = CurrentWeatherDto(tempC = 10.0f, condition = conditionDto)

internal val currentDto = CurrentDto(current = currentWeatherDto)

internal val forecastLocationDto = ForecastLocationDto(tzId = "Bulgaria/Sofia")

internal val forecastCurrentDto = ForecastCurrentDto(
  lastUpdatedEpoch = 10100,
  tempC = 35.0f,
  feelsLikeC = 35.0f,
  windKph = 10.0f,
  windDir = "N",
  pressureMb = 11.0f,
  precipMm = 12.0f,
  humidity = 13,
  cloud = 14,
  uv = 15.0f,
  condition = conditionDto
)

internal val dayDto = DayDto(
  maxTempC = 20.0f,
  minTempC = 10.0f,
  maxWindKph = 10.0f,
  uv = 15.0f,
  dailyWillTtRain = 1,
  dailyChanceOfSnow = 22,
  dailyWillItSnow = 0,
  dailyChanceOfRain = 55,
  conditionDto = conditionDto
)

private val hourDto = HourDto(
  timeEpoch = 10001,
  tempC = 20.0f,
  windKph = 8.0f,
  pressureMb = 1100.0f,
  humidity = 44,
  windDir = "S",
  willItRain = 0,
  willItSnow = 1,
  chanceOfRain = 55,
  chanceOfSnow = 22,
  uv = 15.0f,
  condition = conditionDto
)

private val forecastDailyDto = ForecastDailyDto(
  dateEpoch = 11000,
  dailyWeather = dayDto,
  hourDtoArray = listOf(hourDto)
)

private val forecastDayDto = ForecastDayDto(
  forecastDay = listOf(forecastDailyDto, forecastDailyDto)
)

internal val forecastDto = ForecastDto(
  location = forecastLocationDto,
  current = forecastCurrentDto,
  forecast = forecastDayDto
)