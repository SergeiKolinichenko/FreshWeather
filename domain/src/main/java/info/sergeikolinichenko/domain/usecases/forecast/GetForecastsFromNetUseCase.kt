package info.sergeikolinichenko.domain.usecases.forecast

import info.sergeikolinichenko.domain.entity.City
import info.sergeikolinichenko.domain.repositories.ForecastRepository
import javax.inject.Inject

/** Created by Sergei Kolinichenko on 21.02.2024 at 17:18 (GMT+3) **/

// UseCase for getting forecast by city id
class GetForecastsFromNetUseCase @Inject constructor(
  private val repository: ForecastRepository
){
  suspend operator fun invoke(cities: List<City>) = repository.getForecastsFromNet(cities)
}