package info.sergeikolinichenko.myapplication.utils

import info.sergeikolinichenko.domain.entity.City
import info.sergeikolinichenko.myapplication.network.dto.CityDto
import info.sergeikolinichenko.myapplication.network.dto.PlaceAddressDto

/** Created by Sergei Kolinichenko on 19.06.2024 at 14:48 (GMT+3) **/

private val placeAddressDto = PlaceAddressDto(
  state = "England",
  country = "United Kingdom",
  city = "London",
  village = "village",
  town = "town",
)

internal val cityDto =
  CityDto(
    id = 1,
    displayName = "London, England, United Kingdom",
    lat = "51.5074",
    lon = "0.1278",
    classType = "classType 1",
    type = "type 1",
    placeAddress = placeAddressDto
  )

internal fun CityDto.mapToCity() = City(
  id = id,
  name = placeAddress.city?: placeAddress.town?: placeAddress.village?: "",
  country = placeAddress.country,
  region = placeAddress.state?: "",
  lat = lat.toDouble(),
  lon = lon.toDouble()
)