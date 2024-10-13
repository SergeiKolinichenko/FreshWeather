package info.sergeikolinichenko.myapplication.presentation.components.search

import info.sergeikolinichenko.myapplication.network.dto.CityDto
import info.sergeikolinichenko.myapplication.presentation.stores.search.SearchStore
import kotlinx.coroutines.flow.StateFlow

/** Created by Sergei Kolinichenko on 21.02.2024 at 15:11 (GMT+3) **/

interface SearchComponent {

  val model: StateFlow<SearchStore.State>

  fun onClickedClearLine()

  fun onBackClicked()

  fun onQueryChanged(query: String)

  fun onItemClicked(city: CityDto)

}