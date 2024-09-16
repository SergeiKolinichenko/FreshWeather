package info.sergeikolinichenko.myapplication.mappers

import info.sergeikolinichenko.myapplication.local.models.CityDbModel
import info.sergeikolinichenko.domain.entity.City
import info.sergeikolinichenko.domain.entity.Forecast
import info.sergeikolinichenko.myapplication.local.models.ForecastDbModel

/** Created by Sergei Kolinichenko on 23.02.2024 at 18:58 (GMT+3) **/

internal fun CityDbModel.mapDbModelToCity() = City(
    id = id,
    name = name,
    country = country,
    region = region,
    lat = lat,
    lon = lon,
  )

internal fun City.mapCityToDbModel() = CityDbModel(
    id = id,
    name = name,
    country = country,
    region = region,
    lat = lat,
    lon = lon,
  )

internal fun List<CityDbModel>.mapListDbModelsToListCities(): List<City> {
  return map { it.mapDbModelToCity() }
}

private fun Forecast.mapForecastToDbModel() = ForecastDbModel(
  id = id,
  tzId = tzId,
  currentForecast = currentForecast,
  upcomingDays = upcomingDays,
  upcomingHours = upcomingHours
)

internal fun List<Forecast>.mapListForecastToListDbModel() = map { it.mapForecastToDbModel() }

private fun ForecastDbModel.mapDbModelToForecast() = Forecast(
  id = id,
  tzId = tzId,
  currentForecast = currentForecast,
  upcomingDays = upcomingDays,
  upcomingHours = upcomingHours
)

internal fun List<ForecastDbModel>.mapListDbModelsToListForecast() = map { it.mapDbModelToForecast() }