package info.sergeikolinichenko.myapplication.presentation.stors.search

/** Created by Sergei Kolinichenko on 21.02.2024 at 15:55 (GMT+3) **/

import com.arkivanov.mvikotlin.core.store.Store
import info.sergeikolinichenko.myapplication.network.dto.CityDto
import info.sergeikolinichenko.myapplication.presentation.stors.search.SearchStore.Intent
import info.sergeikolinichenko.myapplication.presentation.stors.search.SearchStore.Label
import info.sergeikolinichenko.myapplication.presentation.stors.search.SearchStore.State

interface SearchStore : Store<Intent, State, Label> {

  sealed interface Intent {
    data class OnQueryChanged(val query: String) : Intent
    data class OnClickedCity(val city: CityDto) : Intent
    data object OnClickedClearLine : Intent
    data object OnClickedBack : Intent
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

      data object NotEnoughLetters: SearchState

      data class SuccessLoaded(val cities: List<CityDto>) : SearchState
    }
  }

  sealed interface Label {
    data object ClickedBack : Label
    data object ClickedCityItem : Label
  }
}
