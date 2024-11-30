package info.sergeikolinichenko.myapplication.presentation.stores.search

/** Created by Sergei Kolinichenko on 21.02.2024 at 15:55 (GMT+3) **/

import com.arkivanov.mvikotlin.core.store.Store
import info.sergeikolinichenko.domain.entity.City
import info.sergeikolinichenko.myapplication.presentation.stores.search.SearchStore.Intent
import info.sergeikolinichenko.myapplication.presentation.stores.search.SearchStore.Label
import info.sergeikolinichenko.myapplication.presentation.stores.search.SearchStore.State

interface SearchStore : Store<Intent, State, Label> {

  sealed interface Intent {
    data class OnQueryChanged(val query: String) : Intent
    data class OnClickedCity(val city: City) : Intent
    data object OnClickedClearLine : Intent
    data object OnClickedBack : Intent
    data object OnSearch : Intent
  }

  data class State(
    val query: String,
    val state: SearchState
  ) {
    sealed interface SearchState {
      data object Initial : SearchState
      data object Loading : SearchState
      data object Error : SearchState
      data object Empty : SearchState
//      data class SuccessLoaded(val cities: List<CityDto>) : SearchState
      data class SuccessLoaded(val cities: List<City>) : SearchState
    }
  }

  sealed interface Label {
    data object ClickedBack : Label
    data object ClickedCityItem : Label
  }
}
