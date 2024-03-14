package info.sergeikolinichenko.myapplication.presentation.screens.favourite

import info.sergeikolinichenko.myapplication.entity.CityScreen
import kotlinx.coroutines.flow.StateFlow

/** Created by Sergei Kolinichenko on 21.02.2024 at 15:11 (GMT+3) **/

interface FavouriteComponent {

  val model: StateFlow<FavouriteStore.State>
  fun onSearchClicked()
  fun onButtonClicked()
  fun onItemClicked(city: CityScreen)
}