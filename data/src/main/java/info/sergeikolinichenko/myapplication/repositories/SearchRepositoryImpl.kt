package info.sergeikolinichenko.myapplication.repositories

import info.sergeikolinichenko.domain.repositories.SearchRepository
import info.sergeikolinichenko.myapplication.network.api.ApiFactory
import javax.inject.Inject

class SearchRepositoryImpl @Inject constructor() : SearchRepository {

  override suspend fun <T> searchCities(query: String): List<T> {
    @Suppress("UNCHECKED_CAST")
    return  ApiFactory.apiServiceOpenStreetMap.searchCities(query).run {
      if (isSuccessful) {
       body()?.filter {
          it.placeAddress.city != null || it.placeAddress.village != null || it.placeAddress.town != null
        } as List<T>
      } else {
        throw Exception("Error while searching cities")
      }
    }
  }
}