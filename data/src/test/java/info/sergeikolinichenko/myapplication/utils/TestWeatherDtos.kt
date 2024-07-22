package info.sergeikolinichenko.myapplication.utils

import info.sergeikolinichenko.myapplication.network.dto.ConditionDto
import info.sergeikolinichenko.myapplication.network.dto.WeatherDto
import info.sergeikolinichenko.myapplication.network.dto.WeatherCurrentDto
import info.sergeikolinichenko.myapplication.network.dto.ForecastDayDto
import info.sergeikolinichenko.myapplication.network.dto.ForecastCurrentDto
import info.sergeikolinichenko.myapplication.network.dto.ForecastDailyDto
import info.sergeikolinichenko.myapplication.network.dto.ForecastDaysDto
import info.sergeikolinichenko.myapplication.network.dto.ForecastDto
import info.sergeikolinichenko.myapplication.network.dto.ForecastLocationDto
import info.sergeikolinichenko.myapplication.network.dto.ForecastHourDto
import info.sergeikolinichenko.myapplication.network.dto.WeatherDailyDto
import info.sergeikolinichenko.myapplication.network.dto.WeatherDayDto
import info.sergeikolinichenko.myapplication.network.dto.WeatherDaysDto

/** Created by Sergei Kolinichenko on 18.06.2024 at 18:46 (GMT+3) **/

// test class date Weather

internal val testConditionDto =
  ConditionDto(
    text = "sunny",
    icon = "//cdn.weatherapi.com/weather/64x64/day/116.png"
  )
internal val testWeatherCurrentDto = WeatherCurrentDto(
  tempC = 10.0f,
  tempF = 60.0f,
  condition = testConditionDto
)

private val weatherDayDto: WeatherDayDto = WeatherDayDto(
  maxTempC = 20.0f,
  maxTempF = 60.0f,
  minTempC = 15.0f,
  minTempF = 50.0f,
)

private val testWeatherDailyDto = WeatherDailyDto(day = weatherDayDto)

internal val testWeatherDaysDto = WeatherDaysDto(listOf(testWeatherDailyDto))

internal val testWeatherDto = WeatherDto(
  current = testWeatherCurrentDto,
  weather = testWeatherDaysDto
)

// test class date Forecast

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
  condition = testConditionDto
)

internal val forecastDayDto = ForecastDayDto(
  maxTempC = 20.0f,
  minTempC = 10.0f,
  maxWindKph = 10.0f,
  uv = 15.0f,
  dailyWillTtRain = 1,
  dailyChanceOfSnow = 22,
  dailyWillItSnow = 0,
  dailyChanceOfRain = 55,
  conditionDto = testConditionDto
)

private val forecastHourDto = ForecastHourDto(
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
  condition = testConditionDto
)

private val forecastDailyDto = ForecastDailyDto(
  dateEpoch = 11000,
  dailyWeather = forecastDayDto,
  forecastHourDtoArray = listOf(forecastHourDto)
)

private val forecastDaysDto = ForecastDaysDto(
  forecastDay = listOf(forecastDailyDto, forecastDailyDto)
)

internal val forecastDto = ForecastDto(
  location = forecastLocationDto,
  current = forecastCurrentDto,
  forecast = forecastDaysDto
)