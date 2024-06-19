package info.sergeikolinichenko.myapplication.repositories

import info.sergeikolinichenko.myapplication.mappers.toFavouriteScreenWeather
import info.sergeikolinichenko.myapplication.mappers.toForecast
import info.sergeikolinichenko.myapplication.network.api.ApiService
import info.sergeikolinichenko.domain.entity.Forecast
import info.sergeikolinichenko.domain.entity.CurrentWeather
import info.sergeikolinichenko.domain.repositories.WeatherRepository
import javax.inject.Inject

class WeatherRepositoryImpl @Inject constructor(
  private val apiService: ApiService
) : WeatherRepository {

  override suspend fun getWeather(id: Int): CurrentWeather {
    val response = apiService.getWeather("$PREFIX_CITY_ID$id")
    if (!response.isSuccessful) {
      throw Exception("Error while getting weather")
    } else {
      return response.body()!!.toFavouriteScreenWeather()
    }
  }

  override suspend fun getForecast(id: Int): Forecast {
    val response = apiService.getForecast("$PREFIX_CITY_ID$id")
    if (!response.isSuccessful) {
      throw Exception("Error while getting forecast")
    } else {
      return response.body()!!.toForecast()
    }
  }

  companion object {
    private const val PREFIX_CITY_ID = "id:"
  }
}