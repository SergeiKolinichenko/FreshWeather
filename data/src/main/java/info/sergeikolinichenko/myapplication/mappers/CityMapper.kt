package info.sergeikolinichenko.myapplication.mappers

import info.sergeikolinichenko.domain.entity.City
import info.sergeikolinichenko.myapplication.network.dto.PlaceDto
import kotlin.math.abs

/** Created by Sergei Kolinichenko on 28.11.2024 at 17:35 (GMT+3) **/

internal fun List<PlaceDto>.mapListFoundDtoToListCity() = map { it.mapFoundDtoToCity() }

private fun PlaceDto.mapFoundDtoToCity() = City(
  id = abs(placeId.hashCode()),
  country = country,
  region = state,
  name = city,
  lat = lat,
  lon = lon
)