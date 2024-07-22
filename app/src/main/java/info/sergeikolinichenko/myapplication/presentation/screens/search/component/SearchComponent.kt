package info.sergeikolinichenko.myapplication.presentation.screens.search.component

import info.sergeikolinichenko.myapplication.entity.CityForScreen
import info.sergeikolinichenko.myapplication.presentation.screens.search.store.SearchStore
import kotlinx.coroutines.flow.StateFlow

/** Created by Sergei Kolinichenko on 21.02.2024 at 15:11 (GMT+3) **/

interface SearchComponent {

  val model: StateFlow<SearchStore.State>

  fun onSearchClicked()

  fun onBackClicked()

  fun onQueryChanged(query: String)

  fun onItemClicked(city: CityForScreen)

}