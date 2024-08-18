package info.sergeikolinichenko.myapplication.utils

import info.sergeikolinichenko.domain.entity.City
import info.sergeikolinichenko.domain.entity.PRECIPITATION
import info.sergeikolinichenko.domain.entity.PRESSURE
import info.sergeikolinichenko.domain.entity.Settings
import info.sergeikolinichenko.domain.entity.TEMPERATURE
import info.sergeikolinichenko.domain.entity.Weather
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

internal val testWeather = Weather(
  temp = "25°C",
  maxTemp = "30°C",
  minTemp = "15°C",
  description = "Sunny",
  condIconUrl = "//cdn.weatherapi.com/weather/64x64/day/116.png"
)

internal val testSettings = Settings(
  temperature = TEMPERATURE.CELSIUS,
  precipitation = PRECIPITATION.MM,
  pressure = PRESSURE.HPA
)