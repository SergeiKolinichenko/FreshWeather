package info.sergeikolinichenko.myapplication.presentation.screens.favourite.store

/** Created by Sergei Kolinichenko on 21.02.2024 at 15:31 (GMT+3) **/

import android.os.Parcelable
import com.arkivanov.essenty.instancekeeper.InstanceKeeper
import com.arkivanov.mvikotlin.core.store.Store
import info.sergeikolinichenko.domain.entity.City
import info.sergeikolinichenko.myapplication.entity.CityFs
import info.sergeikolinichenko.myapplication.presentation.screens.editing.store.EditingFavouritesStore
import info.sergeikolinichenko.myapplication.presentation.screens.favourite.store.FavouriteStore.Intent
import info.sergeikolinichenko.myapplication.presentation.screens.favourite.store.FavouriteStore.Label
import info.sergeikolinichenko.myapplication.presentation.screens.favourite.store.FavouriteStore.State
import kotlinx.parcelize.Parcelize

interface FavouriteStore : Store<Intent, State, Label> {

  sealed interface Intent {
    data object SearchClicked : Intent
    data object ActionMenuClicked : Intent
    data object ClosingActionMenu : Intent
    data object ItemMenuSettingsClicked : Intent
    data object ItemMenuEditingClicked : Intent
    data object ReloadCities: Intent
    data class ReloadWeather(val cities: List<City>) : Intent
    data class ItemCityClicked(val id: Int) : Intent
  }

  @Parcelize
  data class State(
    val cityItems: List<CityItem>,
    val listCitiesLoadedState: ListCitiesLoadedState = ListCitiesLoadedState.Initial,
    val dropDownMenuState: DropDownMenuState = DropDownMenuState.Initial
  ) : Parcelable, InstanceKeeper.Instance {

    @Parcelize
    data class CityItem(
      val city: CityFs,
      val weatherLoadingState: WeatherLoadingState,
    ) : Parcelable

    @Parcelize
    sealed interface ListCitiesLoadedState : Parcelable {
      @Parcelize
      data object Initial : ListCitiesLoadedState

      @Parcelize
      data object Loaded : ListCitiesLoadedState

      @Parcelize
      data object Error : ListCitiesLoadedState
    }

    @Parcelize
    sealed interface WeatherLoadingState : Parcelable {
      @Parcelize
      data object Initial : WeatherLoadingState

      @Parcelize
      data object Loading : WeatherLoadingState

      @Parcelize
      data class Error(
        val cityName: String,
        val codeError: String
      ) : WeatherLoadingState

      @Parcelize
      data class LoadedWeatherLoading(
        val temp: String,
        val maxTemp: String,
        val minTemp: String,
        val description: String,
        val icon: String
      ) : WeatherLoadingState

    }

    @Parcelize
    sealed interface DropDownMenuState : Parcelable {
      @Parcelize
      data object Initial : DropDownMenuState

      @Parcelize
      data object OpenMenu : DropDownMenuState

      @Parcelize
      data object CloseMenu : DropDownMenuState
    }
  }


  sealed interface Label {

    data object OnClickSearch : Label

    data object OnClickItemMenuSettings : Label

    data class OnClickItemMenuEditing(val cities: List<EditingFavouritesStore.State.CityItem>) :
      Label

    data class OnClickCity(val id: Int) : Label
  }
}
