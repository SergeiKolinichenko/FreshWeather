package info.sergeikolinichenko.myapplication.utils

import info.sergeikolinichenko.domain.entity.City
import info.sergeikolinichenko.myapplication.entity.CityToScreen

/** Created by Sergei Kolinichenko on 25.02.2024 at 18:23 (GMT+3) **/

fun City.toCityToScreen() = CityToScreen(
  id = id,
  name = name,
  region = region,
  country = country,
  url = url
)

fun CityToScreen.toCity() = City(
  id = id,
  name = name,
  region = region,
  country = country,
  url = url
)