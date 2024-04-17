package info.sergeikolinichenko.domain.usecases

import info.sergeikolinichenko.domain.repositories.WeatherRepository
import javax.inject.Inject

/** Created by Sergei Kolinichenko on 21.02.2024 at 17:18 (GMT+3) **/

// UseCase for getting forecast by city id
class GerForecastUseCase @Inject constructor(
  private val repository: WeatherRepository
){
  suspend operator fun invoke(id: Int) = repository.getForecast(id)
}