package info.sergeikolinichenko.myapplication.mappers

import info.sergeikolinichenko.myapplication.local.models.CityDbModel
import info.sergeikolinichenko.domain.entity.City

/** Created by Sergei Kolinichenko on 23.02.2024 at 18:58 (GMT+3) **/

fun CityDbModel.toCity(): City {
  return City(
    id = id,
    name = name,
    country = country,
    region = region,
    lat = lat,
    lon = lon,
  )
}
fun City.toCityDbModel(): CityDbModel {
  return CityDbModel(
    id = id,
    name = name,
    country = country,
    region = region,
    lat = lat,
    lon = lon,
  )
}
fun List<CityDbModel>.toListFavouriteCities(): List<City> {
  return map { it.toCity() }
}