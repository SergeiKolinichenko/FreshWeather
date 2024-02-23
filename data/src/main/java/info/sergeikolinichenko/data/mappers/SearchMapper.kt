package info.sergeikolinichenko.data.mappers

import info.sergeikolinichenko.data.network.dto.CityDto
import info.sergeikolinichenko.domain.entity.City

/** Created by Sergei Kolinichenko on 23.02.2024 at 19:18 (GMT+3) **/

fun CityDto.toCity(): City {
  return City(
    id = id,
    name = name,
    country = country,
    region = region,
    url = url
  )
}

fun List<CityDto>.toListCities(): List<City> {
  return map { it.toCity() }
}