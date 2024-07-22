package info.sergeikolinichenko.domain.usecases.weather

import info.sergeikolinichenko.domain.repositories.WeatherRepository
import javax.inject.Inject

/** Created by Sergei Kolinichenko on 21.02.2024 at 17:15 (GMT+3) **/

//
class GetWeatherUseCase @Inject constructor(
  private val repository: WeatherRepository
){
  suspend operator fun invoke(id: Int) = repository.getWeather(id)
}