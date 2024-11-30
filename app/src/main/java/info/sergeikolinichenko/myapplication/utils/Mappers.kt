package info.sergeikolinichenko.myapplication.utils

import info.sergeikolinichenko.domain.entity.City
import info.sergeikolinichenko.domain.entity.CurrentForecast
import info.sergeikolinichenko.domain.entity.DayForecast
import info.sergeikolinichenko.domain.entity.Forecast
import info.sergeikolinichenko.domain.entity.HourForecast
import info.sergeikolinichenko.myapplication.entity.CityFs
import info.sergeikolinichenko.myapplication.entity.CurrentForecastFs
import info.sergeikolinichenko.myapplication.entity.DayForecastFs
import info.sergeikolinichenko.myapplication.entity.ForecastFs
import info.sergeikolinichenko.myapplication.entity.HourForecastFs

/** Created by Sergei Kolinichenko on 11.08.2024 at 17:33 (GMT+3) **/

//internal fun CityDto.mapDtoToCity() = City(
//  id = id,
//  name = placeAddress.city?: placeAddress.town?: placeAddress.village?: "",
//  country = placeAddress.country,
//  region = placeAddress.state?: "",
//  lat = lat.toDouble(),
//  lon = lon.toDouble()
//)

internal fun City.mapCityToCityFs() = CityFs(
  id = id,
  name = name,
  region = region,
  country = country,
  lat = lat,
  lon = lon
)

internal fun List<City>.mapCityListToCityFsList() = map { it.mapCityToCityFs() }

internal fun CityFs.mapCityFaToCity() = City(
  id = id,
  name = name,
  region = region,
  country = country,
  lat = lat,
  lon = lon
)

internal fun List<CityFs>.mapCityFsListToCityList() = map { it.mapCityFaToCity() }

internal fun Forecast.mapToForecastScreen() = ForecastFs(
  id = id,
  tzId = tzId,
  currentForecast = currentForecast.mapToCurrentForecastScreen(),
  upcomingDays = upcomingDays.map { it.mapToDayForecastScreen() },
  upcomingHours = upcomingHours.map { it.mapToHourForecastScreen() }
)

internal fun List<Forecast>.mapToForecastScreenList() = map { it.mapToForecastScreen() }

private fun HourForecast.mapToHourForecastScreen() = HourForecastFs(
  date = date,
  temp = temp,
  icon = icon,
  pressure = pressure,
  humidity = humidity,
  uvIndex = uvIndex,
  precipProb = precipProb,
  precipType = precipType
)

private fun DayForecast.mapToDayForecastScreen() = DayForecastFs(
  date = date,
  temp = temp,
  tempMax = tempMax,
  tempMin = tempMin,
  humidity = humidity,
  windSpeed = windSpeed,
  windDir = windDir,
  pressure = pressure,
  uvIndex = uvIndex,
  cloudCover = cloudCover,
  precipProb = precipProb,
  precip = precip,
  precipType = precipType,
  description = description,
  icon = icon,
  sunrise = sunrise,
  sunset = sunset,
  moonrise = moonrise,
  moonset = moonset,
  moonPhase = moonPhase
)

private fun CurrentForecast.mapToCurrentForecastScreen() = CurrentForecastFs(
  date = date,
  temp = temp,
  feelsLike = feelsLike,
  cloudCover = cloudCover,
  windDir = windDir,
  windSpeed = windSpeed,
  pressure = pressure,
  humidity = humidity,
  precipProb = precipProb,
  precip = precip,
  precipType = precipType,
  uvIndex = uvIndex,
  conditions = conditions,
  icon = icon
)

