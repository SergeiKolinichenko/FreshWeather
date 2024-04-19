package info.sergeikolinichenko.myapplication.utils

import info.sergeikolinichenko.domain.entity.City
import info.sergeikolinichenko.domain.entity.ForecastHourly
import info.sergeikolinichenko.myapplication.R
import info.sergeikolinichenko.myapplication.entity.CityScreen
import info.sergeikolinichenko.myapplication.entity.WeatherScreen

/** Created by Sergei Kolinichenko on 25.02.2024 at 18:23 (GMT+3) **/

fun City.toCityScreen() = CityScreen(
  id = id,
  name = name,
  region = region,
  country = country
)

fun CityScreen.toCity() = City(
  id = id,
  name = name,
  region = region,
  country = country
)

fun List<ForecastHourly>.toListHourlyWeatherScreen() = map { it.toHourlyWeatherScreen() }
fun ForecastHourly.toHourlyWeatherScreen() = WeatherScreen(
  date = date,
  temperature = tempC,
  maxTemp = maxTempC,
  minTemp = minTempC,
  descriptionWeather = descriptionText,
  conditionUrl = condIconUrl,
  windSpeed = windKph,
  windDirection = windDir,
  airPressure = pressureMb,
  humidity = humidity,
  uv = uv
)
fun Float.fromKphToStringId() = when {
  this < 0.2 -> R.string.beaufort_scale_calm
  this > 0.2 && this <= 1.5 -> R.string.beaufort_scale_light_air
  this > 1.5 && this <= 3.3 -> R.string.beaufort_scale_light_breeze
  this > 3.3 && this <= 5.4 -> R.string.beaufort_scale_gentle_breeze
  this > 5.4 && this <= 7.9 -> R.string.beaufort_scale_moderate_breeze
  this > 7.9 && this <= 10.7 -> R.string.beaufort_scale_fresh_breeze
  this > 10.7 && this <= 13.8 -> R.string.beaufort_scale_strong_breeze
  this > 13.8 && this <= 17.1 -> R.string.beaufort_scale_moderate_gale
  this > 17.1 && this <= 20.7 -> R.string.beaufort_scale_fresh_gale
  this > 20.7 && this <= 24.4 -> R.string.beaufort_scale_strong_gale
  this > 24.4 && this <= 28.4 -> R.string.beaufort_scale_whole_gale
  this > 28.4 && this <= 32.6 -> R.string.beaufort_scale_storm
  this > 32.6 -> R.string.beaufort_scale_hurricane
  else -> -1
}

fun String.toDegree() = when {
  this == "N" -> 0f
  this == "NNE" -> 22.5f
  this == "NE" -> 45f
  this == "ENE" -> 67.5f
  this == "E" -> 90f
  this == "ESE" -> 112.5f
  this == "SE" -> 135f
  this == "SSE" -> 157.5f
  this == "S" -> 180f
  this == "SSW" -> 202.5f
  this == "SW" -> 225f
  this == "WSW" -> 247.5f
  this == "W" -> 270f
  this == "WNW" -> 292.5f
  this == "NW" -> 315f
  this == "NNW" -> 337.5f
  else -> 0f
}