package info.sergeikolinichenko.myapplication.presentation.screens.details.store

/** Created by Sergei Kolinichenko on 21.02.2024 at 15:26 (GMT+3) **/

import android.util.Log
import com.arkivanov.mvikotlin.core.store.Reducer
import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineBootstrapper
import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineExecutor
import info.sergeikolinichenko.domain.entity.City
import info.sergeikolinichenko.domain.usecases.favourite.GetFavouriteCitiesUseCase
import info.sergeikolinichenko.domain.usecases.forecast.HandleForecastIntoDbUseCase
import info.sergeikolinichenko.domain.usecases.forecast.GetForecastsFromNetUseCase
import info.sergeikolinichenko.myapplication.entity.CityFs
import info.sergeikolinichenko.myapplication.entity.ForecastFs
import info.sergeikolinichenko.myapplication.presentation.screens.details.store.DetailsStore.Intent
import info.sergeikolinichenko.myapplication.presentation.screens.details.store.DetailsStore.Label
import info.sergeikolinichenko.myapplication.presentation.screens.details.store.DetailsStore.State
import info.sergeikolinichenko.myapplication.utils.DURATION_OF_FORECAST_LIFE_MINUTES
import info.sergeikolinichenko.myapplication.utils.getMinutesDifferenceFromNow
import info.sergeikolinichenko.myapplication.utils.mapToForecastScreenList
import info.sergeikolinichenko.myapplication.utils.mapCityToCityFs
import info.sergeikolinichenko.myapplication.utils.mapCityFsListToCityList
import kotlinx.coroutines.launch
import javax.inject.Inject

interface DetailsStore : Store<Intent, State, Label> {

  sealed interface Intent {
    data class OnDayClicked(val id: Int, val index: Int) : Intent
    data object OnBackClicked : Intent
    data object OnSettingsClicked : Intent
    data object ReloadWeather : Intent
    data object OnSwipeLeft : Intent
    data object OnSwipeRight : Intent
  }

  data class State(
    val citiesState: CitiesState,
    val forecastState: ForecastsState,
  ) {

    sealed interface ForecastsState {
      data object Initial : ForecastsState
      data object Loading : ForecastsState
      data class Loaded(val forecasts: List<ForecastFs>) : ForecastsState
      data object Failed : ForecastsState
    }

    sealed interface CitiesState {
      data object Initial : CitiesState
      data class Loaded(val id: Int, val cities: List<CityFs>) : CitiesState
      data object Error : CitiesState
    }
  }

  sealed interface Label {
    data object OnBackClicked : Label
    data object OnSettingsClicked : Label
    data class OnDayClicked(val id: Int, val index: Int) : Label
  }
}

class DetailsStoreFactory @Inject constructor(
  private val storeFactory: StoreFactory,
  private val getForecast: GetForecastsFromNetUseCase,
  private val getFavouriteCities: GetFavouriteCitiesUseCase,
  private val handleForecastIntoDb: HandleForecastIntoDbUseCase
) {

  fun create(id: Int): DetailsStore =
    object : DetailsStore, Store<Intent, State, Label> by storeFactory.create(
      name = "DetailsStore",
      initialState = State(
        citiesState = State.CitiesState.Initial,
        forecastState = State.ForecastsState.Initial
      ),
      bootstrapper = BootstrapperImpl(id),
      executorFactory = ::ExecutorImpl,
      reducer = ReducerImpl
    ) {}

  private sealed interface Action {
    data class CitiesLoaded(val id: Int, val cities: List<CityFs>) : Action
    data object CitiesLoadingFailed : Action
  }

  private sealed interface Message {
    data class CitiesLoaded(val id: Int, val cities: List<CityFs>) : Message
    data object CitiesLoadingFailed : Message
    data class ForecastLoaded(val forecasts: List<ForecastFs>) : Message
    data object ForecastStartLoading : Message
    data class ForecastLoadingFailed(val referrerCode: String) : Message

    data class NewCityId(val id: Int) : Message
  }

  private inner class BootstrapperImpl(
    private val id: Int
  ) : CoroutineBootstrapper<Action>() {

    override fun invoke() {

      scope.launch {

        getFavouriteCities().collect {

          when {
            it.isSuccess -> {
              dispatch(
                Action.CitiesLoaded(
                  id = id,
                  cities = it.getOrNull()!!.map { city -> city.mapCityToCityFs() })
              )
            }

            it.isFailure -> {
              dispatch(Action.CitiesLoadingFailed)
            }
          }
        }
      }
    }
  }

  private inner class ExecutorImpl : CoroutineExecutor<Intent, Action, State, Message, Label>() {

    override fun executeIntent(intent: Intent) {
      when (intent) {

        is Intent.OnBackClicked -> {
          publish(Label.OnBackClicked)
        }

        is Intent.OnDayClicked -> {
          publish(
            Label.OnDayClicked(
              id = intent.id,
              index = intent.index
            )
          )
        }


        Intent.OnSettingsClicked -> publish(Label.OnSettingsClicked)

        Intent.ReloadWeather -> {

          if (state().citiesState is State.CitiesState.Loaded) {

            val citiesState = state().citiesState as State.CitiesState.Loaded
            scope.launch {
              loadForecastFromNet(citiesState.cities.mapCityFsListToCityList())
            }
          }
        }

        Intent.OnSwipeLeft -> {

          if (state().citiesState is State.CitiesState.Loaded) {

            val citiesState = state().citiesState as State.CitiesState.Loaded

            val cityIndex = citiesState.cities.indexOfFirst { it.id == citiesState.id }

            if (cityIndex > 0) {
              scope.launch {
                val id = citiesState.cities[cityIndex - 1].id
                dispatch(Message.NewCityId(id))
              }
            } else {
              publish(Label.OnBackClicked)
            }
          }
        }

        Intent.OnSwipeRight -> {

          if (state().citiesState is State.CitiesState.Loaded) {

            val citiesState = state().citiesState as State.CitiesState.Loaded

            val cityIndex = citiesState.cities.indexOfFirst { it.id == citiesState.id }

            if (cityIndex < citiesState.cities.size - 1) {
              scope.launch {
                val id = citiesState.cities[cityIndex + 1].id
                dispatch(Message.NewCityId(id))
              }
            }
          }
        }
      }
    }

    override fun executeAction(action: Action) {

      when (action) {

        is Action.CitiesLoaded -> {

          dispatch(Message.CitiesLoaded(id = action.id, cities = action.cities))

          val citiesState = state().citiesState as State.CitiesState.Loaded

          if (state().forecastState is State.ForecastsState.Initial) {

            scope.launch {

              handleForecastIntoDb.getForecastsFromDb().collect {

                if (it.isSuccess) {

                  it.getOrNull()?.let { forecasts ->

                    val forecast = forecasts.first { it.id == citiesState.id }

                    val minuteDifference =
                      getMinutesDifferenceFromNow(forecast.currentForecast.date, forecast.tzId)

                    if (minuteDifference > DURATION_OF_FORECAST_LIFE_MINUTES) {
                      scope.launch {
                        loadForecastFromNet(action.cities.mapCityFsListToCityList())
                      }
                    } else {
                      dispatch(Message.ForecastLoaded(forecasts.mapToForecastScreenList()))
                    }
                  }
                }

              }


            }


          }
        }

        Action.CitiesLoadingFailed -> dispatch(Message.CitiesLoadingFailed)
      }
    }

    private suspend fun loadForecastFromNet(cities: List<City>) {

      dispatch(Message.ForecastStartLoading)

      val result = getForecast(cities)

      when {
        result.isSuccess -> {
          val forecast = result.getOrNull()!!
          handleForecastIntoDb.insertForecastToDb(forecast)
        }

        result.isFailure -> {
          val errorCode = result.exceptionOrNull()!!.message!!
          dispatch(Message.ForecastLoadingFailed(errorCode))
        }
      }
    }
  }

  private object ReducerImpl : Reducer<State, Message> {

    override fun State.reduce(msg: Message): State =
      when (msg) {

        is Message.ForecastLoaded ->
          copy(forecastState = State.ForecastsState.Loaded(msg.forecasts))

        is Message.ForecastStartLoading ->
          copy(forecastState = State.ForecastsState.Loading)

        is Message.ForecastLoadingFailed ->
          copy(forecastState = State.ForecastsState.Failed)

        is Message.CitiesLoaded ->
          copy(citiesState = State.CitiesState.Loaded(msg.id, msg.cities))

        Message.CitiesLoadingFailed -> copy(citiesState = State.CitiesState.Error)

        is Message.NewCityId -> {
          val cities = citiesState as State.CitiesState.Loaded
          copy(citiesState = State.CitiesState.Loaded(id = msg.id, cities = cities.cities))
        }
      }
  }
}
