package info.sergeikolinichenko.domain.repositories

import info.sergeikolinichenko.domain.entity.City

/** Created by Sergei Kolinichenko on 21.02.2024 at 16:58 (GMT+3) **/

interface SearchRepository {
  suspend fun searchCities(query: String): List<City>
}