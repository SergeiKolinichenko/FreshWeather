package info.sergeikolinichenko.myapplication.repositories

import info.sergeikolinichenko.domain.entity.City
import info.sergeikolinichenko.domain.repositories.SearchRepository
import info.sergeikolinichenko.myapplication.mappers.mapListFoundDtoToListCity
import info.sergeikolinichenko.myapplication.network.api.ApiFactory
import javax.inject.Inject

class SearchRepositoryImpl @Inject constructor() : SearchRepository {

  override suspend fun searchCities(query: String): List<City> {
    return ApiFactory.apiServiceSearch.search(query).run {
      if (isSuccessful) {
        body()?.found?.mapListFoundDtoToListCity() ?: emptyList()
      } else {
        throw Exception("Error while searching cities")
      }
    }
  }
}