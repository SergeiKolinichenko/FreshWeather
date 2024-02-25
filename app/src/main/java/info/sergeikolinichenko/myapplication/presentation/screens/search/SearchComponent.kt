package info.sergeikolinichenko.myapplication.presentation.screens.search

import info.sergeikolinichenko.domain.entity.City
import kotlinx.coroutines.flow.StateFlow

/** Created by Sergei Kolinichenko on 21.02.2024 at 15:11 (GMT+3) **/

interface SearchComponent {

  val model: StateFlow<SearchStore.State>
  fun onSearchClicked()
  fun onBackClicked()
  fun onQueryChanged(query: String)
  fun onItemClicked(city: City)
}