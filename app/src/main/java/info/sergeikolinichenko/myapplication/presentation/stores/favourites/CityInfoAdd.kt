package info.sergeikolinichenko.myapplication.presentation.stores.favourites

import info.sergeikolinichenko.domain.entity.City
import info.sergeikolinichenko.domain.usecases.favourite.ChangeFavouriteStateUseCase
import info.sergeikolinichenko.domain.usecases.search.SearchCitiesUseCase

/** Created by Sergei Kolinichenko on 27.09.2024 at 19:14 (GMT+3) **/

internal suspend fun cityInfoAdd(
  city: City,
  changeFavouriteStateUseCase: ChangeFavouriteStateUseCase,
  searchCities: SearchCitiesUseCase,
) {
  val searchedCity: List<City> = searchCities(city.name)
  if (searchedCity.isNotEmpty()) {
    changeFavouriteStateUseCase.addToFavourite(searchedCity.first())
  }
}