package info.sergeikolinichenko.myapplication.presentation.stors.favourites

import android.os.Parcelable
import com.arkivanov.essenty.instancekeeper.InstanceKeeper
import com.arkivanov.mvikotlin.core.store.Store
import info.sergeikolinichenko.myapplication.entity.CityFs
import info.sergeikolinichenko.myapplication.entity.ForecastFs
import kotlinx.parcelize.Parcelize

interface FavouriteStore : Store<FavouriteStore.Intent, FavouriteStore.State, FavouriteStore.Label> {

  sealed interface Intent {
    data object SearchClicked : Intent
    data object ActionMenuClicked : Intent
    data object ClosingActionMenu : Intent
    data object ItemMenuSettingsClicked : Intent
    data object ItemMenuEditingClicked : Intent
    data object ReloadCities: Intent
    data object ReloadWeather : Intent
    data class ItemCityClicked(val id: Int) : Intent
  }

  @Parcelize
  data class State(
    val citiesState: CitiesState = CitiesState.Initial,
    val forecastState: ForecastState = ForecastState.Initial,
    val dropDownMenuState: DropDownMenuState = DropDownMenuState.Initial
  ) : Parcelable, InstanceKeeper.Instance {

    @Parcelize
    sealed interface CitiesState : Parcelable {
      @Parcelize
      data object Initial : CitiesState

      @Parcelize
      data object Loading : CitiesState

      @Parcelize
      data class Loaded(val listCities: List<CityFs>) : CitiesState

      @Parcelize
      data object Error : CitiesState
    }

    @Parcelize
    sealed interface ForecastState : Parcelable {

      @Parcelize
      data object Initial : ForecastState

      @Parcelize
      data object Loading : ForecastState

      @Parcelize
      data class Error(val errorMessage: String) : ForecastState

      @Parcelize
      data class Loaded(val listForecast: List<ForecastFs>) : ForecastState
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

    data object OnSearchClicked : Label

    data object OnItemMenuSettingsClicked : Label

    data object OnItemMenuEditingClicked : Label

    data class OnItemClicked(val id: Int) : Label
  }
}