package info.sergeikolinichenko.data.mappers

import info.sergeikolinichenko.data.network.dto.CityIdDto
import info.sergeikolinichenko.data.network.dto.CityTzDto
import info.sergeikolinichenko.domain.entity.City

/** Created by Sergei Kolinichenko on 23.02.2024 at 19:18 (GMT+3) **/

const val NON_TIME_ZONE_INFO = "empty_string"

fun CityIdDto.cityIdDtoToCity(): City {
  return City(
    id = idCity,
    name = nameCity,
    country = countryCity,
    region = regionCity,
    idTimeZone = NON_TIME_ZONE_INFO
  )
}

fun CityTzDto.cityTzDtoToCity(id: Int): City {
  return City(
    id = id,
    name = nameCity,
    country = countryCity,
    region = regionCity,
    idTimeZone = idTimeZone
  )
}

fun List<CityIdDto>.toListSearchedCities(): List<City> {
  return map {
    it.cityIdDtoToCity()
  }
}