package info.sergeikolinichenko.domain.repositories

import info.sergeikolinichenko.domain.entity.City
import info.sergeikolinichenko.domain.entity.Forecast
import kotlinx.coroutines.flow.Flow

/** Created by Sergei Kolinichenko on 21.02.2024 at 16:57 (GMT+3) **/

interface ForecastRepository {

  val getForecastsFromDb: Flow<Result<List<Forecast>>>

  suspend fun insertForecastsToDb(forecasts: List<Forecast>)

  suspend fun getForecastsFromNet(cities: List<City>): Result<List<Forecast>>

}