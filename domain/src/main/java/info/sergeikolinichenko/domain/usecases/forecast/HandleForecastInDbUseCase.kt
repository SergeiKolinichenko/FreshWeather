package info.sergeikolinichenko.domain.usecases.forecast

import info.sergeikolinichenko.domain.entity.Forecast
import info.sergeikolinichenko.domain.repositories.ForecastRepository
import javax.inject.Inject

/** Created by Sergei Kolinichenko on 11.09.2024 at 18:48 (GMT+3) **/

class HandleForecastInDbUseCase@Inject constructor(
  private val repository: ForecastRepository
) {

  fun getForecastsFromDb() = repository.getForecastsFromDb

  suspend fun insertForecastToDb(forecasts: List<Forecast>) = repository.insertForecastsToDb(forecasts)
}