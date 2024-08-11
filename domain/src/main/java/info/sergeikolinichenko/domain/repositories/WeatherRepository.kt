package info.sergeikolinichenko.domain.repositories

import info.sergeikolinichenko.domain.entity.City
import info.sergeikolinichenko.domain.entity.Weather
import info.sergeikolinichenko.domain.entity.Forecast
import kotlinx.coroutines.flow.Flow

/** Created by Sergei Kolinichenko on 21.02.2024 at 16:57 (GMT+3) **/

interface WeatherRepository {
  suspend fun getWeather(city: City): Result<Weather>
  suspend fun getForecast(city: City): Result<Forecast>
}