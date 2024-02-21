package info.sergeikolinichenko.domain.usecases

import info.sergeikolinichenko.domain.repositories.FavouriteRepository
import javax.inject.Inject

/** Created by Sergei Kolinichenko on 21.02.2024 at 17:01 (GMT+3) **/

class GetFavouriteCitiesUseCase @Inject constructor(
  private val repository: FavouriteRepository
){
  operator fun invoke() = repository.favouriteCities
}