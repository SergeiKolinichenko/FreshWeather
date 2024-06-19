package info.sergeikolinichenko.domain.usecases

import info.sergeikolinichenko.domain.repositories.FavouriteRepository
import javax.inject.Inject

/** Created by Sergei Kolinichenko on 21.02.2024 at 17:05 (GMT+3) **/

// This use case is responsible for observing the favourite state of a center
class ObserveFavouriteStateUseCase @Inject constructor(
  private val repository: FavouriteRepository
){
  operator fun invoke(id: Int) = repository.observeIsFavourite(id)
}