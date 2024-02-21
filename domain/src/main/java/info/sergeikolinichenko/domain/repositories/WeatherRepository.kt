package info.sergeikolinichenko.domain.repositories

import info.sergeikolinichenko.domain.entity.Forecast
import info.sergeikolinichenko.domain.entity.Weather

/** Created by Sergei Kolinichenko on 21.02.2024 at 16:57 (GMT+3) **/

interface WeatherRepository {
  suspend fun getWeather(id: Int): Weather
  suspend fun getForecast(id: Int): Forecast
}