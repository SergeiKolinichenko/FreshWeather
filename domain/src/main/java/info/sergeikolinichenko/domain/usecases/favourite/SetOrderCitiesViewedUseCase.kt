package info.sergeikolinichenko.domain.usecases.favourite

import info.sergeikolinichenko.domain.repositories.FavouriteRepository
import javax.inject.Inject

/** Created by Sergei Kolinichenko on 12.07.2024 at 20:26 (GMT+3) **/

class SetOrderCitiesViewedUseCase @Inject constructor(
    private val repository: FavouriteRepository
) {

  operator fun invoke(cities: List<Int>)  = repository.setOrderCitiesViewed(cities)

}