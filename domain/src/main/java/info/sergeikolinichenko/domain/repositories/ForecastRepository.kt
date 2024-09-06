package info.sergeikolinichenko.domain.repositories

import info.sergeikolinichenko.domain.entity.City
import info.sergeikolinichenko.domain.entity.Forecast

/** Created by Sergei Kolinichenko on 21.02.2024 at 16:57 (GMT+3) **/

interface ForecastRepository {
  suspend fun getForecast(cities: List<City>): Result<List<Forecast>>
}