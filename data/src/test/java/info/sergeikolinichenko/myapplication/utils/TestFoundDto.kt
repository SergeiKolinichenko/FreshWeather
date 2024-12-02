package info.sergeikolinichenko.myapplication.utils

import info.sergeikolinichenko.myapplication.network.dto.FoundDto
import info.sergeikolinichenko.myapplication.network.dto.PlaceDto

/** Created by Sergei Kolinichenko on 30.11.2024 at 16:50 (GMT+3) **/

internal val testPlaceDto = PlaceDto(
  placeId = "hUijjd88393hhdhdhd7",
  city = "London",
  state = "England",
  country = "United Kingdom",
  lat = 51.5074,
  lon = 0.1278
)

internal val testFoundDto = FoundDto(
  found = listOf(testPlaceDto)
)