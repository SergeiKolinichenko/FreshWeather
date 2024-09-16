package info.sergeikolinichenko.myapplication.presentation.screens.favourite.store

import android.util.Log
import com.arkivanov.mvikotlin.core.store.Reducer
import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineBootstrapper
import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineExecutor
import info.sergeikolinichenko.domain.entity.City
import info.sergeikolinichenko.domain.entity.Forecast
import info.sergeikolinichenko.domain.usecases.favourite.ChangeFavouriteStateUseCase
import info.sergeikolinichenko.domain.usecases.favourite.GetFavouriteCitiesUseCase
import info.sergeikolinichenko.domain.usecases.forecast.ActWithForecastInDbUseCase
import info.sergeikolinichenko.domain.usecases.forecast.GetForecastsFromNetUseCase
import info.sergeikolinichenko.domain.usecases.search.SearchCitiesUseCase
import info.sergeikolinichenko.myapplication.network.dto.CityDto
import info.sergeikolinichenko.myapplication.presentation.screens.editing.store.EditingStore
import info.sergeikolinichenko.myapplication.utils.DURATION_OF_FORECAST_LIFE_MINUTES
import info.sergeikolinichenko.myapplication.utils.getMinutesDifferenceFromNow
import info.sergeikolinichenko.myapplication.utils.mapCityListToCityFsList
import info.sergeikolinichenko.myapplication.utils.mapToForecastScreenList
import info.sergeikolinichenko.myapplication.utils.mapCityFsListToCityList
import info.sergeikolinichenko.myapplication.utils.mapDtoToCity
import kotlinx.coroutines.launch
import javax.inject.Inject

/** Created by Sergei Kolinichenko on 02.07.2024 at 19:07 (GMT+3) **/
class FavouriteStoreFactory @Inject constructor(
  private val storeFactory: StoreFactory,
  private val getFavouriteCitiesFromDb: GetFavouriteCitiesUseCase,
  private val changeFavouriteStateInDb: ChangeFavouriteStateUseCase,
  private val searchCitiesOnNet: SearchCitiesUseCase,
  private val getForecastFromNet: GetForecastsFromNetUseCase,
  private val actWithForecastInDb: ActWithForecastInDbUseCase
) {
  fun create(): FavouriteStore =
    object : FavouriteStore,
      Store<FavouriteStore.Intent, FavouriteStore.State, FavouriteStore.Label> by storeFactory.create(
        name = "FavouriteStore",
        initialState = FavouriteStore.State(
          citiesState = FavouriteStore.State.CitiesState.Initial,
          forecastState = FavouriteStore.State.ForecastState.Initial,
        ),
        bootstrapper = BootstrapperImpl(),
        executorFactory = ::ExecutorImpl,
        reducer = ReducerImpl
      ) {}

  private sealed interface Action {

    data class FavouriteCitiesLoaded(val cities: List<City>) : Action

    data object FavouriteCitiesLoadedError : Action

  }

  private sealed interface Message {

    data class FavoriteCitiesLoaded(val cities: List<City>) : Message

    data object FavouriteCitiesLoadingError : Message

    data class WeatherLoaded(val listForecasts: List<Forecast>) : Message

    data class WeatherLoadingError(val error: String?) : Message

    data object WeatherLoading : Message

    data object DropDownMenuOpened : Message

    data object DropDownMenuClosed : Message
  }

  private inner class BootstrapperImpl : CoroutineBootstrapper<Action>() {
    override fun invoke() {

      scope.launch {
        getFavouriteCitiesFromDb().collect { result ->

          when {

            result.isSuccess -> {
              result.getOrNull()?.let { cities ->
                cities.forEach { city ->

                  if (city.lat == 0.0 || city.lon == 0.0) {
                    cityInfoAdd(city, changeFavouriteStateInDb, searchCitiesOnNet)
                  }
                }
                dispatch(Action.FavouriteCitiesLoaded(cities))
              }
            }
            result.isFailure -> {
              dispatch(Action.FavouriteCitiesLoadedError)
            }
          }
        }
      }
    }
  }

  private inner class ExecutorImpl : CoroutineExecutor<FavouriteStore.Intent,
      Action,
      FavouriteStore.State,
      Message,
      FavouriteStore.Label>() {

    override fun executeIntent(intent: FavouriteStore.Intent) {

      when (intent) {
        is FavouriteStore.Intent.SearchClicked -> publish(FavouriteStore.Label.OnSearchClicked)
        is FavouriteStore.Intent.ItemCityClicked -> {
          publish(FavouriteStore.Label.OnItemClicked(id = intent.id))
        }
        FavouriteStore.Intent.ActionMenuClicked -> dispatch(Message.DropDownMenuOpened)
        FavouriteStore.Intent.ClosingActionMenu -> dispatch(Message.DropDownMenuClosed)

        FavouriteStore.Intent.ItemMenuSettingsClicked -> {
          publish(FavouriteStore.Label.OnItemMenuSettingsClicked)
          dispatch(Message.DropDownMenuClosed)
        }

        is FavouriteStore.Intent.ReloadWeather -> {

          scope.launch {
            if (state().citiesState is FavouriteStore.State.CitiesState.Loaded) {

              val cities = state().citiesState as FavouriteStore.State.CitiesState.Loaded
              loadForecast(cities.listCities.mapCityFsListToCityList())
            }
          }
        }

        FavouriteStore.Intent.ItemMenuEditingClicked -> {

          if (state().citiesState !is FavouriteStore.State.CitiesState.Loaded) return
          if (state().forecastState !is FavouriteStore.State.ForecastState.Loaded) return

          val cities = state().citiesState as FavouriteStore.State.CitiesState.Loaded
          val forecastState = state().forecastState as FavouriteStore.State.ForecastState.Loaded

          val cityItems = cities.listCities.map { city ->
            EditingStore.State.CityItem(
              id = city.id,
              temp = forecastState.listForecast.first { it.id == city.id }.currentForecast.temp,
              icon = forecastState.listForecast.first { it.id == city.id }.currentForecast.icon
            )
          }
          publish(
            FavouriteStore.Label.OnItemMenuEditingClicked(
              cities = cityItems
            )
          )
          dispatch(Message.DropDownMenuClosed)
        }

        FavouriteStore.Intent.ReloadCities -> {

          scope.launch {

            getFavouriteCitiesFromDb().collect { result ->

              when {
                result.isSuccess -> {
                  result.getOrNull()?.let { cities ->
                    dispatch(Message.FavoriteCitiesLoaded(cities))
                    loadForecast(cities)
                  }
                }

                result.isFailure -> {
                  dispatch(Message.FavouriteCitiesLoadingError)
                }
              }
            }
          }
        }
      }
    }

    override fun executeAction(action: Action) {

      when (action) {

        is Action.FavouriteCitiesLoaded -> {

          scope.launch {

            dispatch(Message.FavoriteCitiesLoaded(action.cities))

            if (state().forecastState == FavouriteStore.State.ForecastState.Initial) {
              loadForecast(action.cities)
            } else if (state().forecastState is FavouriteStore.State.ForecastState.Loaded) {

              val forecastState = state().forecastState as FavouriteStore.State.ForecastState.Loaded
              val forecast = forecastState.listForecast.first()
              val minuteDifference =
                getMinutesDifferenceFromNow(forecast.currentForecast.date, forecast.tzId)

              val numberOfCities = action.cities.size
              val numberOfForecasts = forecastState.listForecast.size

              if (minuteDifference > DURATION_OF_FORECAST_LIFE_MINUTES || numberOfCities != numberOfForecasts) {
                loadForecast(action.cities)
              }
            }

            actWithForecastInDb.getForecastsFromDb().collect { forecasts ->

              if (forecasts.isSuccess) {
                dispatch(Message.WeatherLoaded(forecasts.getOrNull()!!))
              } else if (forecasts.isFailure) {
                dispatch(Message.WeatherLoadingError(forecasts.exceptionOrNull()?.message))
              }
            }

          }
        }

        Action.FavouriteCitiesLoadedError -> dispatch(Message.FavouriteCitiesLoadingError)
      }
    }

    private suspend fun loadForecast(cities: List<City>) {

      dispatch(Message.WeatherLoading)

      val result = getForecastFromNet.invoke(cities)

      when {

        result.isSuccess -> {
          actWithForecastInDb.insertForecastToDb(result.getOrNull()!!)
        }

        result.isFailure -> {
          dispatch(Message.WeatherLoadingError(result.exceptionOrNull()!!.message))
        }
      }
    }
  }

  private object ReducerImpl : Reducer<FavouriteStore.State, Message> {

    override fun FavouriteStore.State.reduce(msg: Message): FavouriteStore.State =
      when (msg) {

        is Message.FavoriteCitiesLoaded -> {

          copy(
            citiesState = FavouriteStore.State.CitiesState.Loaded(
              listCities = msg.cities.mapCityListToCityFsList()
            )
          )
        }

        is Message.WeatherLoaded -> {
          copy(
            forecastState = FavouriteStore.State.ForecastState.Loaded(
              listForecast = msg.listForecasts.mapToForecastScreenList()
            )
          )
        }

        is Message.WeatherLoading -> {
          copy(forecastState = FavouriteStore.State.ForecastState.Loading)
        }

        is Message.WeatherLoadingError -> copy(
          forecastState = FavouriteStore.State.ForecastState.Error(
            errorMessage = msg.error ?: "Something went wrong"
          )
        )

        Message.DropDownMenuClosed ->
          copy(dropDownMenuState = FavouriteStore.State.DropDownMenuState.CloseMenu)

        Message.DropDownMenuOpened ->
          copy(dropDownMenuState = FavouriteStore.State.DropDownMenuState.OpenMenu)

        Message.FavouriteCitiesLoadingError ->
          copy(citiesState = FavouriteStore.State.CitiesState.Error)
      }
  }
}

private suspend fun cityInfoAdd(
  city: City,
  changeFavouriteStateUseCase: ChangeFavouriteStateUseCase,
  searchCities: SearchCitiesUseCase,
) {
  val searchedCity: List<CityDto> = searchCities(city.name)
  if (searchedCity.isNotEmpty()) {
    changeFavouriteStateUseCase.addToFavourite(searchedCity.first().mapDtoToCity())
  }
}