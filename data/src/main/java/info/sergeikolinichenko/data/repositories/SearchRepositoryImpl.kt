package info.sergeikolinichenko.data.repositories

import info.sergeikolinichenko.data.mappers.toListCities
import info.sergeikolinichenko.data.network.api.ApiService
import info.sergeikolinichenko.domain.entity.City
import info.sergeikolinichenko.domain.repositories.SearchRepository
import javax.inject.Inject

class SearchRepositoryImpl @Inject constructor(
  private val apiService: ApiService
) : SearchRepository {
  override suspend fun searchCenters(query: String): List<City> {
    val response = apiService.searchCities(query)
    if (!response.isSuccessful) {
      throw Exception("Error while searching cities")
    } else {
      return response.body()!!.toListCities()
    }
  }
}