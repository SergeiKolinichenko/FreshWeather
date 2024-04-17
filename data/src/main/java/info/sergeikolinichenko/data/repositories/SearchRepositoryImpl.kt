package info.sergeikolinichenko.data.repositories

import info.sergeikolinichenko.data.mappers.cityTzDtoToCity
import info.sergeikolinichenko.data.mappers.toListSearchedCities
import info.sergeikolinichenko.data.network.api.ApiService
import info.sergeikolinichenko.domain.entity.City
import info.sergeikolinichenko.domain.repositories.SearchRepository
import javax.inject.Inject

class SearchRepositoryImpl @Inject constructor(
  private val apiService: ApiService
) : SearchRepository {
  override suspend fun searchCities(query: String): List<City> {

    val response = apiService.searchCities(query)

    if (!response.isSuccessful) {
      throw Exception("Error while searching cities")
    } else {
      return response.body()!!.toListSearchedCities()
    }
  }
  override suspend fun getCityInfo(city: City): City {

    val response = apiService.getCityInfo(city.name)

    if (!response.isSuccessful) {
      throw Exception("Error while getting city info")
    } else {
      val body = response.body()?.locationInfo
      return  body?.cityTzDtoToCity(city.id) ?: city
    }
  }
}