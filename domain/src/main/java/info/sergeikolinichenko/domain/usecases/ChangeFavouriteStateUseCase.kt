package info.sergeikolinichenko.domain.usecases

import info.sergeikolinichenko.domain.entity.City
import info.sergeikolinichenko.domain.repositories.FavouriteRepository
import javax.inject.Inject

/** Created by Sergei Kolinichenko on 21.02.2024 at 17:07 (GMT+3) **/

class ChangeFavouriteStateUseCase @Inject constructor(
  private val repository: FavouriteRepository
){

  suspend fun addToFavourite(center: City) = repository.setToFavourite(center)
  suspend fun removeFromFavourite(centerId: Int) = repository.removeFromFavourite(centerId)
}