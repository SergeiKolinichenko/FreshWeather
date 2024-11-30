package info.sergeikolinichenko.myapplication.presentation.stores.details

/** Created by Sergei Kolinichenko on 21.02.2024 at 15:26 (GMT+3) **/

import com.arkivanov.mvikotlin.core.store.Reducer
import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineBootstrapper
import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineExecutor
import info.sergeikolinichenko.domain.entity.City
import info.sergeikolinichenko.domain.usecases.favourite.GetFavouriteCitiesUseCase
import info.sergeikolinichenko.domain.usecases.forecast.HandleForecastInDbUseCase
import info.sergeikolinichenko.domain.usecases.forecast.GetForecastsFromNetUseCase
import info.sergeikolinichenko.myapplication.entity.CityFs
import info.sergeikolinichenko.myapplication.entity.ForecastFs
import info.sergeikolinichenko.myapplication.presentation.stores.details.DetailsStore.Intent
import info.sergeikolinichenko.myapplication.presentation.stores.details.DetailsStore.Label
import info.sergeikolinichenko.myapplication.presentation.stores.details.DetailsStore.State
import info.sergeikolinichenko.myapplication.utils.DoNeedNewOne
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
      data class Loaded(val forecasts: List<ForecastFs>) : ForecastsState
      data object LoadingFailed : ForecastsState
    }

    sealed interface CitiesState {
      data object Initial : CitiesState
      data class Loaded(val id: Int, val cities: List<CityFs>) : CitiesState
      data object LoadingFailed : CitiesState
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
  private val getForecastsFromNet: GetForecastsFromNetUseCase,
  private val getFavouriteCities: GetFavouriteCitiesUseCase,
  private val handleForecastIntoDb: HandleForecastInDbUseCase,
  private val doNeedNewOne: DoNeedNewOne
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
              handleForecastIntoDb.getForecastsFromDb().collect { dbForecast ->
                if (dbForecast.isSuccess) {
                  dbForecast.getOrNull()?.let { forecasts ->
                    val forecast = forecasts.first { it.id == citiesState.id }
                    val ifForecastCorrect =
                      doNeedNewOne.invoke(forecast.currentForecast.date, forecast.tzId)
                    if (ifForecastCorrect) {
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

      val result = getForecastsFromNet(cities)
      when {
        result.isSuccess -> {
          val forecast = result.getOrNull()
          if (forecast != null) {
            println("true")
            handleForecastIntoDb.insertForecastToDb(forecast)
          } else {
            println("falce")
            dispatch(Message.ForecastLoadingFailed("unexpected state loading"))
          }
        }
        result.isFailure -> {
          val errorCode = result.exceptionOrNull()?.message
          if (errorCode != null) {
            dispatch(Message.ForecastLoadingFailed(errorCode))
          } else {
            dispatch(Message.ForecastLoadingFailed("unexpected state exception"))
          }
        }
      }
    }
  }

  private object ReducerImpl : Reducer<State, Message> {

    override fun State.reduce(msg: Message): State =
      when (msg) {

        is Message.ForecastLoaded ->
          copy(forecastState = State.ForecastsState.Loaded(msg.forecasts))

        is Message.ForecastLoadingFailed ->
          copy(forecastState = State.ForecastsState.LoadingFailed)

        is Message.CitiesLoaded ->
          copy(citiesState = State.CitiesState.Loaded(msg.id, msg.cities))

        Message.CitiesLoadingFailed -> copy(citiesState = State.CitiesState.LoadingFailed)

        is Message.NewCityId -> {
          val cities = citiesState as State.CitiesState.Loaded
          copy(citiesState = State.CitiesState.Loaded(id = msg.id, cities = cities.cities))
        }
      }
  }
}
