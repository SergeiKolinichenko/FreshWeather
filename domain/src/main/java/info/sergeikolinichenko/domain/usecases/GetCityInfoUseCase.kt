package info.sergeikolinichenko.domain.usecases

import info.sergeikolinichenko.domain.entity.City
import info.sergeikolinichenko.domain.repositories.SearchRepository
import javax.inject.Inject

/** Created by Sergei Kolinichenko on 13.04.2024 at 19:15 (GMT+3) **/

class GetCityInfoUseCase @Inject constructor(
  private val repository: SearchRepository) {

  suspend operator fun invoke(city: City) = repository.getCityInfo(city)
}