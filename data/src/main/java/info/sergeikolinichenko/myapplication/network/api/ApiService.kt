package info.sergeikolinichenko.myapplication.network.api

import info.sergeikolinichenko.myapplication.network.dto.CityDto
import info.sergeikolinichenko.myapplication.network.dto.WeatherDto
import info.sergeikolinichenko.myapplication.network.dto.ForecastDto
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

/** Created by Sergei Kolinichenko on 21.02.2024 at 19:52 (GMT+3) **/

interface ApiService {

    @GET("forecast.json")
  suspend fun getWeather(
    @Query("q") location: String,
    @Query("days") days: Int = ONE_DAY_WEATHER
  ): Response<WeatherDto>

  @GET("forecast.json")
  suspend fun getForecast(
    @Query("q") location: String,
    @Query("days") days: Int = MAX_DAYS_FORECAST
  ): Response<ForecastDto>

  @GET("search.json")
  suspend fun searchCities(
    @Query("q") query: String,
  ): Response<List<CityDto>>

  companion object {
    private const val MAX_DAYS_FORECAST = 7
    private const val ONE_DAY_WEATHER = 1
  }
}