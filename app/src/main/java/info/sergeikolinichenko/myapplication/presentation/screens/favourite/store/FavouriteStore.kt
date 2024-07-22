package info.sergeikolinichenko.myapplication.presentation.screens.favourite.store

/** Created by Sergei Kolinichenko on 21.02.2024 at 15:31 (GMT+3) **/

import com.arkivanov.mvikotlin.core.store.Store
import info.sergeikolinichenko.domain.entity.City
import info.sergeikolinichenko.myapplication.entity.CityForScreen
import info.sergeikolinichenko.myapplication.presentation.screens.favourite.store.FavouriteStore.Intent
import info.sergeikolinichenko.myapplication.presentation.screens.favourite.store.FavouriteStore.Label
import info.sergeikolinichenko.myapplication.presentation.screens.favourite.store.FavouriteStore.State

interface FavouriteStore : Store<Intent, State, Label> {
  sealed interface Intent {
    data object SearchClicked : Intent
    data object ActionMenuClicked : Intent
    data object ClosingActionMenu : Intent
    data object ItemMenuSettingsClicked: Intent
    data class ReloadWeather(val cities: List<City>) : Intent
    data class ItemCityClicked(
      val city: CityForScreen,
      val numberGradient: Int
    ) : Intent
  }

  data class State(
    val cityItems: List<CityItem>,
    val listCitiesLoadedState: ListCitiesLoadedState = ListCitiesLoadedState.Initial,
    val dropDownMenuState: DropDownMenuState = DropDownMenuState.Initial
  ) {

    data class CityItem(
      val city: CityForScreen,
      val weatherLoadingState: WeatherLoadingState,
    )

    sealed interface ListCitiesLoadedState {
      data object Initial : ListCitiesLoadedState
      data object Loaded : ListCitiesLoadedState
      data object Error : ListCitiesLoadedState
    }

    sealed interface WeatherLoadingState {
      data object Initial : WeatherLoadingState
      data object Loading : WeatherLoadingState
      data object Error : WeatherLoadingState
      data class LoadedWeatherLoading(
        val temp: String,
        val maxTemp: String,
        val minTemp: String,
        val description: String,
        val iconUrl: String
      ) : WeatherLoadingState
    }

    sealed interface DropDownMenuState {
      data object Initial : DropDownMenuState
      data object OpenMenu : DropDownMenuState
      data object CloseMenu : DropDownMenuState
    }
  }

  sealed interface Label {
    data object OnClickSearch : Label
    data object OnClickItemMenuSettings : Label
    data class OnClickCity(
      val city: CityForScreen,
      val numberGradient: Int
    ) : Label
  }
}
