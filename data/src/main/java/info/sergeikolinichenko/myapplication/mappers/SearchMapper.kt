package info.sergeikolinichenko.myapplication.mappers

import info.sergeikolinichenko.myapplication.network.dto.CityDto
import info.sergeikolinichenko.domain.entity.City

/** Created by Sergei Kolinichenko on 23.02.2024 at 19:18 (GMT+3) **/
fun CityDto.toCity(): City {
  return City(
    id = idCity,
    name = nameCity,
    country = countryCity,
    region = regionCity
  )
}
fun List<CityDto>.toListCities(): List<City> {
  return map {
    it.toCity()
  }
}