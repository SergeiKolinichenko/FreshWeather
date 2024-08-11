package info.sergeikolinichenko.myapplication.presentation.screens.favourite.component

import info.sergeikolinichenko.myapplication.entity.CityForScreen
import info.sergeikolinichenko.myapplication.presentation.screens.favourite.store.FavouriteStore
import kotlinx.coroutines.flow.StateFlow

/** Created by Sergei Kolinichenko on 21.02.2024 at 15:11 (GMT+3) **/

interface FavouriteComponent {

  val model: StateFlow<FavouriteStore.State>

  fun onSearchClicked()

  fun onActionMenuClicked()

  fun onClosingActionMenu()

  fun reloadWeather()

  fun onItemMenuSettingsClicked()

  fun onItemClicked(id: Int)
}