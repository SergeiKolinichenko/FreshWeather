package info.sergeikolinichenko.myapplication.repositories

import android.util.Log
import info.sergeikolinichenko.myapplication.mappers.toListCities
import info.sergeikolinichenko.myapplication.network.api.ApiService
import info.sergeikolinichenko.domain.entity.City
import info.sergeikolinichenko.domain.repositories.SearchRepository
import info.sergeikolinichenko.myapplication.network.api.ApiFactory
import javax.inject.Inject

class SearchRepositoryImpl @Inject constructor(
  private val apiFactory: ApiFactory
) : SearchRepository {

  override suspend fun searchCities(query: String): List<City> {

    val response = apiFactory.getWeatherapiApi().searchCities(query)

    if (!response.isSuccessful) {
      throw Exception("Error while searching cities")
    } else {
      return response.body()!!.toListCities()
    }
  }
}