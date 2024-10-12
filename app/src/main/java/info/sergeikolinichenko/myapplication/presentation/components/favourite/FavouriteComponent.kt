package info.sergeikolinichenko.myapplication.presentation.components.favourite

import info.sergeikolinichenko.myapplication.presentation.stors.favourites.FavouriteStore
import kotlinx.coroutines.flow.StateFlow

/** Created by Sergei Kolinichenko on 21.02.2024 at 15:11 (GMT+3) **/

interface FavouriteComponent {

  val model: StateFlow<FavouriteStore.State>

  fun onSearchClicked()

  fun onActionMenuClicked()

  fun onClosingActionMenu()

  fun reloadForecast()

  fun reloadCities()

  fun onItemMenuSettingsClicked()

  fun onItemMenuEditingClicked()

  fun onItemClicked(id: Int)
}