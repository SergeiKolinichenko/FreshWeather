package info.sergeikolinichenko.domain.repositories

import info.sergeikolinichenko.domain.entity.CurrentWeather
import info.sergeikolinichenko.domain.entity.Forecast

/** Created by Sergei Kolinichenko on 21.02.2024 at 16:57 (GMT+3) **/

interface WeatherRepository {
  suspend fun getWeather(id: Int): CurrentWeather
  suspend fun getForecast(id: Int): Forecast
}