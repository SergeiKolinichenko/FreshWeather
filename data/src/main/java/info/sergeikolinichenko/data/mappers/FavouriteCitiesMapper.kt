package info.sergeikolinichenko.data.mappers

import info.sergeikolinichenko.data.local.models.CityDbModel
import info.sergeikolinichenko.domain.entity.City

/** Created by Sergei Kolinichenko on 23.02.2024 at 18:58 (GMT+3) **/

fun CityDbModel.toCity(): City {
  return City(
    id = id,
    name = name,
    country = country,
    region = region,
    url = url
  )
}

fun City.toCityDbModel(): CityDbModel {
  return CityDbModel(
    id = id,
    name = name,
    country = country,
    region = region,
    url = url
  )
}

fun List<CityDbModel>.toListCities(): List<City> {
  return map { it.toCity() }
}