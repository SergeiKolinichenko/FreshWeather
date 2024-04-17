package info.sergeikolinichenko.data.network.api

import info.sergeikolinichenko.data.network.dto.CityIdDto
import info.sergeikolinichenko.data.network.dto.LocationInfo
import info.sergeikolinichenko.data.network.dto.WeatherCurrentDto
import info.sergeikolinichenko.data.network.dto.WeatherForecastDto
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

/** Created by Sergei Kolinichenko on 21.02.2024 at 19:52 (GMT+3) **/

interface ApiService {
  @GET("current.json")
  suspend fun getWeather(
    @Query("q") location: String,
  ): Response<WeatherCurrentDto>

  @GET("forecast.json")
  suspend fun getForecast(
    @Query("q") location: String,
    @Query("days") days: Int = MAX_DAYS_FORECAST
  ): Response<WeatherForecastDto>

  @GET("search.json")
  suspend fun searchCities(
    @Query("q") query: String,
  ): Response<List<CityIdDto>>
  @GET("timezone.json")
  suspend fun getCityInfo(
    @Query("q") query: String,
  ): Response<LocationInfo>
  companion object {
    private const val MAX_DAYS_FORECAST = 7
  }
}