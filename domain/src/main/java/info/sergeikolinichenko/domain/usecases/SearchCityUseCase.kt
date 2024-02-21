package info.sergeikolinichenko.domain.usecases

import info.sergeikolinichenko.domain.repositories.SearchRepository
import javax.inject.Inject

/** Created by Sergei Kolinichenko on 21.02.2024 at 17:10 (GMT+3) **/

class SearchCityUseCase @Inject constructor(
  private val repository: SearchRepository
){
  suspend operator fun invoke(query: String) = repository.searchCenters(query)
}