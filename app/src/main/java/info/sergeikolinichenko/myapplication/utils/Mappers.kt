package info.sergeikolinichenko.myapplication.utils

import info.sergeikolinichenko.domain.entity.City
import info.sergeikolinichenko.domain.entity.Weather
import info.sergeikolinichenko.myapplication.entity.CityScreen
import info.sergeikolinichenko.myapplication.entity.WeatherScreen

/** Created by Sergei Kolinichenko on 25.02.2024 at 18:23 (GMT+3) **/

fun City.toCityScreen() = CityScreen(
  id = id,
  name = name,
  region = region,
  country = country,
  url = url
)

fun CityScreen.toCity() = City(
  id = id,
  name = name,
  region = region,
  country = country,
  url = url
)
fun Weather.toWeatherScreen() = WeatherScreen(
  temperature = temperature,
  maxTemp = maxTemp,
  minTemp = minTemp,
  descriptionWeather = descriptionWeather,
  conditionUrl = conditionUrl,
  windSpeed = windSpeed,
  airPressure = airPressure,
  humidity = humidity,
  date = date
)